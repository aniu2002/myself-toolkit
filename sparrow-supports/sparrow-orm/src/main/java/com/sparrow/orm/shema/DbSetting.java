package com.sparrow.orm.shema;

public class DbSetting {
    public final String driver;
    public final String url;
    public final String user;
    public final String password;

    public DbSetting() {
        this.driver = "com.mysql.jdbc.Driver";
        this.url = "jdbc:mysql://127.0.0.1/sys?autoReconnect=true&useUnicode=true&characterEncoding=utf8";
        this.user = "root";
        this.password = "123456";
    }

    public DbSetting(String driver, String url, String user,
                     String password) {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
    }
}
