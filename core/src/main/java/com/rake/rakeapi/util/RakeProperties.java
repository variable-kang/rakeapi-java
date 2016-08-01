package com.rake.rakeapi.util;

import com.rake.rakeapi.queue.FileQueue;
import com.rake.rakeapi.queue.MemoryQueue;
import com.rake.rakeapi.sender.AsyncKafkaSender;
import com.rake.rakeapi.sender.ConsoleSender;

import java.util.Properties;

public class RakeProperties {
    private Properties protoDefaultProperties = null;
    private Properties devDefaultProperties = null;
    private Properties liveDefaultProperties = null;
    private Properties currentDefaultProperties = null;
    private Properties runningProperties = null;

    public RakeProperties() {
        this(new Properties());
    }

    public RakeProperties(Properties properties) {
        protoDefaultProperties = new Properties();
        protoDefaultProperties.setProperty("logger.type", "sync");
        protoDefaultProperties.setProperty("sender.class", ConsoleSender.class.getName());
        protoDefaultProperties.setProperty("queue.class", MemoryQueue.class.getName());
        protoDefaultProperties.setProperty("sender.use_monitoring", "true");
        protoDefaultProperties.setProperty("sender.monitoring_timedelta", "1");
        protoDefaultProperties.setProperty("sender.monitoring_topic", "rakeapi-monitoring");
        protoDefaultProperties.setProperty("sender.use_monitoring", "false");
        protoDefaultProperties.setProperty("sender.monitoring_timedelta", "1");
        protoDefaultProperties.setProperty("sender.monitoring_topic", "rakeapi-monitoring");
        protoDefaultProperties.setProperty("sender.send_heartbeats", "false");
        protoDefaultProperties.setProperty("sender.send_heartbeats_message", "$$$$$$$$$$");

        devDefaultProperties = new Properties();
        devDefaultProperties.setProperty("logger.type", "async");
        devDefaultProperties.setProperty("sender.class", AsyncKafkaSender.class.getName());
        devDefaultProperties.setProperty("sender.send_heartbeats", "true");
        devDefaultProperties.setProperty("sender.send_heartbeats_message", "$$$$$$$$$$");
        devDefaultProperties.setProperty("queue.class", FileQueue.class.getName());
        devDefaultProperties.setProperty("queue.file.path", "/tmp/rake_api_queue/");
        devDefaultProperties.setProperty("queue.file.name", "devqueue");
        devDefaultProperties.setProperty("queue.file.gc_period_sec", "60");
        devDefaultProperties.setProperty("sender.use_monitoring", "true");
        devDefaultProperties.setProperty("sender.monitoring_timedelta", "1");
        devDefaultProperties.setProperty("sender.monitoring_topic", "rakeapi-dev-monitoring");
        devDefaultProperties.setProperty("async.kafka.bootstrap.servers", "DICi-devbroker01.is.skp:9092,DICi-devbroker02.is.skp:9092,DICi-devbroker03.is.skp:9092"
                + ",DICi-devbroker04.is.skp:9092,DICi-devbroker05.is.skp:9092");
        devDefaultProperties.setProperty("async.kafka.acks", "all");
        devDefaultProperties.setProperty("async.kafka.retries", "0");
        devDefaultProperties.setProperty("async.kafka.batch.size", "16384");
        devDefaultProperties.setProperty("async.kafka.linger.ms", "0");
        devDefaultProperties.setProperty("async.kafka.buffer.memory", "33554432");
        devDefaultProperties.setProperty("async.kafka.key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        devDefaultProperties.setProperty("async.kafka.value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        devDefaultProperties.setProperty("async.kafka.client.id", "");
        devDefaultProperties.setProperty("sender.rakeapi.version", "rakeapi-0.2.0");

        liveDefaultProperties = new Properties();
        liveDefaultProperties.setProperty("logger.type", "async");
        liveDefaultProperties.setProperty("sender.class", AsyncKafkaSender.class.getName());
        liveDefaultProperties.setProperty("sender.send_heartbeats", "true");
        liveDefaultProperties.setProperty("sender.send_heartbeats_message", "$$$$$$$$$$");
        liveDefaultProperties.setProperty("queue.class", FileQueue.class.getName());
        liveDefaultProperties.setProperty("queue.file.path", "/tmp/rake_api_queue/");
        liveDefaultProperties.setProperty("queue.file.name", "livequeue");
        liveDefaultProperties.setProperty("queue.file.gc_period_sec", "60");
        liveDefaultProperties.setProperty("sender.use_monitoring", "true");
        liveDefaultProperties.setProperty("sender.monitoring_timedelta", "1");
        liveDefaultProperties.setProperty("sender.monitoring_topic", "rakeapi-monitoring");
        liveDefaultProperties.setProperty("async.kafka.bootstrap.servers", "dicc-broker01-172.cm.skp:9092,dicc-broker02-172.cm.skp:9092,dicc-broker03-172.cm.skp:9092,dicc-broker04-172.cm.skp:9092,dicc-broker05-172.cm.skp:9092,dicc-broker06-172.cm.skp:9092");
        liveDefaultProperties.setProperty("async.kafka.acks", "all");
        liveDefaultProperties.setProperty("async.kafka.retries", "0");
        liveDefaultProperties.setProperty("async.kafka.batch.size", "16384");
        liveDefaultProperties.setProperty("async.kafka.linger.ms", "0");
        liveDefaultProperties.setProperty("async.kafka.buffer.memory", "33554432");
        liveDefaultProperties.setProperty("async.kafka.key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        liveDefaultProperties.setProperty("async.kafka.value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        liveDefaultProperties.setProperty("async.kafka.client.id", "");
        liveDefaultProperties.setProperty("sender.rakeapi.version", "rakeapi-0.2.0");

        runningProperties = properties;
        if (!runningProperties.containsKey("api.type")) {
            throw new IllegalArgumentException("api.type is mandatory option");
        }
        if (!runningProperties.containsKey("service.id")) {
            throw new IllegalArgumentException("service.id is mandatory option");
        }
        String apiType = runningProperties.getProperty("api.type");
        if ("live".equals(apiType.toLowerCase())) {
            currentDefaultProperties = liveDefaultProperties;
        } else if ("dev".equals(apiType.toLowerCase())) {
            currentDefaultProperties = devDefaultProperties;
        } else if ("proto".equals(apiType.toLowerCase())) {
            currentDefaultProperties = protoDefaultProperties;
        } else {
            throw new IllegalArgumentException("api.type must be one of \"live\", \"dev\", \"proto\"");
        }

    }

    public String get(String propertyName) {
        String propertyNameLowerCase = propertyName.toLowerCase();
        String value = runningProperties.getProperty(propertyNameLowerCase);
        if (null == value) {
            value = currentDefaultProperties.getProperty(propertyNameLowerCase);
        }
        if (null == value) {
            throw new IllegalArgumentException(propertyName + " is not defined.");
        }
        return value;
    }

    public Properties getAll(String prefix) {
        Properties properties = new Properties();
        String prefixLowerCase = prefix.toLowerCase();
        for (String key : currentDefaultProperties.stringPropertyNames()) {
            if (key.startsWith(prefixLowerCase)) {
                properties.setProperty(key, currentDefaultProperties.getProperty(key));
            }
        }

        for (String key : runningProperties.stringPropertyNames()) {
            if (key.startsWith(prefixLowerCase)) {
                properties.setProperty(key, runningProperties.getProperty(key));
            }
        }
        return properties;
    }
}
