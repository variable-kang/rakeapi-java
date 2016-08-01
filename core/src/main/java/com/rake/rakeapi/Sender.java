package com.rake.rakeapi;

import com.rake.rakeapi.util.RakeProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class Sender implements Runnable {
    private static Logger staticLogger = LoggerFactory.getLogger(Sender.class.getName());
    private final Logger logger = LoggerFactory.getLogger(Sender.class);
    protected Queue queue = null;
    private boolean isRunning;

    protected Sender(Queue queue) throws Exception {
        this.queue = queue;
        logger.info("QueueName: [{}]", queue.getQueueName());
    }

    public static Sender getInstance(Queue queue, RakeProperties rakeProperties) throws Exception {

        String senderClass = rakeProperties.get("sender.class");
        try {
            return Class.forName(senderClass).asSubclass(Sender.class).getConstructor(Queue.class, RakeProperties.class).newInstance(queue, rakeProperties);
        } catch (Exception e) {
            staticLogger.error("cannot create Sender: " + senderClass, e);
            throw e;
        }
    }


    /**
     * For sync sender
     */
    public abstract boolean send(String log);

    /**
     * For async sender
     */
    public void run() {
        String log;
        isRunning = true;
        while (isRunning && null != (log = queue.deQueue())) {
            if (!send(log)) {
                isRunning = false;
                queue.enQueue(log);
            }
        }
    }

    public abstract Map<String, Long> getLogCountMap();

    public void close() {
        logger.info("Sender is closing");
        isRunning = false;
    }
}
