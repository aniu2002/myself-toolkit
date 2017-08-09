/**
 * File Name:StatementUtils.java
 * Date:2013-12-20上午9:35:02
 */

package com.sparrow.collect.orm.utils;

import com.sparrow.collect.orm.SqlParameter;
import com.sparrow.collect.orm.SqlParameterValue;
import org.apache.commons.lang3.StringUtils;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Calendar;
import java.util.Date;

public final class StatementUtils {
    public static final int TYPE_UNKNOWN = Integer.MIN_VALUE;

    public static void setParameterValue(PreparedStatement ps, int paramIndex,
                                         SqlParameter param, Object inValue) throws SQLException {
        setParameterValueInternal(ps, paramIndex, param.getSqlType(), param
                .getScale(), inValue);
    }

    public static void setParameterValue(PreparedStatement ps, int paramIndex,
                                         int sqlType, Object inValue) throws SQLException {
        setParameterValueInternal(ps, paramIndex, sqlType, null, inValue);
    }

    private static void setParameterValueInternal(PreparedStatement ps,
                                                  int paramIndex, int sqlType, Integer scale, Object inValue)
            throws SQLException {
        int sqlTypeToUse = sqlType;
        Object inValueToUse = inValue;
        // override type info?
        if (inValue instanceof SqlParameterValue) {
            SqlParameterValue parameterValue = (SqlParameterValue) inValue;
            if (parameterValue.getSqlType() != TYPE_UNKNOWN)
                sqlTypeToUse = parameterValue.getSqlType();
            inValueToUse = parameterValue.getValue();
        }
        if (inValueToUse == null)
            setNull(ps, paramIndex, sqlTypeToUse, null);
        else
            setValue(ps, paramIndex, sqlTypeToUse, scale, inValueToUse);
    }

    public final static void setNull(PreparedStatement ps, int paramIndex,
                                     int sqlType, String typeName) throws SQLException {
        if (typeName != null)
            ps.setNull(paramIndex, sqlType, typeName);
        else
            ps.setNull(paramIndex, sqlType);
    }

    public final static void setValue(PreparedStatement ps, int paramIndex,
                                      int sqlType, Integer scale, Object inValue) throws SQLException {
        if (sqlType == Types.VARCHAR || sqlType == Types.LONGVARCHAR
                || (sqlType == Types.CLOB && isStringValue(inValue.getClass()))) {
            ps.setString(paramIndex, inValue.toString());
        } else if (sqlType == Types.DECIMAL || sqlType == Types.NUMERIC) {
            if (inValue instanceof BigDecimal)
                ps.setBigDecimal(paramIndex, (BigDecimal) inValue);
            else if (scale != null)
                ps.setObject(paramIndex, inValue, sqlType, scale);
            else
                ps.setObject(paramIndex, inValue, sqlType);
        } else if (sqlType == Types.DATE) {
            if (inValue instanceof Date) {
                if (inValue instanceof java.sql.Date) {
                    ps.setDate(paramIndex, (java.sql.Date) inValue);
                } else {
                    ps.setDate(paramIndex, new java.sql.Date(
                            ((Date) inValue).getTime()));
                }
            } else if (inValue instanceof Calendar) {
                Calendar cal = (Calendar) inValue;
                ps.setDate(paramIndex, new java.sql.Date(cal.getTime()
                        .getTime()), cal);
            } else {
                ps.setObject(paramIndex, inValue, Types.DATE);
            }
        } else if (sqlType == Types.TIME) {
            if (inValue instanceof Date) {
                if (inValue instanceof java.sql.Time) {
                    ps.setTime(paramIndex, (java.sql.Time) inValue);
                } else {
                    ps.setTime(paramIndex, new java.sql.Time(
                            ((Date) inValue).getTime()));
                }
            } else if (inValue instanceof Calendar) {
                Calendar cal = (Calendar) inValue;
                ps.setTime(paramIndex, new java.sql.Time(cal.getTime()
                        .getTime()), cal);
            } else {
                ps.setObject(paramIndex, inValue, Types.TIME);
            }
        } else if (sqlType == Types.TIMESTAMP) {
            if (inValue instanceof Date) {
                if (inValue instanceof Timestamp) {
                    ps.setTimestamp(paramIndex, (Timestamp) inValue);
                } else {
                    ps.setTimestamp(paramIndex, new Timestamp(
                            ((Date) inValue).getTime()));
                }
            } else if (inValue instanceof Calendar) {
                Calendar cal = (Calendar) inValue;
                ps.setTimestamp(paramIndex, new Timestamp(cal
                        .getTime().getTime()), cal);
            } else {
                ps.setObject(paramIndex, inValue, Types.TIMESTAMP);
            }
        } else {
            if (isStringValue(inValue.getClass())) {
                ps.setString(paramIndex, inValue.toString());
            } else if (isDateValue(inValue.getClass())) {
                ps.setTimestamp(paramIndex, new Timestamp(
                        ((Date) inValue).getTime()));
            } else if (inValue instanceof Calendar) {
                Calendar cal = (Calendar) inValue;
                ps.setTimestamp(paramIndex, new Timestamp(cal
                        .getTime().getTime()), cal);
            } else {
                ps.setObject(paramIndex, inValue, sqlType);
            }
        }
    }

