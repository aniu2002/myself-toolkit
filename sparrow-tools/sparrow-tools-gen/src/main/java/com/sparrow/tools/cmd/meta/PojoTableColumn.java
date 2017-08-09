package com.sparrow.tools.cmd.meta;

import org.apache.commons.lang3.StringUtils;

public class PojoTableColumn {
	// table column name
	private String name;
	// like user
	private String fieldName;
	// like User for set or get
	private String fieldNameX;
	// table column type e. VARCHAR
	private String type;
	// comments for column
	private String desc;
	// java type e. java.lang.String
	private String javaType;
	// sample type e. String
	private String sampleType;
	// e. java.lang.String
	private Class<?> classType;
	private String render;
	private String numberType;
	// sql Type, java.sql.Types.INTEGER
	// table column name
	private String generator;
	private int sqlType;
	private int size;
	private boolean primary;
	private boolean notNull;

	public String getGenerator() {
		return generator;
	}

	public void setGenerator(String generator) {
		this.generator = generator;
	}

	public int getSqlType() {
		return sqlType;
	}

	public void setSqlType(int sqlType) {
		this.sqlType = sqlType;
	}

	public String getNumberType() {
		return numberType;
	}

	public void setNumberType(String numberType) {
		this.numberType = numberType;
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

	public String getRender() {
		return render;
	}

	public void setRender(String render) {
		this.render = render;
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
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
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
				+ this.notNull + ", primaryKey=" + this.primary + " --- "
				+ this.javaType + ", " + this.fieldName;
	}

}
