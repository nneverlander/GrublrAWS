package com.grublr.core;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.geo.GeoDataManager;
import com.amazonaws.geo.GeoDataManagerConfiguration;
import com.amazonaws.geo.model.GeoPoint;
import com.amazonaws.geo.model.GeoQueryResult;
import com.amazonaws.geo.model.PutPointRequest;
import com.amazonaws.geo.model.QueryRadiusRequest;
import com.amazonaws.geo.model.QueryRadiusResult;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.util.json.JSONException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.grublr.util.Constants;
import com.grublr.util.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by adi on 9/3/15.
 */
public class DynamoDBHandler implements DataStoreHandler {

    private DynamoDBHandler() {

    }

    private static DynamoDBHandler instance;
    private static final Logger log = Logger.getLogger(DynamoDBHandler.class.getName());
    private static final AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient(new InstanceProfileCredentialsProvider());
    private static final GeoDataManagerConfiguration config = new GeoDataManagerConfiguration(dbClient, Constants.DYNAMO_DB_TABLENAME);
    private static final GeoDataManager geoDataManager = new GeoDataManager(config);

    static {
        config.withRangeKeyAttributeName(Constants.UNIQUE_NAME);
        config.withGeohashIndexName(Constants.GEOHASH_INDEX_NAME);
    }

    public static final DynamoDBHandler getInstance() {
        if (instance == null) {
            instance = new DynamoDBHandler();
        }
        return instance;
    }

