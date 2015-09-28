package com.grublr.core;

import com.amazonaws.util.json.JSONException;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.List;

/**
 * Created by adi on 9/1/15.
 */
public interface DataStoreHandler extends DataHandler {

    void writeMetaData(String associatedImageName, JsonNode jsonData) throws IOException, JSONException;

    List<JsonNode> readMetaData(JsonNode inputJson) throws IOException, JSONException;

    void editMetaData(JsonNode entityObj) throws Exception;

    void postTaken(JsonNode entityObj) throws Exception;
}