    private static boolean isStringValue(Class<?> inValueType) {
        return (CharSequence.class.isAssignableFrom(inValueType) || StringWriter.class
                .isAssignableFrom(inValueType));
    }

    private static boolean isDateValue(Class<?> inValueType) {
        return (Date.class.isAssignableFrom(inValueType) && !(java.sql.Date.class
                .isAssignableFrom(inValueType)
                || java.sql.Time.class.isAssignableFrom(inValueType) || Timestamp.class
                .isAssignableFrom(inValueType)));
    }

    public static int getSqlType(Object object) {
        if (object == null)
            return TYPE_UNKNOWN;
        return getSqlType(object.getClass());
    }

    public static int getSqlType(Class<?> javaType) {
        if (javaType == null) {
            return TYPE_UNKNOWN;
        } else if (javaType.isPrimitive()
                || javaType == Integer.class) {
            return Types.NUMERIC;
        } else if (javaType == Boolean.class) {
            return Types.BOOLEAN;
        } else if (javaType == Long.class) {
            return Types.NUMERIC;
        } else if (javaType == Float.class) {
            return Types.NUMERIC;
        } else if (javaType == Double.class) {
            return Types.NUMERIC;
        } else if (javaType == Date.class) {
            return Types.DATE;
        } else if (javaType == Timestamp.class) {
            return Types.TIME;
        } else if (javaType == String.class) {
            return Types.VARCHAR;
        } else if (javaType == Clob.class) {
            return Types.CLOB;
        } else if (javaType == Blob.class) {
            return Types.BLOB;
        } else if (javaType == Character.class) {
            return Types.CHAR;
        }
        return TYPE_UNKNOWN;
    }

    public static final int getSqlType(String javaType) {
        if (StringUtils.isEmpty(javaType)
                || "string".equals(javaType)
                || "text".equals(javaType)) {
            return Types.VARCHAR;
        } else if ("bool".equals(javaType)
                || "boolean".equals(javaType)) {
            return Types.BOOLEAN;
        } else if ("integer".equals(javaType)
                || "int".equals(javaType)) {
            return Types.INTEGER;
        } else if ("long".equals(javaType)) {
            return Types.DECIMAL;
        } else if ("float".equals(javaType)) {
            return Types.FLOAT;
        } else if ("double".equals(javaType)) {
            return Types.DOUBLE;
        } else if ("datetime".equals(javaType)
                || "timestamp".equals(javaType)) {
            return Types.TIMESTAMP;
        } else if ("date".equals(javaType)) {
            return Types.DATE;
        } else if ("time".equals(javaType)) {
            return Types.TIME;
        } else if ("string".equals(javaType)) {
            return Types.VARBINARY;
        } else if ("byte".equals(javaType)) {
            return Types.ARRAY;
        } else if ("char".equals(javaType)) {
            return Types.CHAR;
        }
        return Types.VARCHAR;
    }
}
