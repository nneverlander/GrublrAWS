package com.grublr.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by adi on 9/1/15.
 */
public class Utils {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = Logger.getLogger(Utils.class.getName());

    public static JsonNode stringToJson(String str) {
        try {
            return mapper.readTree(str);
        } catch (IOException e) {
            log.severe(e.getCause() + e.getMessage() + e.toString());
        }
        return null;
    }

    public static void sysout(Object... obj) {
        for (Object o : obj) {
            System.out.println(o.toString());
        }
    }

    public static Properties readProps(String fileName) {
        Properties props = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(fileName);
            // load a properties file
            props.load(input);

        } catch (IOException e) {
            log.severe(e.getCause() + e.getMessage() + e.toString());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.severe(e.getCause() + e.getMessage() + e.toString());
                }
            }
        }
        return props;
    }

    public static String generateUniqueString(String str) {
        return str + Calendar.getInstance().getTimeInMillis() + "-" + UUID.randomUUID();
    }
}
