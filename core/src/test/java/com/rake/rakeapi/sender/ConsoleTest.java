package com.rake.rakeapi.sender;

import com.rake.rakeapi.LoggerAPI;
import org.junit.Test;

import java.util.Properties;

/**
 * Created by jl on 4/29/14.
 */
public class ConsoleTest {
    LoggerAPI loggerAPI;

    @Test
    public void SyncConsole() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("api.type", "dev");
        properties.setProperty("logger.type", "Sync");
        properties.setProperty("service.id", "rakeapi-tester");
        properties.setProperty("sender.class", ConsoleSender.class.getName());
        loggerAPI = LoggerAPI.getInstance(properties);

        String testLog = "{\"ticketId\":\"100420130910230151959100\",\"channelCode\":\"tmap\",\"groupCode\":null,\"userKey\":null,\"invokeClassName\":\"com.skmnc.ndds.tmap.resource.notice.NoticeServiceResource\",\"invokeMethodName\":\"findPopupNotices\",\"serviceKrName\":\"요금제 정보 전달 조회\",\"resourceURI\":\"/etc/noticeservice/findpopupnotices\",\"httpMethod\":\"POST\",\"inputSize\":273,\"outputSize\":280389,\"startTime\":1378821711984,\"endTime\":1378821712026,\"success\":true,\"exceptionCode\":null,\"exceptionMessage\":null,\"terminalCode\":null,\"terminalMessage\":null,\"mdn\":\"01122223333\",\"sid\":\"61001610016000A\",\"fee\":\"61400\",\"sessionId\":\"100420130910230151959100\",\"trsSessionId\":\"\",\"server\":\"skmc-NDDTpCH4\",\"step\":\"D\",\"preService\":\"50\",\"menu\":\"2D\",\"reqMenu\":null,\"rpOption\":null,\"error\":null,\"verPhone\":\"HD4.1.2(459200)\",\"verEtc\":\"SHV-E210S\",\"poiIdMppNsl\":null,\"targetName\":null,\"tigMin\":null,\"svcType\":\"47\",\"verKit\":null,\"peekThread\":2,\"serverType\":\"S\",\"deviceId\":null,\"sendSid\":\"61001610016000A\",\"userId\":null,\"osType\":\"AND\",\"appType\":null,\"appExeType\":null,\"using\":\"MAIN\",\"macAddress\":\"\",\"tigMvnoInfo\":null,\"tigServiceNumber\":null,\"tauthWifi\":null}";
        loggerAPI.log("service_god", testLog);
        loggerAPI.close(true);
    }

    @Test
    public void ASyncConsole() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("api.type", "dev");
        properties.setProperty("logger.type", "async");
        properties.setProperty("service.id", "rakeapi-tester");
        properties.setProperty("sender.class", ConsoleSender.class.getName());
        loggerAPI = LoggerAPI.getInstance(properties);

        String testLog = "{\"ticketId\":\"100420130910230151959100\",\"channelCode\":\"tmap\",\"groupCode\":null,\"userKey\":null,\"invokeClassName\":\"com.skmnc.ndds.tmap.resource.notice.NoticeServiceResource\",\"invokeMethodName\":\"findPopupNotices\",\"serviceKrName\":\"요금제 정보 전달 조회\",\"resourceURI\":\"/etc/noticeservice/findpopupnotices\",\"httpMethod\":\"POST\",\"inputSize\":273,\"outputSize\":280389,\"startTime\":1378821711984,\"endTime\":1378821712026,\"success\":true,\"exceptionCode\":null,\"exceptionMessage\":null,\"terminalCode\":null,\"terminalMessage\":null,\"mdn\":\"01122223333\",\"sid\":\"61001610016000A\",\"fee\":\"61400\",\"sessionId\":\"100420130910230151959100\",\"trsSessionId\":\"\",\"server\":\"skmc-NDDTpCH4\",\"step\":\"D\",\"preService\":\"50\",\"menu\":\"2D\",\"reqMenu\":null,\"rpOption\":null,\"error\":null,\"verPhone\":\"HD4.1.2(459200)\",\"verEtc\":\"SHV-E210S\",\"poiIdMppNsl\":null,\"targetName\":null,\"tigMin\":null,\"svcType\":\"47\",\"verKit\":null,\"peekThread\":2,\"serverType\":\"S\",\"deviceId\":null,\"sendSid\":\"61001610016000A\",\"userId\":null,\"osType\":\"AND\",\"appType\":null,\"appExeType\":null,\"using\":\"MAIN\",\"macAddress\":\"\",\"tigMvnoInfo\":null,\"tigServiceNumber\":null,\"tauthWifi\":null}";
        loggerAPI.log("service_god", testLog);
        loggerAPI.close(true);
    }
}
