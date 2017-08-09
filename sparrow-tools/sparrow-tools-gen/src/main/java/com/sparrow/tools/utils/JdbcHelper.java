package com.sparrow.tools.utils;

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
}
