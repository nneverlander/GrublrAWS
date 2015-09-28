package com.grublr.core;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by adi on 8/31/15.
 */
public interface PhotoHandler extends DataHandler {

    void writePhoto(String name, byte[] image) throws IOException;

    String readPhoto(String name) throws IOException;

    void editPhoto(JsonNode node, byte[] bytes) throws IOException;

    void postTaken(JsonNode entityObj) throws IOException;
}
