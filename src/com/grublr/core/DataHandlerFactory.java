package com.grublr.core;

import com.grublr.util.Constants;
import com.grublr.util.Utils;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by adi on 9/3/15.
 */
public class DataHandlerFactory {

    private static final Logger log = Logger.getLogger(DataHandlerFactory.class.getName());

    public static DataStoreHandler getDefaultDataStoreHandler() {
        Properties props = Utils.readProps(Constants.APP_PROPERTIES);
        String defaultHandler = props.getProperty("dataStoreHandler.default");
        return (DataStoreHandler) getHandler(defaultHandler);
    }

    public static PhotoHandler getDefaultPhotoHandler() {
        Properties props = Utils.readProps(Constants.APP_PROPERTIES);
        String defaultHandler = props.getProperty("photoHandler.default");
        return (PhotoHandler) getHandler(defaultHandler);
    }

    private static DataHandler getHandler(String defaultHandler) {
        try {
            Class handler = Class.forName(defaultHandler);
            return (DataHandler) handler.getMethod("getInstance").invoke(null);
        } catch (ClassNotFoundException e) {
            log.severe(e.getCause() + e.getMessage() + e.toString());
        } catch (InvocationTargetException e) {
            log.severe(e.getCause() + e.getMessage() + e.toString());
        } catch (NoSuchMethodException e) {
            log.severe(e.getCause() + e.getMessage() + e.toString());
        } catch (IllegalAccessException e) {
            log.severe(e.getCause() + e.getMessage() + e.toString());
        }
        return null;
    }

}
