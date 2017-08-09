package com.sparrow.orm.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class PropsUtils {
	public static final Map<String, PropertyDescriptor[]> descriptorsCache = new HashMap<String, PropertyDescriptor[]>();

	public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) {
		if (clazz == null)
			throw new IllegalArgumentException("No bean specified");
		String beanClassName = clazz.getName();
		PropertyDescriptor descriptors[] = null;
		descriptors = (PropertyDescriptor[]) descriptorsCache
				.get(beanClassName);
		if (descriptors != null)
			return (descriptors);
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			return (new PropertyDescriptor[0]);
		}
		descriptors = beanInfo.getPropertyDescriptors();
		if (descriptors == null)
			descriptors = new PropertyDescriptor[0];
		descriptorsCache.put(beanClassName, descriptors);
		return descriptors;
	}

	public static Method getWriteMethod(PropertyDescriptor descriptor) {
		return descriptor.getWriteMethod();
	}

	public static Method getReadMethod(PropertyDescriptor descriptor) {
		return descriptor.getReadMethod();
	}
}
