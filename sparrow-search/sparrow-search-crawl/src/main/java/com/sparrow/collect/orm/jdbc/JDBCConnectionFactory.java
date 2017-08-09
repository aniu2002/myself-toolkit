package com.sparrow.collect.orm.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class JDBCConnectionFactory implements ConnectionFactory {
    private boolean showSql;
    private boolean formatSql;
    private String driver;
    private String url;
    private String user;
    private String password;
    private boolean first = true;

    public JDBCConnectionFactory(Map<String, String> map) {
        this.driver = map.get("jdbc.driver");
        this.url = map.get("jdbc.url");
        this.user = map.get("jdbc.user");
        this.password = map.get("jdbc.password");
        this.showSql = "true".equalsIgnoreCase(map.get("jdbc.showSql"));
        this.formatSql = "true".equalsIgnoreCase(map.get("jdbc.formatSql"));
    }

    public boolean isShowSql() {
        return showSql;
    }

    public boolean isFormatSql() {
        return formatSql;
    }

    public boolean test() {
        try {
            this.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Connection getConnection() throws SQLException {
        if (this.first) {
            try {
                Class.forName(driver);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            this.first = false;
        }
        return DriverManager.getConnection(url, user, password);
    }

    public String getDatabaseType() {
        return "mysql";
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public boolean isOracle() {
        return false;
    }

    @Override
    public void destroy() {

    }
}
