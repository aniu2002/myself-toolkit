package com.sparrow.tools.utils;

import com.sparrow.tools.common.DbSetting;

import java.sql.*;
import java.util.Date;

/**
 * Created by Administrator on 2016/3/10 0010.
 */
public class JdbcHelper {
    public static final int TYPE_UNKNOWN = Integer.MIN_VALUE;

    public static int getSqlType(Class<?> javaType) {
        if (javaType == null)
            return TYPE_UNKNOWN;
        else if (javaType.isPrimitive() || javaType == Integer.class)
            return Types.NUMERIC;
        else if (javaType == Boolean.class)
            return Types.BOOLEAN;
        else if (javaType == Long.class)
            return Types.NUMERIC;
        else if (javaType == Float.class)
            return Types.NUMERIC;
        else if (javaType == Double.class)
            return Types.NUMERIC;
        else if (javaType == Date.class)
            return Types.DATE;
        else if (javaType == Time.class)
            return Types.TIME;
        else if (javaType == Timestamp.class)
            return Types.TIMESTAMP;
        else if (javaType == String.class)
            return Types.VARCHAR;
        else if (javaType == Clob.class)
            return Types.CLOB;
        else if (javaType == Blob.class)
            return Types.BLOB;
        else if (javaType == Character.class)
            return Types.CHAR;
        return TYPE_UNKNOWN;
    }

    public static void main(String[] args) {
        DbSetting dbs = new DbSetting("com.mysql.jdbc.Driver",
                "jdbc:mysql://192.168.2.30:3306/bi?useUnicode=true&characterEncoding=UTF-8&useInformationSchema=true", "bi",
                "123456");
        try {
            getTableNotes(getConnection(dbs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static Connection getConnection(DbSetting dbs) throws SQLException {
        try {
            Class.forName(dbs.driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("No driver setting ... ");
        }
        return DriverManager.getConnection(dbs.url, dbs.user, dbs.password);
    }

    private static void getTableNotes(Connection con) {
        try {
            DatabaseMetaData dbmd = con.getMetaData();
            ResultSet resultSet = dbmd.getTables(null, "%", "%", new String[]{"TABLE"});
            int n = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                String remarkes = resultSet.getString("REMARKS");
                StringBuilder sb = new StringBuilder();
                int idx;
                for (int i = 0; i < n; i++) {
                    idx = i + 1;
                    sb.append(',').append(idx).append('-').append(resultSet.getMetaData().getColumnName(idx)).append('-').append(resultSet.getObject(idx));
                }
                System.out.println(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
