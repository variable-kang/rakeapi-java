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

        String testLog = "{\"service_id\":\"rakeapi-admin-test\",\"log\":\"ticket_id\":\"100420130910230151959100\",\"channelCode\":\"tmap\",\"groupCode\":null,\"userKey\":null,\"invokeClassName\":\"com.skmnc.ndds.tmap.resource.notice.NoticeServiceResource\",\"invokeMethodName\":\"findPopupNotices\",\"serviceKrName\":\"요금제 정보 전달 조회\",\"resourceURI\":\"/etc/noticeservice/findpopupnotices\",\"httpMethod\":\"POST\",\"inputSize\":273,\"outputSize\":280389,\"startTime\":1378821711984,\"endTime\":1378821712026,\"success\":true,\"exceptionCode\":null,\"exceptionMessage\":null,\"terminalCode\":null,\"terminalMessage\":null,\"mdn\":\"01122223333\",\"sid\":\"61001610016000A\",\"fee\":\"61400\",\"sessionId\":\"100420130910230151959100\",\"trsSessionId\":\"\",\"server\":\"skmc-NDDTpCH4\",\"step\":\"D\",\"preService\":\"50\",\"menu\":\"2D\",\"reqMenu\":null,\"rpOption\":null,\"error\":null,\"verPhone\":\"HD4.1.2(459200)\",\"verEtc\":\"SHV-E210S\",\"poiIdMppNsl\":null,\"targetName\":null,\"tigMin\":null,\"svcType\":\"47\",\"verKit\":null,\"peekThread\":2,\"serverType\":\"S\",\"deviceId\":null,\"sendSid\":\"61001610016000A\",\"userId\":null,\"osType\":\"AND\",\"appType\":null,\"appExeType\":null,\"using\":\"MAIN\",\"macAddress\":\"\",\"tigMvnoInfo\":null,\"tigServiceNumber\":null,\"tauthWifi\":null";

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
