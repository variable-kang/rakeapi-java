package com.rake.rakeapi.queue;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class QueueInOutTest {
    @Test
    public void jsonReadTest() throws IOException {
        String serviceId = "rakeapi-admin-test";
        String log = "testLog";
        //로그를 파일 큐에 저장하는 로직
        ObjectMapper objectMapperIn = new ObjectMapper();
        HashMap<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.clear();
        jsonMap.put("service_id", serviceId);
        jsonMap.put("log", log);
        String jsonObject = objectMapperIn.writeValueAsString(jsonMap);

        //파일 큐에 저장되어 있는 로그를 읽어와서 service_id와 log로 분리하는 로직
        ObjectMapper objectMapperOut = new ObjectMapper();
        JsonNode jsonNode = objectMapperOut.readTree(jsonObject.getBytes());
        String message = jsonNode.get("log").getTextValue();
        assertEquals("service_id", "bos-newtech-server", jsonNode.get("service_id").getTextValue());
        assertEquals("log", log, message);
    }
}
