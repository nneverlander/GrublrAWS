package com.grublr.db;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by adi on 10/8/15.
 */
public class UserProfileDBHandler {

    private UserProfileDBHandler() {

    }

    private static UserProfileDBHandler instance;
    private static final Logger log = Logger.getLogger(UserProfileDBHandler.class.getName());

    public static final UserProfileDBHandler getInstance() {
        if (instance == null) {
            instance = new UserProfileDBHandler();
        }
        return instance;
    }

    public void updateBadFlags(int flags) {
        try {
            // update score
            updateScore();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void updateScore() {
        try {
            // TODO
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void updateFavs(String favs) {
        try {

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

    }

    public void updateFollowing(String following) {
        try {

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void updateName(String name) {
        try {

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void updateActivePosts(String postId) {
        try {

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

}
