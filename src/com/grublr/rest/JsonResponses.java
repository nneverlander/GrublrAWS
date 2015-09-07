package com.grublr.rest;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by adi on 9/7/15.
 */
@XmlRootElement
public class JsonResponses {

    private List<JsonResponse> jsonResponses;

    public JsonResponses() {

    }

    public JsonResponses(List<JsonResponse> responses) {
        jsonResponses = responses;
    }


    public List<JsonResponse> getJsonResponses() {
        return jsonResponses;
    }

    public void setJsonResponses(List<JsonResponse> jsonResponses) {
        this.jsonResponses = jsonResponses;
    }
}
