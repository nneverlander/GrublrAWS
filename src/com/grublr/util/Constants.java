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
    public static final String LONGITUDE = "lon";
    public static final String METADATA = "metadata";
    public static final String FILE = "file";
    public static final String SEARCH_RADIUS_IN_METERS = "radius";

    //Users table constants
    public static final String USERNAME_COL = "userName";
    public static final String PASSWORD_COL = "password";
    public static final String SALT_COL = "salt";


    //AWS constants
    public static final String S3_BUCKET = "grublr-images";
    public static final String S3_URL = "https://s3.amazonaws.com/";
    public static final String DYNAMO_DB_IMAGE_METADATA_TABLE = "ImageMetadata";
    public static final String DYNAMO_DB_IMAGE_USERS_TABLE = "Users";

    //App constants
    public static final String APP_PROPERTIES = "app.properties";

    //Other constants
    public static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    public static final java.lang.Object USERNAME_EXISTS = "userNameExists";
    public static final String AUTH_FAILURE = "authFailure";

}
