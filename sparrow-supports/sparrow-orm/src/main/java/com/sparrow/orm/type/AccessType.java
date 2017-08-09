package com.sparrow.orm.type;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.util.Date;

import com.sparrow.core.utils.StringUtils;


public class AccessType implements Type {

	public String getColumnType(String javaType) {
		if (StringUtils.isEmpty(javaType))
			return "VARCHAR";
		else if ("Integer".equals(javaType) || "int".equals(javaType))
			return "INTEGER";
		else if ("long".equals(javaType.toLowerCase()))
			return "LONG";
		else if ("float".equals(javaType.toLowerCase()))
			return "FLOAT";
		else if ("double".equals(javaType.toLowerCase()))
			return "DOUBLE";
		else if ("datetime".equals(javaType.toLowerCase())
				|| "date".equals(javaType.toLowerCase())
				|| "time".equals(javaType.toLowerCase()))
			return "Date";
		else if ("timestamp".equals(javaType.toLowerCase()))
			return "TimeStamp";
		else if ("string".equals(javaType.toLowerCase()))
			return "VARCHAR";
		else if ("text".equals(javaType.toLowerCase()))
			return "VARCHAR";
		else if ("byte".equals(javaType.toLowerCase()))
			return "Binary";
		else if ("char".equals(javaType.toLowerCase()))
			return "Char";
		return "VARCHAR";
	}

	public String getColumnTypeX(Object javaType) {
		if (javaType == null)
			return "VARCHAR";
		else if (javaType instanceof Integer)
			return "INTEGER";
		else if (javaType instanceof Long)
			return "LONG";
		else if (javaType instanceof Float)
			return "FLOAT";
		else if (javaType instanceof Double)
			return "DOUBLE";
		else if (javaType instanceof Date)
			return "Date";
		else if (javaType instanceof Timestamp)
			return "Time";
		else if (javaType instanceof String)
			return "VARCHAR";
		else if (javaType instanceof Clob)
			return "VARCHAR";
		else if (javaType instanceof Blob)
			return "Binary";
		else if (javaType instanceof Character)
			return "Char";
		return "VARCHAR";
	}

	public String getJavaType(Object javaType) {
		if (javaType == null)
			return "string";
		else if (javaType instanceof Integer)
			return "int";
		else if (javaType instanceof Long)
			return "long";
		else if (javaType instanceof Float)
			return "float";
		else if (javaType instanceof Double)
			return "double";
		else if (javaType instanceof Date)
			return "date";
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
		else if (javaType == Integer.class || javaType == int.class)
			return "int";
		else if (javaType == Long.class || javaType == long.class)
			return "long";
		else if (javaType == Float.class || javaType == float.class)
			return "float";
		else if (javaType == Double.class || javaType == double.class)
			return "double";
		else if (javaType == Date.class)
			return "date";
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
