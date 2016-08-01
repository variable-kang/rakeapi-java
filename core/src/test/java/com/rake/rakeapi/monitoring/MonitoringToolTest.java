package com.rake.rakeapi.monitoring;

import com.rake.rakeapi.heartbeat.MonitoringTools;
import com.rake.rakeapi.util.RakeProperties;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by 1002718 on 2016. 7. 27..
 */
public class MonitoringToolTest {
    @Test
    public void getTopicJoin() {
        Set<String> testSet = Collections.synchronizedSet(new HashSet<String>());
        testSet.add("test1");
        testSet.add("test2");
        testSet.add("test3");
        Properties properties = new Properties();
        properties.setProperty("api.type", "dev");
        properties.setProperty("service.id", "rakeapi-tester");
        MonitoringTools monitoringTool = new MonitoringTools(new RakeProperties(properties));
        //assertEquals("Topic List delimiter is, ", "test1,test2,test3", monitoringTool.getTopic(testSet));

    }

    @Test
    public void getTopicJoinbyNull() {
        Set<String> testSet = Collections.synchronizedSet(new HashSet<String>());
        Properties properties = new Properties();
        properties.setProperty("api.type", "dev");
        properties.setProperty("service.id", "rakeapi-tester");
        MonitoringTools monitoringTool = new MonitoringTools(new RakeProperties(properties));
        assertEquals("Topic List delimiter is, ", "", monitoringTool.getTopic(testSet));

    }
  /*  @Ignore
    @Test
    public void getLogCount(){
        Properties properties=new Properties();
        properties.setProperty("api.type","dev");
        properties.setProperty("sender.use_monitoring","false");
        try {
            LoggerAPI loggerAPI = LoggerAPI.getInstance(properties);
          *//*  int count=0;
            int sum=0;*//*
            for(int i=0;i<100000;i++){
                loggerAPI.log("rakeapi-admin-test","testlog");
                //count++;
              *//*  if(count==3){
                    sum+=loggerAPI.getLogAllCount();
                    loggerAPI.resetLogCount();
                    count=0;
                }*//*
            }
          //  sum+=loggerAPI.getLogAllCount();
//            assertEquals("All Log Count is : ",sum, 100000);
            assertEquals("All Log Count is : ",loggerAPI.getLogAllCount(), 100000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/
}