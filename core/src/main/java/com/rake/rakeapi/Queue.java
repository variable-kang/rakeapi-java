package com.rake.rakeapi;

import com.rake.rakeapi.util.RakeProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jl on 4/21/14.
 */
public abstract class Queue {
    private static final Logger staticLogger = LoggerFactory.getLogger(Queue.class.getName());


    private String queueName;

    public Queue() {
    }

    protected Queue(RakeProperties rakeProperties) {
        queueName = rakeProperties.get("queue.class");
    }

    public static Queue getInstance(RakeProperties rakeProperties) throws Exception {
        String queueClass = rakeProperties.get("queue.class");
        try {
            return Class.forName(queueClass).asSubclass(Queue.class).getConstructor(RakeProperties.class).newInstance(rakeProperties);
        } catch (Exception e) {
            staticLogger.error("cannot create Queue: " + queueClass, e);
            throw e;
        }
    }

    public String getQueueName() {
        return queueName;
    }

    public abstract Boolean enQueue(String log);

    /**
     * @return log if exists, null otherwise
     */
    public abstract String deQueue();

    public abstract int size();

    public abstract void close();
}
