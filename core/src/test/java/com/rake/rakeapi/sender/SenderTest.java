package com.rake.rakeapi.sender;

import com.rake.rakeapi.Queue;
import com.rake.rakeapi.Sender;
import com.rake.rakeapi.queue.MemoryQueue;
import com.rake.rakeapi.sender.ConsoleSender;
import com.rake.rakeapi.util.RakeProperties;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Created by jl on 4/10/14.
 */
public class SenderTest {
    RakeProperties rakeProperties;
    final String classGetName = ConsoleSender.class.getName();
    final String queueGetName = MemoryQueue.class.getName();


    @Test
    public void ClassGetName() {
        assertEquals("getName()", "com.rake.rakeapi.sender.SenderTest", getClass().getName());
    }

    @Test
    public void getSenderByClassGetName() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("sender.class", classGetName);
        properties.setProperty("queue.class", queueGetName);
        properties.setProperty("api.type", "dev");
        properties.setProperty("service.id", "rakeapi-tester");
        rakeProperties = new RakeProperties(properties);
        Queue queue = Queue.getInstance(rakeProperties);
        Sender sender = Sender.getInstance(queue, rakeProperties);
        assertEquals("Reflection test by Queue.getName()", queueGetName.toString(), queue.getClass().getName().toString());
        assertEquals("Reflection test by Sender.getName()", classGetName.toString(), sender.getClass().getName().toString());
    }

    @Test(expected = ClassNotFoundException.class)
    public void getWrongSenderByClassGetNameWithException() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("sender.class", "com.rake.rakeapi.WrongSender");
        properties.setProperty("queue.class", "com.rake.rakeapi.WrongQueue");
        properties.setProperty("api.type", "dev");
        properties.setProperty("service.id", "rakeapi-tester");
        rakeProperties = new RakeProperties(properties);
        Queue queue = Queue.getInstance(rakeProperties);
        Sender sender = Sender.getInstance(queue, rakeProperties);
    }
}
