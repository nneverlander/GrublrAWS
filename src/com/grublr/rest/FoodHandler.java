package com.grublr.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.ByteStreams;
import com.grublr.core.DataHandlerFactory;
import com.grublr.util.Constants;
import com.grublr.util.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * Created by adi on 8/31/15.
 */

@Path("/food")
public class FoodHandler {

    //TODO Gzip compression

    private static final Logger log = Logger.getLogger(FoodHandler.class.getName());

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    @Path("share")
    public Response shareFood(@FormDataParam(Constants.METADATA) String metadata, @FormDataParam(Constants.FILE) InputStream image) {
        if (log.isLoggable(Level.INFO)) log.info("Share food req received");
        try {
            JsonNode entityObj = Utils.stringToJson(metadata);
            String uniqueName = Utils.generateUniqueString(entityObj.get(Constants.NAME).asText());
            // Store metadata in data store
            DataHandlerFactory.getDefaultDataStoreHandler().writeData(uniqueName, entityObj);
            try {
                //Store photo in cloud storage
                DataHandlerFactory.getDefaultPhotoHandler().writePhoto(uniqueName, ByteStreams.toByteArray(image));
            } catch (Exception e) {
                //If something goes wrong, delete the associated metadata
                try {
                    DataHandlerFactory.getDefaultDataStoreHandler().deleteData(uniqueName);
                } catch (Exception ex) {
                    log.log(Level.SEVERE, ex.getMessage(), ex);
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
            //return url
            return Response.ok().build();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("find")
    public Response findFood(String location) {
        if (log.isLoggable(Level.INFO)) log.info("Find food req received");
        Response ret = Response.ok().build();
        try {
            JsonNode locationObj = Utils.stringToJson(location);
            List<JsonNode> posts = DataHandlerFactory.getDefaultDataStoreHandler().readData(locationObj);
            if (posts == null || posts.isEmpty()) {
                if (log.isLoggable(Level.INFO)) log.info("No posts to show");
                ret = Response.ok().entity("No posts").build();
            } else {
                //Getting images
                for (JsonNode post : posts) {
                    String fileName = post.get(Constants.UNIQUE_NAME).asText();
                    final byte[] image = DataHandlerFactory.getDefaultPhotoHandler().readPhoto(fileName);
                    StreamingOutput stream = new StreamingOutput() {
                        public void write(OutputStream out) throws IOException {
                            int read = 0;
                            out.write(image);
                        }
                    };
                    Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
                            .header("Content-Disposition", "attachment; filename=" + fileName)
                            .header(Constants.METADATA, post)
                            .build();
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            ret = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.fromResponse(ret).build();
    }

    @GET
    @Path("doof")
    public Response doof() {
        return Response.ok().entity("doof").build();
    }
}