package com.sparrow.orm.shema.data;

import org.apache.commons.lang3.StringUtils;

public class TableColumn {
	private String name;
	private String fieldName;
	private String fieldNameX;
	private String type;
	private String desc;
	private String javaType;
	private String sampleType;
	private Class<?> classType;
	private int sqlType;
	private int size;
	private boolean isPrimary;
	private boolean notNull;

	public int getSqlType() {
		return sqlType;
	}

	public void setSqlType(int sqlType) {
		this.sqlType = sqlType;
	}

	public Class<?> getClassType() {
		return classType;
	}

	public void setClassType(Class<?> classType) {
		this.classType = classType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
		if (StringUtils.isNotEmpty(javaType)) {
			this.sampleType = javaType.substring(javaType.lastIndexOf('.') + 1);
		}
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldNameX() {
		return fieldNameX;
	}

	public void setFieldNameX(String fieldNameX) {
		this.fieldNameX = fieldNameX;
	}

	public String getSampleType() {
		return sampleType;
	}

	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}

	@Override
	public String toString() {
		return this.name + ", " + this.type + "(" + this.size + "), notNull="
				+ this.notNull + ", primaryKey=" + this.isPrimary + " --- "
				+ this.javaType + ", " + this.fieldName;
	}

}
