package com.grublr.core;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by adi on 9/3/15.
 */
public interface DataHandler {

    void deleteData(JsonNode entityObj) throws Exception;

}
