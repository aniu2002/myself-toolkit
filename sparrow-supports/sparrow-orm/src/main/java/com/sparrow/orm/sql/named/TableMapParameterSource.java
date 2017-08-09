package com.sparrow.orm.sql.named;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import com.sparrow.core.utils.BeanUtils;
import com.sparrow.orm.meta.MappingField;
import com.sparrow.orm.meta.MappingFieldsWrap;
import com.sparrow.orm.util.JdbcUtil;


public class TableMapParameterSource implements SqlParameterSource {
	private final Object bean;
	private Map<String, PropertyDescriptor> fieldsMap;

	public TableMapParameterSource(Object bean,
			MappingFieldsWrap mappingFieldsWrap) {
		this.bean = bean;
		this.initialize(mappingFieldsWrap.getFields());
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
		PropertyDescriptor pd = this.fieldsMap.get(paramName);
		return BeanUtils.getValue(pd, this.bean);
	}

	@Override
	public boolean hasValue(String paramName) {
		return this.fieldsMap.containsKey(paramName);
	}

}
