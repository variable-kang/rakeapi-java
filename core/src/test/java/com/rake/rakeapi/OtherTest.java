package com.rake.rakeapi;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

public class OtherTest {
    private final Logger logger = LoggerFactory.getLogger(OtherTest.class);

    public Callable<Integer> returnIntegerWithSleep(int intValue, long sleepTime) {
        return new MyCallable(intValue, sleepTime);
    }

    @Test
    public void FutureTest() throws ExecutionException, InterruptedException {
        int jobs = 3;
        Set<FutureTask<Integer>> futureTaskSet = new HashSet<FutureTask<Integer>>();
        Set<Integer> resultSet = new HashSet<Integer>();

        for (int i = 0; i < jobs; i++) {
            logger.info("Insert: futureTask" + i + "; [" + i + "]");
            futureTaskSet.add(new FutureTask<Integer>(returnIntegerWithSleep(i, (long) i * 1000)));
            resultSet.add(i);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(jobs);
        for (FutureTask<Integer> futureTask : futureTaskSet)
            executorService.execute(futureTask);

        Set<Integer> actualSet = new HashSet<Integer>();
        while (jobs > 0) {
            Set<FutureTask<Integer>> currentFutureTaskSet = new HashSet<FutureTask<Integer>>(futureTaskSet);
            for (FutureTask<Integer> futureTask : currentFutureTaskSet)
                if (futureTask.isDone()) {
                    jobs--;
                    logger.info("Remove: futureTask: [" + futureTask.get() + "]");
                    futureTaskSet.remove(futureTask);
                    actualSet.add(futureTask.get());
                }
        }
        assertEquals("futureTask", resultSet, actualSet);

        executorService.shutdown();
    }

    @Test
    public void SplitTest() {
        String a = "a\tb\tc";
        System.out.println(a.split("\t")[0]);
    }
}

class MyCallable implements Callable<Integer> {
    private final int intValue;
    private final long sleepTime;

    public MyCallable(int intValue, long sleepTime) {
        this.intValue = intValue;
        this.sleepTime = sleepTime;
    }

    @Override
    public Integer call() throws Exception {
        Thread.sleep(sleepTime);
        return intValue;
    }
}