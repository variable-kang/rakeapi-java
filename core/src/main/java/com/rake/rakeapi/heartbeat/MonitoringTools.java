package com.rake.rakeapi.heartbeat;

import com.rake.rakeapi.ErrorLog;
import com.rake.rakeapi.util.RakeProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Set;

/**
 * Created by Kang Byeongsu on 2016. 7. 26..
 * This Class is many tools to make rakeapi-monitoring log.
 */
public class MonitoringTools {
    private final Logger logger = LoggerFactory.getLogger(MonitoringTools.class);
    private RakeProperties rakeProperties;
    private long fileSize;
    private final static int GB = 1024 * 1024 * 1024;
    public final String CONSTANT_HOST_IP = getHostIP();
    public final String CONSTANT_HOST_NAME = getHostname();
    private final OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
    private ErrorLog errorLog = ErrorLog.getInstance();
    private String dir_path;

    public MonitoringTools(RakeProperties rakeProperties) {
        this.rakeProperties = rakeProperties;
        dir_path = rakeProperties.get("queue.file.path") + rakeProperties.get("service.id");
    }


    /**
     * This method parse all topics are activating as one line.
     *
     * @param topicList
     * @return
     */
    public String getTopic(Set<String> topicList) {
        ArrayList<String> liveTopic = new ArrayList<String>();
        for (String topic : topicList) {
            liveTopic.add(topic);
        }
        return StringUtils.join(liveTopic, ",");
    }


    /**
     * This method get remaining file count at FileQueue Directory
     *
     * @return
     */
    public long getWorkingQueueCount() {
        String filepath = getFilePath() + File.separator + rakeProperties.get("queue.file.name") + File.separator + "data";
        fileSize = 0L;
        long workingQueueCount = 0L;
        if (filepath.trim().isEmpty()) {
        } else {
            workingQueueCount = getFilesInDirectory(new File(filepath), 0L);
        }
        return workingQueueCount;
    }

    /**
     * This method get File size. calculating method is getFilesInDirectory
     *
     * @return remain file queue size
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * This method get FileQueue path at properties file.
     *
     * @return filequeue directory path
     */
    public String getFilePath() {
        return "proto".equals(rakeProperties.get("api.type")) ? "./" : dir_path;
    }

    /**
     * This recursive method get size and count of all files in FileQueue Directory.
     *
     * @param f          FileQueue Directory
     * @param totalCount count variable
     * @return size and count of all files in FileQueue Directory
     */
    private long getFilesInDirectory(File f, long totalCount) {
        //     String tempPath = dir_path + File.separator + rakeProperties.get("queue.file.name") + File.separator + "data";
        if (f.isDirectory()) {
            String[] list = f.list();
            for (int i = 0; i < list.length; i++) {
                totalCount = getFilesInDirectory(new File(f, list[i]), totalCount);
            }
        } else {
            // if (tempPath.equals(f.getParent())) {
            totalCount++;
            fileSize += f.length();
            // }
        }
        return totalCount;
    }

    /**
     * This method get period of sending monitoring log in properties file.
     *
     * @return time delta, when fail to parse, return default value is 60sec.
     */
    public long getTimeDelta() {
        try {
            return Long.parseLong(rakeProperties.get("sender.monitoring_timedelta"));
        } catch (NumberFormatException e) {
            errorLog.error("Time delta String to Long parsing error", e);
            return 60;
        }
    }


    /**
     * This method get Server host name.
     *
     * @return hostname
     */
    private String getHostname() {
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            errorLog.error("get Hostname error", e);
            hostname = "localhost";
        }

        return hostname;
    }

    /**
     * This method get Server main IP. If server has many ip addresses, This select only one.
     *
     * @return server main IP.
     */
    private String getHostIP() {
        String ip = "127.0.0.1";
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = iface.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address) {
                        ip = addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            errorLog.error("get HostIP error", e);
        }
        return ip;
    }


    /**
     * This method get CPU Load Average.
     *
     * @return cpu load
     */
    public double getSystemLoad() {
        return operatingSystemMXBean.getSystemLoadAverage();
    }


    /**
     * This method get usable disk space.
     *
     * @return usable disk space
     */
    public long getSystemDiskUsage() {
        long spaceRatio = 0L;
        String filepath = "proto".equals(rakeProperties.get("api.type")) ? "./" : dir_path;
        File file = new File(filepath);
        long totalSpace = file.getTotalSpace();
        long usableSpace = file.getUsableSpace();
        spaceRatio = (long)((double)usableSpace/(double)totalSpace * 100);
        return spaceRatio;
    }
}
