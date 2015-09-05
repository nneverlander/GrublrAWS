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
    private static Properties props;
    private static boolean isInit = false;

    private DataHandlerFactory () {

    }

    private static void init() throws IOException {
        if(!isInit) {
            props = Utils.readProps(Constants.APP_PROPERTIES);
            isInit = true;
        }
    }

    public static DataStoreHandler getDefaultDataStoreHandler() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException {
        init();
        String defaultHandler = props.getProperty("dataStoreHandler.default");
        return (DataStoreHandler) getHandler(defaultHandler);
    }

    public static PhotoHandler getDefaultPhotoHandler() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException {
        init();
        String defaultHandler = props.getProperty("photoHandler.default");
        return (PhotoHandler) getHandler(defaultHandler);
    }

    private static DataHandler getHandler(String defaultHandler) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Class handler = Class.forName(defaultHandler);
        return (DataHandler) handler.getMethod("getInstance").invoke(null);
    }

}
