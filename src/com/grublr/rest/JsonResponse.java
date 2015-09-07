package com.grublr.rest;

import com.fasterxml.jackson.databind.JsonNode;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by adi on 9/7/15.
 */
@XmlRootElement
public class JsonResponse {

    private byte[] binaryData;
    private JsonNode metadata;

    public JsonResponse() {

    }

    public JsonResponse(JsonNode post, byte[] image) {
        metadata = post;
        binaryData = image;
    }

    public byte[] getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }

    public JsonNode getMetadata() {
        return metadata;
    }

    public void setMetadata(JsonNode metadata) {
        this.metadata = metadata;
    }

}
