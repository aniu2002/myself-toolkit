package com.sparrow.collect.orm.named;

import com.sparrow.collect.orm.utils.JdbcUtil;
import com.sparrow.collect.utils.BeanUtils;
import com.sparrow.collect.utils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

public class BeanParameterSource implements SqlParameterSource {
	private final Object bean;
	private Map<String, PropertyDescriptor> fields;

	public BeanParameterSource(Object bean) {
		this.bean = bean;
		this.initialize(bean.getClass());
	}

	protected void initialize(Class<?> mappedClass) {
		this.fields = new HashMap<String, PropertyDescriptor>();
		PropertyDescriptor[] pds = PropertyUtils
				.getPropertyDescriptors(mappedClass);
		for (PropertyDescriptor pd : pds) {
			if (pd.getReadMethod() != null) {
				this.fields.put(pd.getName(), pd);
			}
		}
	}

	@Override
	public int getSqlType(String paramName) {
		PropertyDescriptor pd = this.fields.get(paramName);
		return JdbcUtil.getSqlType(pd.getPropertyType());
	}

	@Override
	public Object getValue(String paramName) throws IllegalArgumentException {
		PropertyDescriptor pd = this.fields.get(paramName);
		return BeanUtils.getValue(pd, this.bean);
	}

	@Override
	public boolean hasValue(String paramName) {
		return this.fields.containsKey(paramName);
	}

}
