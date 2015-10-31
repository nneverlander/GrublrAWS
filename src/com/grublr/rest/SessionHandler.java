package com.grublr.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.grublr.db.DynamoDBHandler;
import com.grublr.util.Constants;
import com.grublr.util.Utils;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.codec.binary.Base64;

/**
 * Created by adi on 9/9/15.
 */
@Path("/accounts")
public class SessionHandler {

    private static final Logger log = Logger.getLogger(SessionHandler.class.getName());
    private static Properties mailServerProperties = null;
    @Context
    private HttpServletRequest request;
    @Context
    private HttpServletResponse response;

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
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    private boolean checkUserNameExists(String userName) throws Exception {
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
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @Path("checkPassword")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response checkPassword(String jsonStr) {
        if (log.isLoggable(Level.INFO)) log.info("Check password req received");
        try {
            JsonNode node = Utils.stringToJson(jsonStr);
            String userName = node.get(Constants.USERNAME_COL).asText();
            String password = node.get(Constants.PASSWORD_COL).asText();
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
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @Path("resetPassword")
    @POST
    public Response resetPassword(@PathParam("t") String token) {
        if (log.isLoggable(Level.INFO)) log.info("Reset password req received");
        try {
            if (token == null || token.equals("")) {
                return Response.ok(Constants.LINK_EXPIRED).build();
            }
            if (!isLinkAlive(token)) {
                return Response.ok(Constants.LINK_EXPIRED).build();
            }
            // Get user for token
            String user = DynamoDBHandler.getInstance().getUserForToken(token);
            if (user == null) {
                return Response.ok(Constants.LINK_EXPIRED).build();
            }
            request.setAttribute(Constants.USER_COL, user);
            request.setAttribute(Constants.TOKEN_COL, token);
            request.getRequestDispatcher("/jsp/secure/cp.jsp").forward(request, response);
            return Response.ok(user).build();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    private boolean isLinkAlive(String token) {
        return DynamoDBHandler.getInstance().isPasswordTokenValid(token);
    }

    @Path("changePassword")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePassword(String jsonStr) {
        if (log.isLoggable(Level.INFO)) log.info("Change password req received");
        try {
            JsonNode node = Utils.stringToJson(jsonStr);
            String userName = node.get(Constants.USERNAME_COL).asText();
            String password = node.get(Constants.PASSWORD_COL).asText();
            // Generate the salt for encrypting password
            byte[] salt = generateSalt();
            byte[] encryptedPassword = getEncryptedPassword(password, salt);
            String saltStr = toHex(salt);
            String encryptPassStr = toHex(encryptedPassword);
            // Update user information in the database
            boolean result = DynamoDBHandler.getInstance().updateUserPassword(userName, encryptPassStr, saltStr);
            if (result) {
                // If its a reset password, delete token from password reset table
                try {
                    DynamoDBHandler.getInstance().deleteExistingPassWordResetTokens(userName);
                } catch (Exception e) {
                    log.log(Level.SEVERE, e.getMessage(), e);
                }
                String retJson = "{ " + Constants.USERNAME_COL + " : " + userName + " }";
                return Response.ok(retJson).build();
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @Path("forgotPass")
    @GET
    public void forgotPass() {
        if (log.isLoggable(Level.INFO)) log.info("Forgot pass req received");
        try {
            request.getRequestDispatcher("/jsp/fp.jsp").forward(request, response);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Path("forgotPassword")
    @POST
    public Response forgotPassword(String jsonStr) {
        if (log.isLoggable(Level.INFO)) log.info("Forgot password req received");
        try {
            JsonNode node = Utils.stringToJson(jsonStr);
            String userName = node.get(Constants.USERNAME_COL).asText();
            if (!checkUserNameExists(userName)) {
                return Response.ok(Constants.INVALID_USER).build();
            }
            // Check if token already exists and delete if it does
            try {
                DynamoDBHandler.getInstance().deleteExistingPassWordResetTokens(userName);
            } catch (Exception e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }

            String token = Utils.generateUniqueString(userName);
            token = new String(Base64.encodeBase64(token.getBytes()));
            Calendar expiry = Calendar.getInstance();
            expiry.add(Calendar.HOUR, 1); // Token expires in an hour
            DynamoDBHandler.getInstance().setPasswordResetToken(token, userName, expiry.getTimeInMillis()); // Create entry in db
            // Send email to user with password reset link
            sendEmail(token, userName);
            return Response.ok("ok").build();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    private void sendEmail(String token, String email) throws IOException, MessagingException {
        try {
            if (log.isLoggable(Level.INFO)) log.info("Sending email...");

            Session getMailSession = Session.getDefaultInstance(getMailServerProperties(), null);
            MimeMessage generateMailMessage = new MimeMessage(getMailSession);
            generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            generateMailMessage.setSubject(Constants.PASSWORD_RESET_EMAIL_SUBJECT);
            String emailBody = getEmailBody(token);
            generateMailMessage.setContent(emailBody, "text/html");

            Transport transport = getMailSession.getTransport("smtp"); // TODO
            transport.connect("smtp.gmail.com", "<----- Your GMAIL ID ----->", "<----- Your GMAIL PASSWORD ----->"); //TODO
            transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            throw e;
        }
    }

    private String getEmailBody(String token) {
        StringBuilder sb = new StringBuilder();
        sb.append(Constants.PASSWORD_RESET_EMAIL_LINE_1).append("<br><br><a href=").append(Constants.PASSWORD_RESET_URL).append("?t=").append(token).append(">")
                .append(Constants.PASSWORD_RESET_URL).append("</a>").append("<br><br>").append(Constants.PASSWORD_RESET_EMAIL_LINE_2)
                .append("<br><br>").append(Constants.PASSWORD_RESET_EMAIL_LINE_3).append("<br><br>").append(Constants.EMAIL_SIGNATURE);
        return sb.toString();
    }

    private Properties getMailServerProperties() throws IOException {
        if (mailServerProperties == null) {
            mailServerProperties = Utils.readProps(Constants.MAIL_SERVER_PROPERTIES);
        }
        return mailServerProperties;
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
