package com.rake.rakeapi.queue;

import com.rake.rakeapi.Queue;
import com.rake.rakeapi.util.RakeProperties;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by jl on 4/21/14.
 */
public class MemoryQueue extends Queue {
    LinkedBlockingDeque<String> queue;


    public MemoryQueue(RakeProperties rakeProperties) {
        super(rakeProperties);
        queue = new LinkedBlockingDeque<String>();
    }

    @Override
    public Boolean enQueue(String log) {
        return queue.add(log);
    }

    @Override
    public String deQueue() {
        return queue.poll();
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public void close() {
        // Nothing
    }
}
