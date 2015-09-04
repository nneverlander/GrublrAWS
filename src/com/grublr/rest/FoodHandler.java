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
        JsonNode entityObj = Utils.stringToJson(metadata);
        //Store photo in cloud storage
        String name = Utils.generateUniqueString(entityObj.get(Constants.NAME).asText());
        try {
            DataHandlerFactory.getDefaultPhotoHandler().writePhoto(name, ByteStreams.toByteArray(image));
            // Store metadata in data store
            DataHandlerFactory.getDefaultDataStoreHandler().writeData(name, entityObj);
            //return url
            return Response.ok().build();
        } catch (Exception e) {
            log.severe(e.getCause() + e.getMessage() + e.toString());
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("find")
    public Response findFood(String location) {
        Response ret = Response.ok().build();
        try {
            JsonNode locationObj = Utils.stringToJson(location);
            List<JsonNode> posts = DataHandlerFactory.getDefaultDataStoreHandler().readData(locationObj);
            if (posts.isEmpty()) {
                if (log.isLoggable(Level.INFO)) log.info("No posts to show");
                ret = Response.ok().entity("No posts").build();
            } else {
                //Getting images
                for (JsonNode post : posts) {
                    String fileName = post.get(Constants.NAME).asText();
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
            log.severe(e.getCause() + e.getMessage() + e.toString());
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