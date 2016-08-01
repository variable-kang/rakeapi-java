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
        String serviceId = "bos-newtech-server";
        String log = "20150305101557739\thtkn7eOC8Kv6h5M8rPU4\tG8o5fD8tclnRiECvIRpdPo37CuYjQJiz3Bidm70Jb15ZfdNwUW/oU854cmAUMBeaMxqje8tlKSBwasn/m+zyFw==\t1417691178755\tBLE\tN\tsend\tagent\tSHV-E250S\tadr\t18\tios-8\t28819878\t14.12.15:1.5.26:21\tBGL00110\t{\"udid\":\"gHDoXYbZJsVqAVWJ2SBdV+oHhY42GnAmcgbI+LoArMjq68frQhSvNc2MVWNtUoY/k1VvlJ3Nxt/sU5rpAt4gwQ==\",\"uuid\":\"0288DCBAC7B2436CBED44E9256E67F89\",\"major\":\"0\",\"minor\":\"175\",\"app_id\":\"J3i2ADJ20AK3dkD8\",\"distance\":\"0.12\",\"target_app_id\":\"nfsN8yyKp9rnw13k\",\"target_app_pkg\":\"com.skmc.skcashbag.home_google\",\"target_app_order\":\"3\",\"group_id\":\"11730\",\"error_code\":\"0\",\"target_id\":\"BLE_00015961\",\"contents_id\":\"SV_00032865\",\"owner_yn\":\"Y\"}";

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
