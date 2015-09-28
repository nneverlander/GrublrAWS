package com.grublr.util;

/**
 * Created by adi on 9/1/15.
 */
public class Constants {

    // Metadata Constants
    public static final String LOCATION = "location";
    public static final String NAME = "name";
    public static final String UNIQUE_NAME = "unique_name";
    public static final String DESCRIPTION = "desc";
    public static final String URL = "url";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";
    public static final String METADATA = "metadata";
    public static final String FILE = "file";
    public static final String SEARCH_RADIUS_IN_METERS = "radius";
    public static final String COORDINATES = "coordinates";
    public static final String HASHKEY = "hashKey";
    public static final String GEOHASH = "geohash";
    public static final String GEOJSON = "geoJson";

    //Users table constants
    public static final String USERNAME_COL = "userName";
    public static final String PASSWORD_COL = "password";
    public static final String SALT_COL = "salt";

    //Pasword Reset table constants
    public static final String TOKEN_COL = "token";
    public static final String EXPIRY_COL = "expiry";
    public static final String USER_COL = "user";

    //AWS constants
    public static final String S3_BUCKET = "grublr-images";
    public static final String S3_URL = "https://s3.amazonaws.com/";
    public static final String DYNAMO_DB_IMAGE_METADATA_TABLE = "ImageMetadata";
    public static final String DYNAMO_DB_INACTIVE_POSTS_TABLE = "InactivePosts";
    public static final String DYNAMO_DB_USERS_TABLE = "Users";
    public static final String DYNAMO_DB_PASSWORD_RESET_TABLE = "PasswordReset";
    public static final String GLACIER_VAULT = "grublr-images-inactive";
    public static final String GLACIER_ENDPOINT = "https://glacier.us-east-1.amazonaws.com/";

    //App constants
    public static final String APP_PROPERTIES = "app.properties";

    //Other constants
    public static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    public static final String SECURE_RANDOM = "SHA1PRNG";
    public static final java.lang.Object USERNAME_EXISTS = "userNameExists";
    public static final java.lang.Object INVALID_USER = "invalidUser";
    public static final String AUTH_FAILURE = "authFailure";
    public static final double METERS_PER_MILE = 1609.34;
    public static final String DISTANCE = "distance";
    public static final String FORGOT_PASSWORD_URL = ""; //TODO
    public static final String PASSWORD_RESET_URL = ""; //TODO
    public static final String LINK_EXPIRED = "linkExpired";

    // Email Constants
    public static final String PASSWORD_RESET_EMAIL_LINE_1 = "To reset your password for Grublr, please click the following link:";
    public static final String PASSWORD_RESET_EMAIL_LINE_2 = "If you don’t use this link within 1 hour, it will expire. " +
            "To get a new password reset link, visit <a href=" + FORGOT_PASSWORD_URL + ">" + FORGOT_PASSWORD_URL + "</a>";
    public static final String PASSWORD_RESET_EMAIL_LINE_3 = "If you don't want to reset your password, you can ignore this message - someone probably typed in your username or email address by mistake.";
    public static final String EMAIL_SIGNATURE = "Team Grublr";

    public static final String MAIL_SERVER_PROPERTIES = "mail.properties";
    public static final String PASSWORD_RESET_EMAIL_SUBJECT = "Grublr Password Reset";

}