    @Override
    public void writeData(String associatedImageName, JsonNode jsonData) throws IOException, JSONException {
        long begin = System.currentTimeMillis();
        if (log.isLoggable(Level.INFO)) log.info("Storing metadata");
        try {
            putPoint(associatedImageName, jsonData);
            if (log.isLoggable(Level.INFO))
                log.info("Stored metadata and time taken: " + (System.currentTimeMillis() - begin));
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<JsonNode> readData(JsonNode location) throws IOException, JSONException {
        long begin = System.currentTimeMillis();
        if (log.isLoggable(Level.INFO)) log.info("Getting posts");
        try {
            //GeoSpatial radius search
            List<JsonNode> posts = getPostsInRadius(location);
            if (log.isLoggable(Level.INFO))
                log.info("Got metadata and time taken: " + (System.currentTimeMillis() - begin));
            return posts;
        } catch (Exception e) {
            throw e;
        }
    }

    private void putPoint(String associatedImageName, JsonNode node) throws IOException, JSONException {
        GeoPoint geoPoint = new GeoPoint(node.get(Constants.LATITUDE).doubleValue(), node.get(Constants.LONGITUDE).doubleValue());
        AttributeValue rangeKeyAttributeValue = new AttributeValue().withS(associatedImageName);
        PutPointRequest putPointRequest = new PutPointRequest(geoPoint, rangeKeyAttributeValue);
        Iterator<Map.Entry<String, JsonNode>> iter = node.fields();
        while (iter.hasNext()) {
            Map.Entry<String, JsonNode> entry = iter.next();
            AttributeValue attributeValue = new AttributeValue().withS(entry.getValue().asText());
            putPointRequest.getPutItemRequest().addItemEntry(entry.getKey(), attributeValue);
        }
        geoDataManager.putPoint(putPointRequest);
    }

    private List<JsonNode> getPostsInRadius(JsonNode node) throws IOException, JSONException {
        GeoPoint centerPoint = new GeoPoint(node.get(Constants.LATITUDE).doubleValue(), node.get(Constants.LONGITUDE).doubleValue());
        double radiusInMeter = node.get(Constants.SEARCH_RADIUS_IN_METERS).doubleValue();

        List<String> attributesToGet = new ArrayList<>();
        attributesToGet.add(Constants.NAME);
        attributesToGet.add(Constants.UNIQUE_NAME);
        attributesToGet.add(Constants.DESCRIPTION);

        QueryRadiusRequest queryRadiusRequest = new QueryRadiusRequest(centerPoint, radiusInMeter);
        queryRadiusRequest.getQueryRequest().setAttributesToGet(attributesToGet);
        //queryRadiusRequest.getQueryRequest().withConsistentRead(false);
        QueryRadiusResult result = geoDataManager.queryRadius(queryRadiusRequest);
        return resultToNodes(result);
    }

    private List<JsonNode> resultToNodes(GeoQueryResult geoQueryResult) throws JsonParseException, IOException {
        List<JsonNode> nodes = new ArrayList<>();
        for (Map<String, AttributeValue> item : geoQueryResult.getItem()) {
            JsonNode node = Utils.stringToJson(asJsonString(item));
            nodes.add(node);
        }
        return nodes;
    }

    private static String asJsonString(Map<String, AttributeValue> result) {
        if (log.isLoggable(Level.FINE)) log.fine("In method asJsonString");
        StringBuilder sb = new StringBuilder("{");
        Iterator iter = result.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            if (iter.hasNext()) {
                sb.append("\"" + entry.getKey() + "\"").append(":").append(entry.getValue()).append(",");
            } else {
                sb.append("\"" + entry.getKey() + "\"").append(":").append(entry.getValue()).append("}");
            }
        }
        if (log.isLoggable(Level.FINE)) log.fine("Exited method asJsonString");
        return sb.toString();
    }

    @Override
    public void deleteData(String uniqueName) throws Exception{
        if (log.isLoggable(Level.INFO)) log.info("Deleting posts");
        try {
            Map<String, AttributeValue> key = new HashMap<>(1);
            key.put(Constants.UNIQUE_NAME, new AttributeValue(uniqueName));
            DeleteItemRequest deleteItemRequest = new DeleteItemRequest(Constants.DYNAMO_DB_TABLENAME, key);
            dbClient.deleteItem(deleteItemRequest);
            if (log.isLoggable(Level.INFO)) log.info("Deleted posts");
        } catch (Exception e) {
            throw e;
        }
    }

    /*private void queryRectangle(JSONObject requestObject, PrintWriter out) throws IOException, JSONException {
        GeoPoint minPoint = new GeoPoint(requestObject.getDouble("minLat"), requestObject.getDouble("minLng"));
        GeoPoint maxPoint = new GeoPoint(requestObject.getDouble("maxLat"), requestObject.getDouble("maxLng"));

        List<String> attributesToGet = new ArrayList<String>();
        attributesToGet.add(config.getRangeKeyAttributeName());
        attributesToGet.add(config.getGeoJsonAttributeName());
        attributesToGet.add("schoolName");

        QueryRectangleRequest queryRectangleRequest = new QueryRectangleRequest(minPoint, maxPoint);
        queryRectangleRequest.getQueryRequest().setAttributesToGet(attributesToGet);
        QueryRectangleResult queryRectangleResult = geoDataManager.queryRectangle(queryRectangleRequest);

        resultToNodes(queryRectangleResult, out);
    }

    private void getPoint(JSONObject requestObject, PrintWriter out) throws IOException, JSONException {
        GeoPoint geoPoint = new GeoPoint(requestObject.getDouble("lat"), requestObject.getDouble("lng"));
        AttributeValue rangeKeyAttributeValue = new AttributeValue().withS(requestObject.getString("rangeKey"));

        GetPointRequest getPointRequest = new GetPointRequest(geoPoint, rangeKeyAttributeValue);
        GetPointResult getPointResult = geoDataManager.getPoint(getPointRequest);

        printGetPointRequest(getPointResult, out);
    }

    private void printGetPointRequest(GetPointResult getPointResult, PrintWriter out) throws JsonParseException,
            IOException {
        Map<String, AttributeValue> item = getPointResult.getGetItemResult().getItem();
        String geoJsonString = item.get(config.getGeoJsonAttributeName()).getS();
        JsonParser jsonParser = factory.createJsonParser(geoJsonString);
        JsonNode jsonNode = mapper.readTree(jsonParser);

        double latitude = jsonNode.get("coordinates").get(0).getDoubleValue();
        double longitude = jsonNode.get("coordinates").get(1).getDoubleValue();
        String hashKey = item.get(config.getHashKeyAttributeName()).getN();
        String rangeKey = item.get(config.getRangeKeyAttributeName()).getS();
        String geohash = item.get(config.getGeohashAttributeName()).getN();
        String schoolName = item.get("schoolName").getS();
        String memo = "";
        if (item.containsKey("memo")) {
            memo = item.get("memo").getS();
        }

        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("latitude", Double.toString(latitude));
        resultMap.put("longitude", Double.toString(longitude));
        resultMap.put("hashKey", hashKey);
        resultMap.put("rangeKey", rangeKey);
        resultMap.put("geohash", geohash);
        resultMap.put("schoolName", schoolName);
        resultMap.put("memo", memo);

        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("action", "get-point");
        jsonMap.put("result", resultMap);

        out.println(mapper.writeValueAsString(jsonMap));
        out.flush();
    }

    private void updatePoint(JSONObject requestObject, PrintWriter out) throws IOException, JSONException {
        GeoPoint geoPoint = new GeoPoint(requestObject.getDouble("lat"), requestObject.getDouble("lng"));
        AttributeValue rangeKeyAttributeValue = new AttributeValue().withS(requestObject.getString("rangeKey"));

        String schoolName = requestObject.getString("schoolName");
        AttributeValueUpdate schoolNameValueUpdate = null;

        String memo = requestObject.getString("memo");
        AttributeValueUpdate memoValueUpdate = null;

        if (schoolName == null || schoolName.equalsIgnoreCase("")) {
            schoolNameValueUpdate = new AttributeValueUpdate().withAction(AttributeAction.DELETE);
        } else {
            AttributeValue schoolNameAttributeValue = new AttributeValue().withS(schoolName);
            schoolNameValueUpdate = new AttributeValueUpdate().withAction(AttributeAction.PUT).withValue(
                    schoolNameAttributeValue);
        }

        if (memo == null || memo.equalsIgnoreCase("")) {
            memoValueUpdate = new AttributeValueUpdate().withAction(AttributeAction.DELETE);
        } else {
            AttributeValue memoAttributeValue = new AttributeValue().withS(memo);
            memoValueUpdate = new AttributeValueUpdate().withAction(AttributeAction.PUT).withValue(memoAttributeValue);
        }

        UpdatePointRequest updatePointRequest = new UpdatePointRequest(geoPoint, rangeKeyAttributeValue);
        updatePointRequest.getUpdateItemRequest().addAttributeUpdatesEntry("schoolName", schoolNameValueUpdate);
        updatePointRequest.getUpdateItemRequest().addAttributeUpdatesEntry("memo", memoValueUpdate);

        UpdatePointResult updatePointResult = geoDataManager.updatePoint(updatePointRequest);

        printUpdatePointResult(updatePointResult, out);
    }

    private void printUpdatePointResult(UpdatePointResult updatePointResult, PrintWriter out)
            throws JsonParseException, IOException {

        Map<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("action", "update-point");

        out.println(mapper.writeValueAsString(jsonMap));
        out.flush();
    }

    private void deletePoint(JSONObject requestObject, PrintWriter out) throws IOException, JSONException {
        GeoPoint geoPoint = new GeoPoint(requestObject.getDouble("lat"), requestObject.getDouble("lng"));
        AttributeValue rangeKeyAttributeValue = new AttributeValue().withS(requestObject.getString("rangeKey"));

        DeletePointRequest deletePointRequest = new DeletePointRequest(geoPoint, rangeKeyAttributeValue);
        DeletePointResult deletePointResult = geoDataManager.deletePoint(deletePointRequest);

        printDeletePointResult(deletePointResult, out);
    }

    private void printDeletePointResult(DeletePointResult deletePointResult, PrintWriter out)
            throws JsonParseException, IOException {

        Map<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("action", "delete-point");

        out.println(mapper.writeValueAsString(jsonMap));
        out.flush();
    }*/
}
