package com.sparrow.collect.orm.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Project Name: sparrow-egg
 * Package Name: com.sparrow.collect.website.database.jdbc
 * Author : YZC
 * Date: 2016/12/12
 * Time: 10:09
 */
public interface ConnectionFactory {
    Connection getConnection() throws SQLException;

    String getDatabaseType();

    boolean isShowSql();

    boolean isFormatSql();

    String getUser();

    String getDriver();

    String getUrl();

    String getPassword();

    void destroy();

    boolean isOracle();
}
