package com.sparrow.orm.shema.mapper;

import java.beans.PropertyDescriptor;

public class FieldItem {
	private PropertyDescriptor prop;
	private Class<?> javaType;
	private int sqlType;
	private String field;
	private String column;

	public FieldItem(PropertyDescriptor prop, Class<?> javaType, int sqlType,
			String field, String column) {
		this.javaType = javaType;
		this.sqlType = sqlType;
		this.field = field;
		this.column = column;
	}

	public Class<?> getJavaType() {
		return javaType;
	}

	public void setJavaType(Class<?> javaType) {
		this.javaType = javaType;
	}

	public int getSqlType() {
		return sqlType;
	}

	public void setSqlType(int sqlType) {
		this.sqlType = sqlType;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public PropertyDescriptor getProp() {
		return prop;
	}

	public void setProp(PropertyDescriptor prop) {
		this.prop = prop;
	}
	
}
