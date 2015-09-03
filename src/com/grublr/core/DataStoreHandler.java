package com.grublr.core;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 * Created by adi on 9/1/15.
 */
public interface DataStoreHandler extends DataHandler {

    void writeData(String associatedImageName, JsonNode jsonData);
    List<JsonNode> readData(JsonNode inputJson);

}
