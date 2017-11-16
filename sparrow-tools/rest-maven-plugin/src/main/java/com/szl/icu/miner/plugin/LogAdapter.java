package com.szl.icu.miner.plugin;

import com.szl.icu.miner.tools.log.Log;

public class LogAdapter implements Log {
    private final org.apache.maven.plugin.logging.Log mvnLog;

    public LogAdapter(org.apache.maven.plugin.logging.Log mvnLog) {
        this.mvnLog = mvnLog;
    }

    public void info(Object message) {
        if (message != null)
            this.mvnLog.info(message.toString());
    }

    public void debug(Object message) {
        if (message != null)
            this.mvnLog.debug(message.toString());
    }
}
