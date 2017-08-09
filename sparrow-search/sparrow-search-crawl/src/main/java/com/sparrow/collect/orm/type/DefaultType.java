package com.sparrow.collect.orm.type;

import org.apache.commons.lang3.StringUtils;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.util.Date;

public class DefaultType implements Type {

	public String getColumnType(String javaType) {
		if (StringUtils.isEmpty(javaType))
			return "varchar";
		else if ("integer".equals(javaType.toLowerCase())
				|| "int".equals(javaType))
			return "integer";
		else if ("long".equals(javaType.toLowerCase()))
			return "long";
		else if ("float".equals(javaType.toLowerCase()))
			return "number";
		else if ("double".equals(javaType.toLowerCase()))
			return "number";
		else if ("datetime".equals(javaType.toLowerCase())
				|| "date".equals(javaType.toLowerCase())
				|| "time".equals(javaType.toLowerCase()))
			return "datetime";
		else if ("timestamp".equals(javaType.toLowerCase()))
			return "timestamp";
		else if ("string".equals(javaType.toLowerCase()))
			return "varchar";
		else if ("text".equals(javaType.toLowerCase()))
			return "clob";
		else if ("byte".equals(javaType.toLowerCase()))
			return "blob";
		else if ("char".equals(javaType.toLowerCase()))
			return "char";
		return "varchar";
	}

	public String getColumnTypeX(Object javaType) {
		if (javaType == null)
			return "varchar";
		else if (javaType instanceof Integer)
			return "integer";
		else if (javaType instanceof Long)
			return "long";
		else if (javaType instanceof Float)
			return "number";
		else if (javaType instanceof Double)
			return "number";
		else if (javaType instanceof Date)
			return "datetime";
		else if (javaType instanceof Timestamp)
			return "timestamp";
		else if (javaType instanceof String)
			return "varchar";
		else if (javaType instanceof Clob)
			return "clob";
		else if (javaType instanceof Blob)
			return "blob";
		else if (javaType instanceof Character)
			return "char";
		return "varchar";
	}

	public String getJavaType(Object javaType) {
		return null;
	}

	public String getJavaTypeX(Class<?> javaType) {
		return null;
	}

}
