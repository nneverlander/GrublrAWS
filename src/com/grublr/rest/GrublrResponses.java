package com.grublr.rest;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by adi on 9/7/15.
 */
@XmlRootElement
public class GrublrResponses {

    private List<GrublrResponse> responses;

    public GrublrResponses() {

    }

    public GrublrResponses(List<GrublrResponse> responses) {
        this.responses = responses;
    }

    public List<GrublrResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<GrublrResponse> responses) {
        this.responses = responses;
    }
}
