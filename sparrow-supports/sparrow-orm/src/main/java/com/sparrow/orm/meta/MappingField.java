package com.sparrow.orm.meta;

import java.beans.PropertyDescriptor;

/**
 * MappingField 针对PO属性与数据库表字段映射的元数据 <br/>
 * 记录PO field 名称 <br/>
 * 记录PO field 的 PropertyDescriptor <br/>
 * 记录PO field 对应的 数据库表列名称 ...
 * 
 * @author YZC (2013-10-10-下午3:26:07)
 */
public class MappingField {
	// 记录PO field 的 PropertyDescriptor
	private PropertyDescriptor prop;
	// 记录PO field 的 java 类型
	private Class<?> javaType;
	// 记录PO field 的 名称
	private String field;
	// 记录PO field 对应的数据表列名
	private String column;
	// 记录PO field 对应的sql列类型
	private int sqlType;
	// 是否插入的时候忽略该字段
	private final boolean ignoreInsert;
	// 是否更新的时候忽略该字段
	private final boolean ignoreUpdate;

	/**
	 * @param prop
	 *            PO field 的 PropertyDescriptor
	 * @param javaType
	 *            记录PO field 的 java 类型
	 * @param sqlType
	 *            记录PO field 对应的sql列类型
	 * @param field
	 *            记录PO field 的 名称
	 * @param column
	 *            记录PO field 对应的数据表列名
	 * @param primary
	 *            记录PO 是否 主键 field
	 * @param ignore
	 *            记录PO 主键 field 根据生成策略是否在sql里忽略掉该字段
	 */
	public MappingField(PropertyDescriptor prop, Class<?> javaType,
			int sqlType, String field, String column) {
		this(prop, javaType, sqlType, field, column, false, false);
	}

	/**
	 * @param prop
	 *            PO field 的 PropertyDescriptor
	 * @param javaType
	 *            记录PO field 的 java 类型
	 * @param sqlType
	 *            记录PO field 对应的sql列类型
	 * @param field
	 *            记录PO field 的 名称
	 * @param column
	 *            记录PO field 对应的数据表列名
	 * @param primary
	 *            记录PO 是否 主键 field
	 * @param ignoreInsert
	 *            记录PO 主键 field 根据生成策略是否在sql里忽略掉该字段
	 * @param ignoreUpdate
	 *            记录PO 主键 field 根据生成策略是否在sql里忽略掉该字段
	 */
	public MappingField(PropertyDescriptor prop, Class<?> javaType,
			int sqlType, String field, String column, boolean ignoreInsert,
			boolean ignoreUpdate) {
		this.prop = prop;
		this.javaType = javaType;
		this.sqlType = sqlType;
		this.field = field;
		this.column = column;
		this.ignoreInsert = ignoreInsert;
		this.ignoreUpdate = ignoreUpdate;
	}

	public boolean isIgnoreInsert() {
		return ignoreInsert;
	}

	public boolean isIgnoreUpdate() {
		return ignoreUpdate;
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

	public boolean isPrimary() {
		return false;
	}

	public String getFillChar() {
		return null;
	}
}
