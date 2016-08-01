package com.rake.rakeapi;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jl on 7/3/15.
 */
public class ThreadFactoryFactory implements ThreadFactory {
    private final ThreadGroup threadGroup;
    private final String threadNamePrefix;
    private final AtomicInteger threadNumber = new AtomicInteger(0);

    public ThreadFactoryFactory(ThreadGroup threadGroup, String threadNamePrefix) {
        if (null == threadGroup || null == threadNamePrefix) {
            throw new NullPointerException("threadGroup and threadNamePrefix cannot be null");
        }
        this.threadGroup = threadGroup;
        this.threadNamePrefix = threadNamePrefix.endsWith("-") ? threadNamePrefix : threadNamePrefix + "-";
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(threadGroup, r, threadNamePrefix + threadNumber.getAndIncrement());
    }
}
