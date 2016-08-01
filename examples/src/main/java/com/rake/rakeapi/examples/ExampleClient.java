package com.rake.rakeapi.examples;

import com.rake.rakeapi.LoggerAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class ExampleClient {
    LoggerAPI loggerAPI;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    Logger rakeLogger = LoggerFactory.getLogger("rake.test");

    public ExampleClient() throws Exception {
        Properties properties = new Properties();
        properties.put("api.type", "dev");
        properties.put("service.id", "rakeapi-tester");
        try {
            loggerAPI = LoggerAPI.getInstance(properties);
        } catch (Exception e) {
            logger.error("Error while initializing LoggerAPI", e);
            throw e;
        }
    }

    private void run(String[] arg) throws Exception {
        if (arg.length < 1) {
            throw new RuntimeException("Filename must be needed");
        }

        BufferedReader in = arg[0].equals("-") ?
                new BufferedReader(new InputStreamReader(System.in)) :
                new BufferedReader(new FileReader(arg[0]));

        String line;
        while (in.ready()) {
            line = in.readLine();
            // loggerAPI.log("NoService", line);
            rakeLogger.info(line);
        }
    }

    public static void main(String[] arg) throws Exception {
        ExampleClient exampleClient = new ExampleClient();
        exampleClient.run(arg);
        Thread.sleep(1000 * 10);
    }
}
