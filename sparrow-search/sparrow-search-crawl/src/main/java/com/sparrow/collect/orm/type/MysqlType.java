package com.sparrow.collect.orm.type;

import org.apache.commons.lang3.StringUtils;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.util.Date;


public class MysqlType implements Type {

	public String getColumnType(String javaType) {
		if (StringUtils.isEmpty(javaType))
			return "VARCHAR";
		else if("bool".equals(javaType) || "boolean".equals(javaType))
			return "TINYINT";
		else if ("Integer".equals(javaType) || "int".equals(javaType))
			return "INTEGER";
		else if ("long".equals(javaType.toLowerCase()))
			return "LONG";
		else if ("float".equals(javaType.toLowerCase()))
			return "FLOAT";
		else if ("double".equals(javaType.toLowerCase()))
			return "DOUBLE";
		else if ("datetime".equals(javaType.toLowerCase()))
			return "DATETIME";
		else if ("date".equals(javaType.toLowerCase()))
			return "DATE";
		else if ("time".equals(javaType.toLowerCase()))
			return "TIME";
		else if ("timestamp".equals(javaType.toLowerCase()))
			return "TIMESTAMP";
		else if ("string".equals(javaType.toLowerCase()))
			return "VARCHAR";
		else if ("text".equals(javaType.toLowerCase()))
			return "TEXT";
		else if ("byte".equals(javaType.toLowerCase()))
			return "BLOB";
		else if ("char".equals(javaType.toLowerCase()))
			return "CHAR";
		return "VARCHAR";
	}

	public String getColumnTypeX(Object javaType) {
		if (javaType == null)
			return "VARCHAR";
		else if (javaType instanceof Integer)
			return "INTEGER";
		else if (javaType instanceof Boolean)
			return "TINYINT";
		else if (javaType instanceof Long)
			return "LONG";
		else if (javaType instanceof Float)
			return "FLOAT";
		else if (javaType instanceof Double)
			return "DOUBLE";
		else if (javaType instanceof Date)
			return "DATETIME";
		else if (javaType instanceof Timestamp)
			return "TIMESTAMP";
		else if (javaType instanceof String)
			return "VARCHAR";
		else if (javaType instanceof Clob)
			return "TEXT";
		else if (javaType instanceof Blob)
			return "BLOB";
		else if (javaType instanceof Character)
			return "CHAR";
		return "VARCHAR";
	}

	public String getJavaType(Object javaType) {
		if (javaType == null)
			return "string";
		else if (javaType instanceof Integer)
			return "int";
		else if (javaType instanceof Boolean)
			return "boolean";
		else if (javaType instanceof Long)
			return "long";
		else if (javaType instanceof Float)
			return "float";
		else if (javaType instanceof Double)
			return "double";
		else if (javaType instanceof Date)
			return "datetime";
		else if (javaType instanceof Timestamp)
			return "time";
		else if (javaType instanceof String)
			return "string";
		else if (javaType instanceof Clob)
			return "string";
		else if (javaType instanceof Blob)
			return "byte";
		else if (javaType instanceof Character)
			return "char";
		return "string";
	}

	public String getJavaTypeX(Class<?> javaType) {
		if (javaType == null)
			return "string";
		else if (javaType == Boolean.class || javaType == boolean.class)
			return "boolean";
		else if (javaType == Integer.class || javaType == int.class)
			return "int";
		else if (javaType == Long.class || javaType == long.class)
			return "long";
		else if (javaType == Float.class || javaType == float.class)
			return "float";
		else if (javaType == Double.class || javaType == double.class)
			return "double";
		else if (javaType == Date.class)
			return "datetime";
		else if (javaType == Timestamp.class)
			return "time";
		else if (javaType == String.class)
			return "string";
		else if (javaType == Clob.class)
			return "string";
		else if (javaType == Blob.class)
			return "byte";
		else if (javaType == Character.class)
			return "char";
		return "string";
	}

}
