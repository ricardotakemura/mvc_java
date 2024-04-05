package com.luizalabs.simple.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLogger {
    private Logger logger;

    public AppLogger(Class<?> clazz) {
        logger = LoggerFactory.getLogger(clazz);
    }

    public long startLog(String message, Object... params) {
        if (logger.isInfoEnabled()) {
            logger.info("[START] " + message + ".", params);
            return System.currentTimeMillis();
        }
        return 0;
    }

    public void endLog(String message, long start, Object... params) {
        if (logger.isInfoEnabled()) {
            logger.info("[END] " + message +
                ". Time: " + (System.currentTimeMillis() - start) +
                "ms.", params);
        }
    }

    public void error(String message, Throwable throwable) {
        if (logger.isErrorEnabled()) {
            logger.error("[ERROR] " + message, throwable);
        }
    }

    public void warn(String message, Throwable throwable) {
        if (logger.isWarnEnabled()) {
            logger.warn("[WARN] " + message, throwable);
        }
    }

    public void info(String message, Object... params) {
        if (logger.isInfoEnabled()) {
            logger.info("[INFO] " + message, params);
        }
    }

    public void debug(String message, Object... params) {
        if (logger.isDebugEnabled()) {
            logger.debug("[DEBUG] " + message, params);
        }
    }
}
