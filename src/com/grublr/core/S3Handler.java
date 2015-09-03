package com.grublr.core;

import java.io.IOException;

/**
 * Created by adi on 9/3/15.
 */
public class S3Handler implements PhotoHandler {

    @Override
    public void writePhoto(String name, byte[] image) throws IOException {

    }

    @Override
    public byte[] readPhoto(String name) throws IOException {
        return new byte[0];
    }
}
