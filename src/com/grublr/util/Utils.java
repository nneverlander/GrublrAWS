package com.grublr.util;

import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.geometry.S2LatLng;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by adi on 9/1/15.
 */
public class Utils {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = Logger.getLogger(Utils.class.getName());

    public static JsonNode stringToJson(String str) throws IOException {
        try {
            return mapper.readTree(str);
        } catch (IOException e) {
            throw e;
        }
    }

    public static void sysout(Object... obj) {
        for (Object o : obj) {
            System.out.println(o.toString());
        }
    }

    public static Properties readProps(String fileName) throws IOException {
        Properties props = new Properties();
        InputStream input = null;
        try {
            input = Utils.class.getResourceAsStream("/" + fileName);
            props.load(input);

        } catch (IOException e) {
            throw e;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }
        return props;
    }

    public static double getDistanceBetweenPointsInMiles(double lat1, double lng1, double lat2, double lng2) {
        S2LatLng point1 = S2LatLng.fromDegrees(lat1, lng1);
        S2LatLng point2 = S2LatLng.fromDegrees(lat2, lng2);
        double distInMeters = point1.getEarthDistance(point2);
        //Convert to miles
        return distInMeters/Constants.METERS_PER_MILE;
    }

    public static File stream2file (InputStream in) throws IOException {
        final File tempFile = File.createTempFile("streamToFile", ".tmp");
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }

    public static String generateUniqueString(String str) {
        return str + Calendar.getInstance().getTimeInMillis() + "-" + UUID.randomUUID();
    }

    public static JsonNode mapToJson(Map<String, Object> resultMap) throws IOException {
        return stringToJson(mapper.writeValueAsString(resultMap));
    }

    public static String mapToString(Map<String, Object> resultMap) throws IOException {
        return mapper.writeValueAsString(resultMap);
    }

    public static String jsonToString(JsonNode post) throws JsonProcessingException {
        return mapper.writeValueAsString(post);
    }

    public static String objToString(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }
}
