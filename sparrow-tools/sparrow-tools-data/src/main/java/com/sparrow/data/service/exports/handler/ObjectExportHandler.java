package com.sparrow.data.service.exports.handler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ObjectExportHandler extends ExportHandler<Object> {
	// 定义静态空参数，方便 getter 的 Method.invoke调用
	static final Object[] VOID_PARAS = new Object[0];
	/** 对于对象列表导入时，记录对象property信息 */
	private PropertyDescriptor[] propertyDescriptors;

	public PropertyDescriptor[] getPropertyDescriptors() {
		return propertyDescriptors;
	}

	public void setPropertyDescriptors(PropertyDescriptor[] propertyDescriptors) {
		this.propertyDescriptors = propertyDescriptors;
	}

	@Override
	public String fetchValue(Object t, int dataIndex) {
		PropertyDescriptor pd = this.propertyDescriptors[dataIndex];
		return (String) getValue(pd, t);
	}

	static String getValue(PropertyDescriptor prop, Object obj) {
		if (prop == null)
			return null;
		Method read = prop.getReadMethod();
		try {
			Object result = read.invoke(obj, VOID_PARAS);
			if (result != null) {
				if (result instanceof String)
					return (String) result;
				else
					return result.toString();
			} else
				return null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}
