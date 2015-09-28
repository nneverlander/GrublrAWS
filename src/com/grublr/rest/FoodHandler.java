package com.grublr.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.ByteStreams;
import com.grublr.core.DataHandlerFactory;
import com.grublr.util.Constants;
import com.grublr.util.Utils;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.beanutils.PropertyUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * Created by adi on 8/31/15.
 */

@Path("/food")
public class FoodHandler {

    private static final Logger log = Logger.getLogger(FoodHandler.class.getName());

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("share")
    public Response shareFood(@FormDataParam(Constants.METADATA) String metadata, @FormDataParam(Constants.FILE) InputStream image) {
        if (log.isLoggable(Level.INFO)) log.info("Share food req received");
        try {
            JsonNode entityObj = Utils.stringToJson(metadata);
            String uniqueName = Utils.generateUniqueString(entityObj.get(Constants.NAME).asText());
            // Store metadata in data store
            DataHandlerFactory.getDefaultDataStoreHandler().writeMetaData(uniqueName, entityObj);
            try {
                //Store photo in cloud storage
                DataHandlerFactory.getDefaultPhotoHandler().writePhoto(uniqueName, ByteStreams.toByteArray(image));
            } catch (Exception e) {
                //If something goes wrong, delete the associated metadata
                try {
                    DataHandlerFactory.getDefaultDataStoreHandler().deleteData(entityObj);
                } catch (Exception ex) {
                    log.log(Level.SEVERE, ex.getMessage(), ex);
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }

            String retJson = "{ " + Constants.UNIQUE_NAME + ":" + uniqueName + " }";
            return Response.ok(retJson).build();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("edit")
    public Response editPost(@FormDataParam(Constants.METADATA) String metadata, @FormDataParam(Constants.FILE) InputStream image) {
        if (log.isLoggable(Level.INFO)) log.info("Edit post req received");
        try {
            JsonNode entityObj = Utils.stringToJson(metadata);
            String uniqueName = entityObj.get(Constants.UNIQUE_NAME).asText();
            // Edit metadata in data store
            DataHandlerFactory.getDefaultDataStoreHandler().editMetaData(entityObj);
            if (image != null) {
                DataHandlerFactory.getDefaultPhotoHandler().editPhoto(entityObj, ByteStreams.toByteArray(image));
            }

            String retJson = "{ " + Constants.UNIQUE_NAME + ":" + uniqueName + " }";
            return Response.ok(retJson).build();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("editMeta")
    public Response editMeta(String metadata) {
        if (log.isLoggable(Level.INFO)) log.info("Edit meta req received");
        try {
            JsonNode entityObj = Utils.stringToJson(metadata);
            String uniqueName = entityObj.get(Constants.UNIQUE_NAME).asText();
            // Edit metadata in data store
            DataHandlerFactory.getDefaultDataStoreHandler().editMetaData(entityObj);
            String retJson = "{ " + Constants.UNIQUE_NAME + ":" + uniqueName + " }";
            return Response.ok(retJson).build();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("delete")
    public Response deletePost(String jsonStr) {
        if (log.isLoggable(Level.INFO)) log.info("Delete post req received");
        try {
            JsonNode entityObj = Utils.stringToJson(jsonStr);
            String uniqueName = entityObj.get(Constants.UNIQUE_NAME).asText();
            // Delete metadata from data store
            DataHandlerFactory.getDefaultDataStoreHandler().deleteData(entityObj);
            // Delete photo from cloud storage
            DataHandlerFactory.getDefaultPhotoHandler().deleteData(entityObj);

            String retJson = "{ " + Constants.UNIQUE_NAME + ":" + uniqueName + " }";
            return Response.ok(retJson).build();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("taken")
    public Response postTaken(String jsonStr) {
        if (log.isLoggable(Level.INFO)) log.info("Taken post req received");
        try {
            JsonNode entityObj = Utils.stringToJson(jsonStr);
            String uniqueName = entityObj.get(Constants.UNIQUE_NAME).asText();
            // Delete metadata from data store
            DataHandlerFactory.getDefaultDataStoreHandler().postTaken(entityObj);
            // Delete photo from cloud storage
            DataHandlerFactory.getDefaultPhotoHandler().postTaken(entityObj);

            String retJson = "{ " + Constants.UNIQUE_NAME + ":" + uniqueName + " }";
            return Response.ok(retJson).build();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("find")
    public Response findFood(String location) {
        if (log.isLoggable(Level.INFO)) log.info("Find food req received");
        List<GrublrResponse> responseList = new ArrayList<>();
        try {
            JsonNode locationObj = Utils.stringToJson(location);
            List<JsonNode> posts = DataHandlerFactory.getDefaultDataStoreHandler().readMetaData(locationObj);
            if (posts == null || posts.isEmpty()) {
                if (log.isLoggable(Level.INFO)) log.info("No posts to show");
                return Response.ok("No posts").build();
            } else {
                //Getting images and metadata
                for (JsonNode post : posts) {
                    GrublrResponse grublrResponse = new GrublrResponse();
                    String fileName = post.get(Constants.UNIQUE_NAME).asText();
                    final String imageUrl = DataHandlerFactory.getDefaultPhotoHandler().readPhoto(fileName);
                    grublrResponse.setImageUrl(imageUrl);

                    Iterator<Map.Entry<String, JsonNode>> iter = post.fields();
                    while (iter.hasNext()) {
                        Map.Entry<String, JsonNode> entry = iter.next();
                        PropertyUtils.setProperty(grublrResponse, entry.getKey(), entry.getValue().asText());
                    }

                    responseList.add(grublrResponse);
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        GrublrResponses grublrResponses = new GrublrResponses(responseList);
        return Response.ok(grublrResponses).build();
    }

    @GET
    @Path("doof")
    public Response doof() {
        return Response.ok().entity("doof").build();
    }
}