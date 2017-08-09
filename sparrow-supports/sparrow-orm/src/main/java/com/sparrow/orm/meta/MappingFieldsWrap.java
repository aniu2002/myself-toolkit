package com.sparrow.orm.meta;

import org.apache.commons.lang3.ArrayUtils;

import com.sparrow.orm.id.IdentifierGenerator;


/**
 * MappingFieldsWrap 包装了PO 属性对应的字段信息，主要是主键字段（联合主键）和普通字段信息<br/>
 * 根据 PK 生成策略，若是数据库自动生成策略，insert时将不在sql中绑定具体的值
 * 
 * @author YZC (2013-10-10-下午3:58:50)
 */
public class MappingFieldsWrap {
	private final Class<?> mapClass;
	// PO 绑定的主键字段
	private final MappingField[] primary;
	// PO 绑定的普通字段
	private final MappingField[] columns;
	private final MappingField[] fields;
	private IdentifierGenerator generator;
	private MappingField keyFeild;
	// PO 忽略的field数
	private final int ignores;
	// PO 对应的所有字段数
	private final int totals;

	public MappingFieldsWrap(Class<?> mapClass, MappingField[] primary,
			MappingField[] columns, int totals, int ignores) {
		this.mapClass = mapClass;
		this.primary = primary;
		this.columns = columns;
		if (primary == null || primary.length == 0)
			this.fields = columns;
		else
			this.fields = ArrayUtils.addAll(primary, columns);
		this.totals = totals;
		this.ignores = ignores;
	}

	public MappingField getKeyFeild() {
		return keyFeild;
	}

	public void setKeyFeild(MappingField keyFeild) {
		this.keyFeild = keyFeild;
	}

	public IdentifierGenerator getGenerator() {
		return generator;
	}

	public void setGenerator(IdentifierGenerator generator) {
		this.generator = generator;
	}

	public int getTotals() {
		return totals;
	}

	public Class<?> getMapClass() {
		return mapClass;
	}

	public MappingField[] getPrimary() {
		return primary;
	}

	public MappingField[] getColumns() {
		return columns;
	}

	public int getIgnores() {
		return ignores;
	}

	public MappingField[] getFields() {
		return fields;
	}
}
