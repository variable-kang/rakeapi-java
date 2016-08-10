package com.rake.rakeapi.sender;

import com.rake.rakeapi.LoggerAPI;
import org.junit.Test;

import java.util.Properties;

/**
 * Created by jl on 4/29/14.
 */
public class ConsoleTest {
    LoggerAPI loggerAPI;

    @Test
    public void SyncConsole() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("api.type", "dev");
        properties.setProperty("logger.type", "Sync");
        properties.setProperty("service.id", "rakeapi-tester");
        properties.setProperty("sender.class", ConsoleSender.class.getName());
        loggerAPI = LoggerAPI.getInstance(properties);

        String testLog = "testLog";
        loggerAPI.log("service_god", testLog);
        loggerAPI.close(true);
    }

    @Test
    public void ASyncConsole() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("api.type", "dev");
        properties.setProperty("logger.type", "async");
        properties.setProperty("service.id", "rakeapi-tester");
        properties.setProperty("sender.class", ConsoleSender.class.getName());
        loggerAPI = LoggerAPI.getInstance(properties);

        String testLog = "testLog";
        loggerAPI.log("service_god", testLog);
        loggerAPI.close(true);
    }
}
