package com.rake.rakeapi;

import com.rake.rakeapi.heartbeat.*;
import com.rake.rakeapi.util.RakeProperties;
//import com.skplanet.pdp.sentinel.shuttle.LogAgentSentinelShuttle;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * This Class is Logging API. It works with Sender and Queue.
 * Sender use Kafka Producer API, Queue use MemoryQueue and FileQueue.
 * Kafka Producer help to send Server to DIC Kafka Broker. MemoryQueue preserve log data in memory. FileQueue preserve log data in File.
 * MessageQueue is so fast but sometimes dangerous about data losing.FileQueue prevent washing away message before send log to kafka broker successfully.
 * Additinally, this can use rakeapi monitoring function. monitoring logs can check RakeAPI health.
 */
public class LoggerAPI {
    private static LoggerAPI instance = null;
    private final Logger logger = LoggerFactory.getLogger(LoggerAPI.class);
    private final RakeProperties rakeProperties;
    private final Queue queue;
    private final Sender sender;
    private boolean isSync;
    private ScheduledExecutorService senderExecutorService;
    private ScheduledExecutorService heartbeatSenderExecutorService;
    private ScheduledExecutorService monitorExecutorService;
    private Set<String> serviceIdSet;
    private ErrorLog errorLog = ErrorLog.getInstance();
    private MonitoringTools monitoringTools;

