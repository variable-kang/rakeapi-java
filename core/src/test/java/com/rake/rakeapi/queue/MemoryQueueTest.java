package com.rake.rakeapi.queue;

import com.rake.rakeapi.Queue;
import com.rake.rakeapi.util.RakeProperties;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Created by 1002718 on 2016. 7. 27..
 */
public class MemoryQueueTest {
    RakeProperties rakeProperties;

    @Test
    public void FileQueueTest() throws Exception {

        String testLog = "{\"service_id\":\"rakeapi-admin-test\",\"log\":\"강병수\"}";

        Properties properties = new Properties();
        properties.setProperty("api.type", "dev");
        properties.setProperty("service.id", "rakeapi-tester2");
        properties.setProperty("queue.class", MemoryQueue.class.getName());
        rakeProperties = new RakeProperties(properties);
        Queue queue = Queue.getInstance(rakeProperties);
        for (int i = 0; i < queue.size() - 1; i++) {
            queue.deQueue();
        }
        queue.enQueue(testLog);
        assertEquals("FileQueue enqueue and dequeue equivalence test", testLog, queue.deQueue());
    }
}
