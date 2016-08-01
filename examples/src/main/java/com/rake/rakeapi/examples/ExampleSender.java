package com.rake.rakeapi.examples;

import com.rake.rakeapi.LoggerAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ExampleSender {
    LoggerAPI loggerAPI;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public ExampleSender() throws Exception {
        Properties properties = new Properties();
        properties.put("api.type", "live");
        properties.put("service.id", "rakeapi-tester2");
        try {
            loggerAPI = LoggerAPI.getInstance(properties);
        } catch (Exception e) {
            logger.error("Error while initializing LoggerAPI", e);
            throw e;
        }
    }

    private void run(String args[]) throws Exception {
        System.out.println("Start run");
        logger.info("Start run");
        int count = Integer.parseInt(args[0]);
        logger.info("Log count is : " + count);
        System.out.println("Log count is : " + count);
        for (int i = 0; i < count; i++) {
            loggerAPI.log("rakeapi-admin-test", "test_service_kafka_async 24 hour! "+i);
            System.out.print(".");
            if(i%100==0){
                System.out.println();
            }
        }
        Thread.sleep(61000);
        loggerAPI.close(true);
    }

    public static void main(String[] arg) throws Exception {
        ExampleSender exampleClient = new ExampleSender();
        exampleClient.run(arg);
    }
}
