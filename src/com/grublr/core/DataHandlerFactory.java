package com.grublr.core;

import com.grublr.util.Constants;
import com.grublr.util.Utils;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by adi on 9/3/15.
 */
public class DataHandlerFactory {

    private static final Logger log = Logger.getLogger(DataHandlerFactory.class.getName());

    public static DataStoreHandler getDefaultDataStoreHandler() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException {
        Properties props = Utils.readProps(Constants.APP_PROPERTIES);
        String defaultHandler = props.getProperty("dataStoreHandler.default");
        return (DataStoreHandler) getHandler(defaultHandler);
    }

    public static PhotoHandler getDefaultPhotoHandler() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException {
        Properties props = Utils.readProps(Constants.APP_PROPERTIES);
        String defaultHandler = props.getProperty("photoHandler.default");
        return (PhotoHandler) getHandler(defaultHandler);
    }

    private static DataHandler getHandler(String defaultHandler) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        try {
            Class handler = Class.forName(defaultHandler);
            return (DataHandler) handler.getMethod("getInstance").invoke(null);
        } catch (Exception e) {
            throw e;
        }
    }

}
