package com.grublr.rest;

import com.grublr.core.DynamoDBHandler;
import com.grublr.util.Constants;
import java.math.BigInteger;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response;

/**
 * Created by adi on 9/9/15.
 */
public class SessionHandler {

    private static final Logger log = Logger.getLogger(DynamoDBHandler.class.getName());

    @POST
    @Path("signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signUp(@FormParam("username") String userName, @FormParam("password") String password) {
        if (log.isLoggable(Level.INFO)) log.info("Signing up");
        if (userName.trim().length() > 0 && password.trim().length() > 0) {
            userName = userName.trim();
            try {
                boolean userNameExists = checkUserNameExists(userName);
                if (userNameExists) {
                    return Response.ok(Constants.USERNAME_EXISTS).build();
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
    public Response signIn(@FormParam("username") String userName, @FormParam("password") String password) {
        if (log.isLoggable(Level.INFO)) log.info("Signing in");
        if (userName.trim().length() > 0 && password.trim().length() > 0) {
            userName = userName.trim();
            try {
                Map<String, String> retMap = DatabaseConnectorExt.getUserDetails(query);
                URI uri;
                if (retMap.size() > 0) {
                    // Authenticate password here
                    log("Going to compare passwords");
                    if (authenticate(password, fromHex(retMap.get("password")), fromHex(retMap.get("salt")))) {
                        log("passwords match");
                        populateSession(retMap);
                        String userType = request.getSession().getAttribute("userType").toString();
                        if (Integer.parseInt(retMap.get("privLevel").toString().trim()) == 5) { // 5 or above privilege level are for admin
                            uri = UriBuilder.fromUri("../html/signup.html").build();
                        } else {
                            Properties tableProps = Util.getTableProperties();
                            if (userType.equalsIgnoreCase(tableProps.getProperty("GROUP_TYPE"))) {
                                uri = UriBuilder.fromUri("/VidyaMap/html/group_present.html").build();
                            } else {
                                uri = UriBuilder.fromUri("/VidyaMap/html/session.html").build();
                            }
                        }
                        // Initiate logging
                        String logConsent = request.getSession().getAttribute("logConsent").toString();
                        log("Logging consent::" + logConsent);
                        if (logConsent.equalsIgnoreCase("true")) {
                            String fn = request.getSession().getAttribute("firstName").toString();
                            String ln = request.getSession().getAttribute("lastName").toString();
                            pw = LogUtil.logInit(userType, userName, fn, ln);
                        }
                        return Response.temporaryRedirect(uri).build();
                    } else {
                        log("password dont match");
                        uri = UriBuilder.fromUri("/VidyaMap/index.html").build();
                        return Response.temporaryRedirect(uri).build();
                    }
                } else {
                    throw new Exception("DB result set is empty");
                }
            } catch (NoSuchAlgorithmException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            } catch (InvalidKeySpecException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            } catch (Exception e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    // Password handling functions
    private byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        // Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
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
        // log("trying authentication compare");
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
