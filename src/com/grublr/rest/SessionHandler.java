package com.grublr.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.grublr.core.DynamoDBHandler;
import com.grublr.util.Constants;
import com.grublr.util.Utils;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by adi on 9/9/15.
 */
@Path("/")
public class SessionHandler {

    private static final Logger log = Logger.getLogger(DynamoDBHandler.class.getName());

    @POST
    @Path("signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signUp(String jsonStr) {
        try {
            JsonNode jsonObj = Utils.stringToJson(jsonStr);
            String userName = jsonObj.get(Constants.USERNAME_COL).asText();
            String password = jsonObj.get(Constants.PASSWORD_COL).asText();
            if (log.isLoggable(Level.INFO)) log.info("Signing up");
            userName = userName.trim();
            boolean userNameExists = checkUserNameExists(userName);
            if (userNameExists) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Constants.USERNAME_EXISTS).build();
            }
            // Generate the salt for encrypting password
            byte[] salt = generateSalt();
            byte[] encryptedPassword = getEncryptedPassword(password, salt);
            String saltStr = toHex(salt);
            String encryptPassStr = toHex(encryptedPassword);
            // Insert new user information into the database
            boolean result = DynamoDBHandler.getInstance().addUser(userName, encryptPassStr, saltStr);
            if (result) {
                String retJson = "{ " + Constants.USERNAME_COL + " : " + userName + " }";
                return Response.ok(retJson).build();
            }
        } catch (NoSuchAlgorithmException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } catch (InvalidKeySpecException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    public boolean checkUserNameExists(String userName) throws Exception {
        if (log.isLoggable(Level.INFO)) log.info("Checking if user name already exists");
        try {
            return DynamoDBHandler.getInstance().checkUserNameExists(userName);
        } catch (Exception e) {
            throw e;
        }
    }

    @Path("signout")
    @GET
    public Response signOut() {
        if (log.isLoggable(Level.INFO)) log.info("Signing out");
        return Response.ok().build();
    }

    @Path("signin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signIn(String jsonStr) {
        try {
            JsonNode jsonObj = Utils.stringToJson(jsonStr);
            String userName = jsonObj.get(Constants.USERNAME_COL).asText();
            String password = jsonObj.get(Constants.PASSWORD_COL).asText();
            if (log.isLoggable(Level.INFO)) log.info("Signing in");
            userName = userName.trim();
            Map<String, String> userDetails = DynamoDBHandler.getInstance().getUser(userName);
            if (userDetails.isEmpty()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Constants.AUTH_FAILURE).build();
            }
            // Authenticate password here
            if (log.isLoggable(Level.INFO)) log.info("Comparing passwords");
            boolean isAuthenticated = authenticate(password, fromHex(userDetails.get(Constants.PASSWORD_COL)), fromHex(userDetails.get(Constants.SALT_COL)));
            if (isAuthenticated) {
                if (log.isLoggable(Level.INFO)) log.info("passwords match");
                String retJson = "{ " + Constants.USERNAME_COL + " : " + userName + " }";
                return Response.ok(retJson).build();
            } else {
                if (log.isLoggable(Level.WARNING)) log.warning("passwords don't match");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Constants.AUTH_FAILURE).build();
            }
        } catch (NoSuchAlgorithmException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } catch (InvalidKeySpecException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    // Password handling functions
    private byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance(Constants.SECURE_RANDOM);
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        return salt;
    }

    private byte[] getEncryptedPassword(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        int derivedKeyLength = 160;
        int iterations = 20000;

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations,
                derivedKeyLength);
        SecretKeyFactory f = SecretKeyFactory.getInstance(Constants.ALGORITHM);
        return f.generateSecret(spec).getEncoded();
    }

    private boolean authenticate(String attemptedPassword,
                                 byte[] encryptedPassword, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encryptedAttemptedPassword = getEncryptedPassword(
                attemptedPassword, salt);
        return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
    }

    private static byte[] fromHex(String hex) {
        byte[] binary = new byte[hex.length() / 2];
        for (int i = 0; i < binary.length; i++) {
            binary[i] = (byte) Integer.parseInt(
                    hex.substring(2 * i, 2 * i + 2), 16);
        }
        return binary;
    }

    private static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

}
