package com.sparrow.collect.orm.jdbc;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jdbc.pool.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Project Name: test-parent
 * Package Name: com.sparrow.collect.website.database.jdbc
 * Author : YZC
 * Date: 2016/12/9
 * Time: 15:56
 */
public class DataSourceConnectionFactory extends JDBCConnectionFactory {
    final DataSource dataSource;

    public DataSourceConnectionFactory(Map<String, String> props) {
        super(props);

        DataSource p = new DataSource();
        p.setDriverClassName(this.getDriver());
        p.setUrl(this.getUrl());
        p.setUsername(this.getUser());
        p.setPassword(this.getPassword());

        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);

        p.setMaxActive(toInt(props.get("max.active"), 100));
        p.setInitialSize(toInt(props.get("idle.size"), 5));
        // p.setMaxIdle(toInt(props.get("max.idle"), 10));
        p.setMinIdle(toInt(props.get("min.idle"), 5));
        p.setMaxWait(toInt(props.get("max.wait"), 10000));

        p.setRemoveAbandonedTimeout(toInt(props.get("remove.timeout"), 60));
        p.setMinEvictableIdleTimeMillis(toInt(props.get("min.time.mills"), 30000));
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        this.dataSource = p;
    }

    static int toInt(String str, int defaultVal) {
        if (StringUtils.isEmpty(str))
            return defaultVal;
        try {
            return Integer.parseInt(str);
        } catch (Throwable t) {
            return defaultVal;
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void destroy() {
        super.destroy();
        this.dataSource.close();
    }
}