    private LoggerAPI(final Properties properties) throws Exception {
        rakeProperties = new RakeProperties(properties);
        String loggerType = rakeProperties.get("logger.type");
        logger.info("logger.type: {}", loggerType);
        isSync = "sync".equals(loggerType);
        serviceIdSet = Collections.synchronizedSet(new HashSet<String>());

        this.queue = Queue.getInstance(rakeProperties);
        this.sender = Sender.getInstance(queue, rakeProperties);

        monitoringTools = new MonitoringTools(rakeProperties);
        logger.info("Logger.type: [Async]");


        senderExecutorService = Executors.newScheduledThreadPool(1, new ThreadFactoryFactory(new ThreadGroup("sender"), "log-sender"));
        senderExecutorService.scheduleWithFixedDelay(sender, 1000, 10, TimeUnit.MILLISECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("Sender is closing");
                senderExecutorService.shutdown();
                logger.info("Sender is closed");
            }
        });

        setHeartbeat();
    }


    /**
     * This method get singleton LoggerAPI instance.
     *
     * @param properties
     * @return LoggerAPI Instance
     * @throws Exception rakeapi has error, throw Exception to stop service.
     */
    public static synchronized LoggerAPI getInstance(Properties properties) throws Exception {
        if (null == instance) {
            instance = new LoggerAPI(properties);
//            instance.startMonitoring();
        }
        return instance;
    }

    /**
     * This method send log to DIC kafka broker. it have two mode are sync, async.
     * sync mode is blocking until receive ack at kafka broker. async is blocking until write to FileQueue.
     * sync or async setting can change in RakeProperties.
     *
     * @param serviceId kafka topic
     * @param log       kafka message
     * @return result of sending or writing.
     */
    public boolean log(String serviceId, String log) {
        logger.debug("service_id: [{}], log: [{}]", serviceId, log);
        if (!serviceIdSet.contains(serviceId)) serviceIdSet.add(serviceId);
        try {
            String sendMessage = parseJsonToString(serviceId, log);
            if (isSync) {
                logger.debug("json string: " + sendMessage);
                return sender.send(sendMessage);
            } else {
                return queue.enQueue(sendMessage);
            }
        } catch (IOException e) {
            errorLog.error("Error while converting json format", e);
            return false;
        }
    }

    /**
     * This method send log to DIC kafka broker. The difference thing with log method, directSend() doesn't use Queue, Just sending log to Kafka Broker.
     * When your goal is no delay sending, You can use this method. The directSend go with log losing possibility because of don't use queue.
     *
     * @param topic kafka topic
     * @param log   kafka message
     * @return result of sending
     */
    public boolean directSend(String topic, String log) {
        logger.debug("direct send. topic: [{}], log: [{}]", topic, log);
        if (!serviceIdSet.contains(topic)) serviceIdSet.add(topic);
        try {
            String sendMessage = parseJsonToString(topic, log);
            return sender.send(sendMessage);
        } catch (IOException e) {
            errorLog.error("Error while converting json format", e);
            return false;
        }
    }

    /**
     * This method get Disk Usage ratio at server.
     *
     * @return Disk Usage ratio
     */
    public long getDiskusage() {
        return monitoringTools.getSystemDiskUsage();
    }

    /**
     * This method is String parse to Json. Because of matching format to write Filequeue, RakeAPI does parsing.
     *
     * @param topic
     * @param log
     * @return JSON format String data
     * @throws IOException JSON Parsing Exception
     */
    private String parseJsonToString(String topic, String log) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("service_id", topic);
        jsonMap.put("log", log);
        return objectMapper.writeValueAsString(jsonMap);
    }


    /**
     * This method calling kill -9 process.
     */
    public void close() {
        close(false);
    }

    /**
     * This method do close process. It clear to remaining jobs.
     *
     * @param waitSendAll true is graceful shutdown. false is force shutdown.
     */
    public void close(boolean waitSendAll) {
        if (waitSendAll) {
            logger.info("Current queue size is [{}]. Waiting until sending all data to Kafka...", getQueueSize());
            try {
                while (getQueueSize() > 0) {
                    Thread.sleep(1000);
                    logger.info("Current queue size is [{}]", getQueueSize());
                }
                logger.info("Queue is empty");
            } catch (InterruptedException e) {
                errorLog.error("closing error", e);
            }
        }

        logger.info("Logger is closing...");
        if (null != senderExecutorService && !senderExecutorService.isShutdown()) {
            logger.info("Shutdown log sender executor");
            senderExecutorService.shutdown();
        }
        if (null != heartbeatSenderExecutorService && !heartbeatSenderExecutorService.isShutdown()) {
            logger.info("Shutdown heartbeat sender executor");
            heartbeatSenderExecutorService.shutdownNow();
        }
        if (null != queue) {
            queue.close();
        }

        if (null != monitorExecutorService && !monitorExecutorService.isShutdown()) {
            logger.info("Shutdown monitor sender executor");
            monitorExecutorService.shutdownNow();
        }
        if (null != sender) {
            sender.close();
        }
        instance = null;
        logger.info("Logger closed.");
    }

    /**
     * This method is heartbeat message to all activate topics periodically.
     * periodical message can help to collecting data and health analysis
     */
    private void setHeartbeat() {
        boolean sendHeartbeats = "true".equals(rakeProperties.get("sender.send_heartbeats"));
        if (sendHeartbeats) {
            final String heartbeatsMessage = rakeProperties.get("sender.send_heartbeats_message");

            heartbeatSenderExecutorService = Executors.newScheduledThreadPool(1, new ThreadFactoryFactory(new ThreadGroup("sender"), "heartbeat-sender"));
            heartbeatSenderExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (String id : serviceIdSet) {
                            log(id, heartbeatsMessage);
                        }
                    } catch (Exception e) {
                        errorLog.sendError("send heartbeat Message", e);
                        logger.info("Cannot send a heartbeat message");
                    }
                }
            }, 1, 1, TimeUnit.MINUTES);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    logger.info("Heartbeat Sender is closing");
                    heartbeatSenderExecutorService.shutdown();
                    logger.info("Heartbeat Sender is closed");
                }
            });
        }
    }


    /**
     * This method get number of remaining message in Queue.
     *
     * @return number of remaining message
     */
    public long getQueueSize() {
        return queue.size();
    }


    /**
     * This method get activating topic set.
     *
     * @return activating topic set
     */
    private Set<String> getServiceIdSet() {
        return serviceIdSet;
    }

    /**
     * This method get sum of messages are completed sending.
     *
     * @return sum of messages are completed sending
     */
    private long getLogAllCount() {
        long sum = 0L;
        for (Long v : sender.getLogCountMap().values()) {
            sum += v;
        }
        return sum;
    }

    private void resetLogCount() {
        Map<String, Long> tempLogCount = sender.getLogCountMap();
        for (String key : tempLogCount.keySet()) {
            tempLogCount.put(key, 0L);
        }
    }

    /**
     * This methog get current time up to millisecond.
     *
     * @return current time
     */
    private String getCurrentTime() {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.KOREA);
        return formatter.format(new java.util.Date());
    }

    /**
     * This method get shuttle message converted String. If it throws Exception in middle of running, but it will don't stop the process.
     *
     * @return shuttle to String message
     */
