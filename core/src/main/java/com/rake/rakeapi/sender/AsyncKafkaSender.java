package com.rake.rakeapi.sender;

import com.rake.rakeapi.ErrorLog;
import com.rake.rakeapi.Queue;
import com.rake.rakeapi.Sender;
import com.rake.rakeapi.util.RakeProperties;
import org.apache.kafka.clients.producer.*;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AsyncKafkaSender extends Sender {
    private final Logger logger = LoggerFactory.getLogger(AsyncKafkaSender.class);
    Properties producerProperties;
    private final static String settingPrefix = "async.kafka.";
    private Producer<String, String> kafkaProducer;
    private ErrorLog errorLog = ErrorLog.getInstance();
    private ConcurrentMap<String, Long> logCountMap;

    public AsyncKafkaSender(Queue queue, RakeProperties rakeProperties) throws Exception {
        super(queue);
        Properties properties = rakeProperties.getAll(settingPrefix);
        producerProperties = new Properties();
        for (String key : properties.stringPropertyNames()) {
            String asyncKey = key.replace(settingPrefix, "");
            String asyncValue = properties.getProperty(key);
            logger.info("Kafka Async Properties. Key : " + asyncKey + ", Value : " + asyncValue);
            producerProperties.setProperty(asyncKey, asyncValue);
        }
        kafkaProducer = new KafkaProducer<String, String>(producerProperties);
        logCountMap = new ConcurrentHashMap<String, Long>();
    }

    @Override
    public boolean send(final String log) {
        try {
            logger.debug("log: [{}]", log);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(log.getBytes());
            String serviceId = jsonNode.get("service_id").getTextValue();
            String message = jsonNode.get("log").getTextValue();

            kafkaProducer.send(new ProducerRecord<String, String>(serviceId, message), new Callback() {
                public void onCompletion(RecordMetadata metadata, Exception e) {
                    if (e != null) {
                        queue.enQueue(log);
                        errorLog.sendError("Send Error in Callback", e);
                    }
                }
            });
            if (logCountMap.containsKey(serviceId))
                logCountMap.put(serviceId, (logCountMap.get(serviceId) + 1L));
            else
                logCountMap.put(serviceId, 1L);


            logger.debug("sent successfully: [{}]", log);
            return true;
        } catch (Throwable t) {
            errorLog.error("Wrong log. Ignore: " + log, new Exception());
            return true;
        }
    }

    @Override
    public Map<String, Long> getLogCountMap() {
        return logCountMap;
    }

    @Override
    public void close() {
        super.close();
        if (null != kafkaProducer) {
            kafkaProducer.close();
            kafkaProducer = null;
        }
        logger.info("KafkaProducer is closed");
    }
}
