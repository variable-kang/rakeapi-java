package com.rake.rakeapi;

import org.junit.*;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Created by jl on 4/23/14.
 */
public class LoggerAPITest {
    @Ignore
    @Test
    public void proto_AsyncLoggerAPITest() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("api.type", "proto");
        properties.setProperty("service.id", "rakeapi-tester");
        LoggerAPI loggerAPI = LoggerAPI.getInstance(properties);
        int count = 10;
        for (int i = 0; i < count; i++)
            assertEquals("ASyncLoggerAPITest", loggerAPI.log("rakeapi-admin-test", "test_service_kafka_async 24 hour!!"), true);

        Thread.sleep(3000);

        loggerAPI.close(true);
    }

    //    @Ignore
    @Test
    public void dev_AsyncLoggerAPITest() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("api.type", "dev");
        properties.setProperty("service.id", "rakeapi-tester");
        LoggerAPI loggerAPI = LoggerAPI.getInstance(properties);
        int count = 10;
        Thread.sleep(1000);
        for (int i = 0; i < count; i++)
            assertEquals("ASyncLoggerAPITest", loggerAPI.log("rakeapi-admin-test", "test_service_kafka_async 24 hour!!"), true);
        for (int i = 0; i < count; i++)
            assertEquals("ASyncLoggerAPITest", loggerAPI.log("kbs", "test_service_kafka_async 24 hour!!"), true);
        for (int i = 0; i < count; i++)
            assertEquals("ASyncLoggerAPITest", loggerAPI.log("kbs2", "test_service_kafka_async 24 hour!!"), true);

    //    Thread.sleep(30000);
        loggerAPI.close(true);
    }

    @Ignore
    @Test
    public void live_AsyncLoggerAPITest() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("api.type", "live");
        properties.setProperty("service.id", "rakeapi-tester");
        LoggerAPI loggerAPI = LoggerAPI.getInstance(properties);
        loggerAPI.log("rakeapi-admin-test", "test_service_kafka_async 24 hour!!");
        Thread.sleep(70000);
        loggerAPI.close();
    }
}
