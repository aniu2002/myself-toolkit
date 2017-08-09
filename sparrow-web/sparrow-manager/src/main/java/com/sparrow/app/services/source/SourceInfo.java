package com.sparrow.app.services.source;

/**
 * Created by yuanzc on 2015/8/18.
 */
public class SourceInfo {
    private long id;
    private String name;
    private String type;
    private String user;
    private String password;
    private String url;
    private String driver;
    //最大可用连接数
    private int maxIdle = 60;
    //最大活跃数，最大使用连接数
    private int maxActive = 50;
    // 最小可用连接数
    private int minIdle = 2;
    //最大等待数10个,最多可用连接数不超过10个
    private int maxWait = 10;
    //长时间存在，不销毁，不做时间检查回收
    private int longTimeFlag = 0;
    private int showSql = 0;
    private int formatSql = 0;

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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public int getLongTimeFlag() {
        return longTimeFlag;
    }

    public void setLongTimeFlag(int longTimeFlag) {
        this.longTimeFlag = longTimeFlag;
    }

    public int getShowSql() {
        return showSql;
    }

    public void setShowSql(int showSql) {
        this.showSql = showSql;
    }

    public int getFormatSql() {
        return formatSql;
    }

    public void setFormatSql(int formatSql) {
        this.formatSql = formatSql;
    }
}