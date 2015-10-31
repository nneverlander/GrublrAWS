package com.grublr.rest;

import com.grublr.db.UserProfileDBHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by adi on 10/8/15.
 */
@Path("/profile")
public class UserProfileHandler {

    private static final Logger log = Logger.getLogger(UserProfileHandler.class.getName());

    @POST
    @Produces("text/plain")
    @Path("updateBadFlags")
    public Response updateBadFlags(int flags) {
        try {
            UserProfileDBHandler.getInstance().updateBadFlags(flags);
        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @POST
    @Produces("text/plain")
    @Path("updateFavs")
    public Response updateFavs(String favs) {
        try {
            UserProfileDBHandler.getInstance().updateFavs(favs);
        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @POST
    @Produces("text/plain")
    @Path("updateFollowing")
    public Response updateFollowing(String following) {
        try {
            UserProfileDBHandler.getInstance().updateFollowing(following);
        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @POST
    @Produces("text/plain")
    @Path("updateName")
    public Response updateName(String name) {
        try {
            UserProfileDBHandler.getInstance().updateName(name);
        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @POST
    @Produces("text/plain")
    @Path("updateActivePosts")
    public Response updateActivePosts(String postId) {
        try {
            UserProfileDBHandler.getInstance().updateActivePosts(postId);
        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

}
