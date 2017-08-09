package com.sparrow.app.services.app;

/**
 * Created by yuanzc on 2015/8/18.
 */
public class AppInfo {
    private long id;
    private String name;
    private String desc;
    // 1 是cmd 2 rest类型
    private String type;
    private long sourceId;
    private String sourceName;
    private String appHome;
    private String webRootPath;
    private String configPath;
    private String processId;
    private int webPort;
    private int started;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAppHome() {
        return appHome;
    }

    public void setAppHome(String appHome) {
        this.appHome = appHome;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getWebRootPath() {
        return webRootPath;
    }

    public void setWebRootPath(String webRootPath) {
        this.webRootPath = webRootPath;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public int getWebPort() {
        return webPort;
    }

    public void setWebPort(int webPort) {
        this.webPort = webPort;
    }

    public int getStarted() {
        return started;
    }

    public void setStarted(int started) {
        this.started = started;
    }
}

