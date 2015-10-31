package com.grublr.pojo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.grublr.util.Utils;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by adi on 10/8/15.
 */
public class UserProfile {

    private static final Logger log = Logger.getLogger(UserProfile.class.getName());

    private String userName;
    private String name;
    private String posts;
    private String activePosts;
    private float score;
    private String favs;
    private int badFlags;
    private String following;

    public UserProfile() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosts() {
        return posts;
    }

    public void setPosts(String posts) {
        this.posts = posts;
    }

    public String getActivePosts() {
        return activePosts;
    }

    public void setActivePosts(String activePosts) {
        this.activePosts = activePosts;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getFavs() {
        return favs;
    }

    public void setFavs(String favs) {
        this.favs = favs;
    }

    public int getBadFlags() {
        return badFlags;
    }

    public void setBadFlags(int badFlags) {
        this.badFlags = badFlags;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    @Override
    public String toString() {
        try {
            return Utils.objToString(this);
        } catch (JsonProcessingException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return "Exception occurred in toString()";
    }

}
