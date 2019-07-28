package com.github.zaphx.discordbot.utilities;

import java.util.logging.Logger;

public class MultiLogger {

    Logger logger;
    DebugLogger debugLogger;

    public MultiLogger(Logger logger, DebugLogger debugLogger) {
        this.debugLogger = debugLogger;
        this.logger = logger;
    }

    public void info(String message) {
        debugLogger.logToFile(message);
        logger.info(message);
    }

    public void warning(String message) {
        debugLogger.logToFile("[WARNING] " + message);
        logger.warning(message);
    }

    public void severe(String message) {
        debugLogger.logToFile("[SEVERE] " + message);
        logger.warning(message);
    }

}
