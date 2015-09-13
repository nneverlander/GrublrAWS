package com.grublr.core;

import java.io.IOException;

/**
 * Created by adi on 8/31/15.
 */
public interface PhotoHandler extends DataHandler {

    void writePhoto(String name, byte[] image) throws IOException;

    String readPhoto(String name) throws IOException;

    void editPhoto(String uniqueName, byte[] bytes) throws IOException;
}
