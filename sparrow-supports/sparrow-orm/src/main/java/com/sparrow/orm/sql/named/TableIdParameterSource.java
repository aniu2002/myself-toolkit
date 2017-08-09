package com.sparrow.orm.sql.named;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import com.sparrow.core.utils.BeanUtils;
import com.sparrow.orm.meta.MappingField;
import com.sparrow.orm.meta.MappingFieldsWrap;
import com.sparrow.orm.util.JdbcUtil;


public class TableIdParameterSource implements SqlParameterSource {
	private final Object bean;
	private Map<String, PropertyDescriptor> fieldsMap;
	private final int pLen;

	public TableIdParameterSource(Object bean,
			MappingFieldsWrap mappingFieldsWrap) {
		this.bean = bean;
		this.pLen = mappingFieldsWrap.getPrimary() == null ? 0
				: mappingFieldsWrap.getPrimary().length;
		MappingField[] fields = mappingFieldsWrap.getPrimary();
		if (this.pLen == 0) {
			fields = mappingFieldsWrap.getColumns();
		}
		this.initialize(fields);
	}

	protected void initialize(MappingField[] fields) {
		this.fieldsMap = new HashMap<String, PropertyDescriptor>();
		for (MappingField f : fields) {
			PropertyDescriptor pd = f.getProp();
			if (pd.getReadMethod() != null) {
				this.fieldsMap.put(pd.getName(), pd);
			}
		}
	}

	@Override
	public int getSqlType(String paramName) {
		PropertyDescriptor pd = this.fieldsMap.get(paramName);
		return JdbcUtil.getSqlType(pd.getPropertyType());
	}

	@Override
	public Object getValue(String paramName) throws IllegalArgumentException {
		if (this.pLen == 1)
			return this.bean;
		PropertyDescriptor pd = this.fieldsMap.get(paramName);
		return BeanUtils.getValue(pd, this.bean);
	}

	@Override
	public boolean hasValue(String paramName) {
		return this.fieldsMap.containsKey(paramName);
	}

}