//    private String getShuttleMessage() {
//        LogAgentSentinelShuttle shuttle = null;
//
//        try {
//            shuttle = new LogAgentSentinelShuttle();
//
//            shuttle.log_time(getCurrentTime())
//                    .hostname(monitoringTools.CONSTANT_HOST_NAME)
//                    .host_ip(monitoringTools.CONSTANT_HOST_IP)
//                    .system_load(monitoringTools.getSystemLoad())
//                    .system_disk_usage(monitoringTools.getSystemDiskUsage())
//                    .logagent_directory(monitoringTools.getFilePath())
//                    .reader_count(0L/*RakeAPI didn't need column*/)
//                    .in_work_queue_count(monitoringTools.getWorkingQueueCount())
//                    .in_waiting_queue_count(getQueueSize())
//                    .time_delta(monitoringTools.getTimeDelta())
//                    .error_count(errorLog.getErrorCount())
//                    .send_error_count(errorLog.getSendCount())
//                    .last_error_message(errorLog.getLastError())
//                    .file_queue_size(monitoringTools.getFileSize())
//                    .service_name(rakeProperties.get("service.id") + "-" + rakeProperties.get("sender.rakeapi.version"))
//                    .topic(monitoringTools.getTopic(getServiceIdSet()))
//                    .send_log_count(getLogAllCount())
//                    .topics(sender.getLogCountMap());
//            return shuttle.toString();
//        } catch (Exception e) {
//            errorLog.error("Data insufficiency Error in rakeapi-monitoring.", e);
//            return "Monitoring Error";
//        }
//    }

    /**
     * This method start rakeapi monitoring log to check rakeapi's health.
     * If invoke error in middle of making log, this write error log. but process don't dead and still run.
     */
//    private void startMonitoring() {
//        boolean useHeartbeat = "true".equals(rakeProperties.get("sender.use_monitoring"));
//        logger.info("use.heartbeat: {}", useHeartbeat);
//        if (useHeartbeat) {
//            int timedelta = Integer.parseInt(rakeProperties.get("sender.monitoring_timedelta"));
//            logger.info("Heartbeat time delta is : {}", timedelta);
//            final String hbTopic = rakeProperties.get("sender.monitoring_topic");
//            monitorExecutorService = Executors.newScheduledThreadPool(1, new ThreadFactoryFactory(new ThreadGroup("sender"), "monitoring-sender"));
//            monitorExecutorService.scheduleAtFixedRate(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        String logMessage = parseJsonToString(hbTopic, getShuttleMessage());
//                        logger.debug("RakeAPI monitoring log is : {}", logMessage);
//                        sender.send(logMessage);
//                        errorLog.reset();
//                        resetLogCount();
//                    } catch (IOException ioe) {
//                        errorLog.error("Monitoring data json parsing error", ioe);
//                    }
//                }
//            }, 0, timedelta, TimeUnit.MINUTES);
//            Runtime.getRuntime().addShutdownHook(new Thread() {
//                @Override
//                public void run() {
//                    logger.info("Heartbeat Sender is closing");
//                    monitorExecutorService.shutdown();
//                    logger.info("Heartbeat Sender is closed");
//                }
//            });
//        }
//    }
}
