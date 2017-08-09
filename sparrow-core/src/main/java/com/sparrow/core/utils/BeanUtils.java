package com.sparrow.core.utils;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

public class BeanUtils {
	// 定义静态空参数，方便 getter 的 Method.invoke调用
	static final Object[] VOID_PARAS = new Object[0];

	public static void populate(Object bean, Map<String, Object> properties)
			throws IllegalAccessException, InvocationTargetException {

		if ((bean == null) || (properties == null))
			return;
		Iterator<String> names = properties.keySet().iterator();
		while (names.hasNext()) {
			String name = (String) names.next();
			if (name == null)
				continue;
			Object value = properties.get(name); // String or String[]
			PropertyDescriptor descriptor = null;
			try {
				// resolve nested reference
				descriptor = PropertyUtils.getPropertyDescriptor(bean, name,
						true);
			} catch (Throwable t) {
				descriptor = null;
			}
			if (descriptor == null) {
				continue;
			}
			Method setter = null;
			if (descriptor instanceof IndexedPropertyDescriptor)
				setter = ((IndexedPropertyDescriptor) descriptor)
						.getIndexedWriteMethod();
			if (setter == null)
				setter = descriptor.getWriteMethod();
			if (setter == null) {
				continue;
			}
			Class<?> parameterTypes[] = setter.getParameterTypes();
			Class<?> parameterType = parameterTypes[0];
			if (parameterTypes.length > 1)
				parameterType = parameterTypes[1]; // Indexed setter

			// Convert the parameter value as required for this setter method
			Object parameters[] = new Object[1];
			if (parameterTypes[0].isArray()) {
				if (value instanceof String) {
					String values[] = new String[1];
					values[0] = (String) value;
					parameters[0] = ConvertUtils.convert((String[]) values,
							parameterType);
				} else if (value instanceof String[]) {
					parameters[0] = ConvertUtils.convert((String[]) value,
							parameterType);
				} else {
					parameters[0] = value;
				}
			} else {
				if (value instanceof String) {
					parameters[0] = ConvertUtils.convert((String) value,
							parameterType);
				} else if (value instanceof String[]) {
					parameters[0] = ConvertUtils.convert(((String[]) value)[0],
							parameterType);
				} else {
					parameters[0] = value;
				}
			}
			try {
				PropertyUtils.setProperty(bean, name, parameters[0]);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				throw new RuntimeException("Can't found setter method for : "
						+ name);
			}

		}
	}

	public static <T> T instantiate(Class<T> clazz) {
		if (clazz.isInterface()) {
			throw new RuntimeException("Specified class is an interface : "
					+ clazz.getName());
		}
		try {
			return clazz.newInstance();
		} catch (InstantiationException ex) {
			throw new RuntimeException("Is it an abstract class?", ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException("Is the constructor accessible?", ex);
		}
	}

	public static Object getValue(PropertyDescriptor prop, Object obj) {
		Method read = prop.getReadMethod();
		try {
			Object result = read.invoke(obj, VOID_PARAS);
			return result;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setValue(PropertyDescriptor prop, Object obj,
			Object value) {
		Method write = prop.getWriteMethod();
		try {
			write.invoke(obj, new Object[] { value });
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
