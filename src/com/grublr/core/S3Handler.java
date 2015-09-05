package com.grublr.core;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.google.common.io.ByteStreams;
import com.grublr.util.Constants;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by adi on 9/3/15.
 */
public class S3Handler implements PhotoHandler {

    private S3Handler() {

    }

    private static S3Handler instance;

    public static final S3Handler getInstance() {
        if (instance == null) {
            instance = new S3Handler();
        }
        return instance;
    }

    private static final Logger log = Logger.getLogger(S3Handler.class.getName());
    private static final AmazonS3 s3Client = new AmazonS3Client(new InstanceProfileCredentialsProvider());
    private static final TransferManager transferMgr = new TransferManager(new InstanceProfileCredentialsProvider());

    @Override
    public void writePhoto(String name, byte[] image) throws IOException {
        try {
            if (log.isLoggable(Level.INFO)) log.info("Writing photo to S3");
            // TransferManager processes all transfers asynchronously, so this call will return immediately.
            long contentLength = image.length;
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentLength);
            transferMgr.upload(Constants.S3_BUCKET, name, new ByteArrayInputStream(image), metadata);
            if (log.isLoggable(Level.INFO)) log.info("Writing photo to S3 complete");
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public byte[] readPhoto(String name) throws IOException {
        try {
            if (log.isLoggable(Level.INFO)) log.info("Reading photo from S3");
            S3Object s3object = s3Client.getObject(new GetObjectRequest(Constants.S3_BUCKET, name));
            InputStream stream = s3object.getObjectContent();
            if (log.isLoggable(Level.INFO)) log.info("Read photo from S3");
            return ByteStreams.toByteArray(stream);
        } catch (Exception e) {
            throw e;
        }
    }
}
