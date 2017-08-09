package com.sparrow.app.common.source;

import com.sparrow.core.log.Logger;
import com.sparrow.core.log.LoggerManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by yuanzc on 2016/1/4.
 */
public class JDBCSource extends DBSource {
    private Connection connection;
    private Logger log = LoggerManager.getSysLog();

    private String url;
    private String driver;
    private String user;
    private String password;

    public JDBCSource(String url, String driver, String user, String password) {
        this.url = url;
        this.driver = driver;
        this.user = user;
        this.password = password;
    }

    @Override
    public boolean initialize() {
        return false;
    }

    @Override
    public boolean destroy() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 获取链接
     *
     * @return
     */
    protected Connection getConnection() {
        try {
            return this.doGetConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    Connection doGetConnection() throws SQLException {
        Connection con = this.connection;
        if (con != null) {
            return con;
        }
        try {
            Class.forName(this.driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("No driver setting ... ");
        }

        this.log.info("---------------------------------------------");
        this.log.info(" -- Driver     : " + this.driver);
        this.log.info(" -- Url : " + this.url);
        this.log.info("---------------------------------------------");

        Properties props = new Properties();
        // props.put("remarksReporting", "true");
        props.put("user", this.user);
        props.put("password", this.password);
        con = DriverManager.getConnection(
                this.url, props);
        this.connection = con;
        return con;
    }


}
