package com.sparrow.orm.meta;

import java.beans.PropertyDescriptor;

import com.sparrow.orm.id.IdentifierGenerator;


/**
 * MappingKeyField 针对PO属性与数据库表字段映射的元数据 <br/>
 * 记录PO field 名称 <br/>
 * 记录PO field 的 PropertyDescriptor <br/>
 * 记录PO field 对应的 数据库表列名称 ...
 * 
 * @author YZC (2013-10-10-下午3:26:07)
 */
public class MappingKeyField extends MappingField {
	// 记录PO 主键 field 对应的id生成策略
	private String generator;
	private IdentifierGenerator idGenerator;
	// 记录PO 是否 主键 field
	private boolean primary;
	// 记录PO 主键 field 由自动函数生成，如果有函数串提供
	// 如果:function.seq.nextval(),那么value里有seq.nextval()
	// 否则自动设置值
	// private boolean auto;
	// 字段填充 字符或者函数
	private String fillChar;

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
	public MappingKeyField(PropertyDescriptor prop, Class<?> javaType,
			int sqlType, String field, String column, boolean primary,
			boolean auto) {
		super(prop, javaType, sqlType, field, column, auto, false);
		this.primary = primary;
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
	 */
	public MappingKeyField(PropertyDescriptor prop, Class<?> javaType,
			int sqlType, String field, String column, boolean primary) {
		super(prop, javaType, sqlType, field, column);
		this.primary = primary;
	}

	public IdentifierGenerator getIdGenerator() {
		return idGenerator;
	}

	public void setIdGenerator(IdentifierGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	public String getGenerator() {
		return generator;
	}

	public void setGenerator(String generator) {
		this.generator = generator;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	@Override
	public String getFillChar() {
		return fillChar;
	}

	public void setFillChar(String fillChar) {
		this.fillChar = fillChar;
	}
}
