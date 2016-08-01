package com.rake.rakeapi.sender;

import com.rake.rakeapi.Queue;
import com.rake.rakeapi.Sender;
import com.rake.rakeapi.util.RakeProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Use only for testing
 * Created by jl on 4/23/14.
 */
public class ConsoleSender extends Sender {
    private final Logger logger = LoggerFactory.getLogger(ConsoleSender.class);

    public ConsoleSender(Queue queue, RakeProperties rakeProperties) throws Exception {
        super(queue);
    }

    @Override
    public boolean send(String log) {
        logger.debug(log);
        System.out.println(log);
        return true;
    }

    @Override
    public Map<String, Long> getLogCountMap() {
        return new ConcurrentHashMap<String, Long>();
    }
}
