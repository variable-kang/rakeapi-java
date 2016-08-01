package com.rake.rakeapi.tools.queue;

import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class QueueSenderTest {
    String tempFile = null;
    String tempDir = null;

    @Before
    public void setUp() throws IOException {
        File tempFile = File.createTempFile("tmp", "tmp");
        tempFile.deleteOnExit();
        this.tempFile = tempFile.getAbsolutePath();
        tempDir = "/tmp";
    }

    @Ignore
    @Test(expected = ParseException.class)
    public void noParam() throws Exception {
        runProcess(new String[]{});
    }

    @Ignore
    @Test(expected = Exception.class)
    public void wrongBrokerList() throws Exception {
        runProcess(new String[]{"--file", tempFile, "--broker-list", "wrong_server"});
    }

    @Ignore
    @Test
    public void correctBrokerList() throws Exception {
        runProcess(new String[]{"--dry-run", "--file", tempFile, "--broker-list", "dicc-broker01-172.cm.skp:9092,dicc-broker02-172.cm.skp:9092,dicc-broker03-172.cm.skp:9092,dicc-broker04-172.cm.skp:9092"});
    }

    public void runProcess(String[] args) throws Exception {
        try {
            new QueueSender().process(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
}