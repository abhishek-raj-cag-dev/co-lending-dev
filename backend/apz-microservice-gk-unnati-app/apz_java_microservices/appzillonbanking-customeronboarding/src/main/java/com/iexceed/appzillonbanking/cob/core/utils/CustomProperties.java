package com.iexceed.appzillonbanking.cob.core.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class CustomProperties {
    private static final Logger logger = LogManager.getLogger(CustomProperties.class);

    private String sampleUrl;

    public CustomProperties() {
        Properties prop;
        InputStream inputStream;
        try {
            inputStream = CustomProperties.class.getClassLoader().getResourceAsStream("CustomProperties.properties");
            if (inputStream != null) {
                if (logger.isInfoEnabled())
                    logger.info("Loading property file CustomProperties.properties.");
                prop = new Properties();
                prop.load(inputStream);

                sampleUrl = prop.getProperty("sampleUrl");

                if (logger.isInfoEnabled())
                    logger.info("Property file loaded.");
            } else {
                logger.error("Sorry, unable to find property file CustomProperties.properties !!!");
            }
        } catch (IOException e) {
            logger.error("Error While loading property file ", e);
        }
    }

    public String getSampleUrl() {
        return sampleUrl;
    }

}
