package com.rake.rakeapi;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Kang Byeongsu on 2016. 4. 4..
 */
public class ErrorLog {
    private static final Logger logger = LoggerFactory.getLogger(ErrorLog.class);
    private static ErrorLog instance = null;
    private long errorCount;
    private long sendCount;
    private String lastError;

    private ErrorLog() {
        errorCount = 0;
        sendCount = 0;
        lastError = "";
    }

    public static synchronized ErrorLog getInstance() {
        if (null == instance)
            instance = new ErrorLog();
        return instance;
    }

    public void error(String message, Exception e) {
        addErrorLogCounter(message, e, false);
        logger.error(message, e);
    }

    public void sendError(String message, Exception e) {
        addErrorLogCounter(message, e, true);
        logger.error(message, e);
    }

    synchronized String getLastError() {
        return lastError;
    }

    synchronized long getSendCount() {
        return sendCount;
    }

    synchronized long getErrorCount() {
        return errorCount;
    }

    synchronized void reset() {
        errorCount = 0;
        sendCount = 0;
        lastError = "";
    }

    private synchronized void addErrorLogCounter(String errorMessage, Exception e, boolean isSendError) {
        errorCount++;
        lastError = errorMessage + e.toString();
        if (isSendError) {
            sendCount++;
        }
    }
}
