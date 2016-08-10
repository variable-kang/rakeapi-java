package com.rake.rakeapi.sender;

import com.rake.rakeapi.LoggerAPI;
import org.junit.Test;

import java.util.Properties;

/**
 * Created by jl on 4/29/14.
 */
public class KafkaTest {
    LoggerAPI loggerAPI;

    @Test
    public void SyncKafka() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("api.type", "dev");
        properties.setProperty("logger.type", "sync");
        properties.setProperty("service.id", "rakeapi-tester");
        properties.setProperty("sender.class", AsyncKafkaSender.class.getName());
        loggerAPI = LoggerAPI.getInstance(properties);

        String testLog = "testLog";
        loggerAPI.log("rakeapi-admin-test", testLog);
        loggerAPI.close(true);
    }

    @Test
    public void ASyncKaka() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("api.type", "dev");
        properties.setProperty("logger.type", "async");
        properties.setProperty("service.id", "rakeapi-tester");
        properties.setProperty("sender.class", AsyncKafkaSender.class.getName());
        loggerAPI = LoggerAPI.getInstance(properties);

        String testLog = "testLog";
        loggerAPI.log("rakeapi-admin-test", testLog);
        loggerAPI.close(true);
    }
}
