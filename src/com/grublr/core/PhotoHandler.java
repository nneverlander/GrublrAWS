package com.grublr.core;

import java.io.IOException;

/**
 * Created by adi on 8/31/15.
 */
public interface PhotoHandler extends DataHandler {

    void writePhoto(String name, byte[] image) throws IOException;
    byte[] readPhoto(String name) throws IOException;

}
