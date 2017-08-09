package com.sparrow.tools.utils;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

public class BeanForceUtil {

	public static Class<?> loadClass(String className)
			throws ClassNotFoundException {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (classLoader == null) {
			classLoader = BeanForceUtil.class.getClassLoader();
		}
		return (classLoader.loadClass(className));
	}

	public static Object createInstance(String className) {
		try {
			return (loadClass(className).newInstance());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object createInstance(Class<?> claz) {
		try {
			return (claz.newInstance());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setBeanProperties(Object bean,
			Map<String, Object> properties) {
		if (bean == null || properties == null)
			return;
		Iterator<String> names = properties.keySet().iterator();
		String name;
		Object value;
		try {
			while (names.hasNext()) {
				name = (String) names.next();
				if (name == null)
					continue;
				value = properties.get(name);
				forceSetProperty(bean, name, value);
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 暴力设置对象变量值,忽略private,protected修饰符的限制.
	 * 
	 * @throws NoSuchFieldException
	 *             如果没有该Field时抛出.
	 */
	public static void forceSetProperty(Object object, String propertyName,
			Object newValue) throws NoSuchFieldException {
		Field field = getDeclaredField(object, propertyName);
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		try {
			field.set(object, newValue);
		} catch (IllegalAccessException e) {
		}
		field.setAccessible(accessible);
	}

	public static Object forceGetProperty(Object object, String propertyName)
			throws NoSuchFieldException {
		Field field = getDeclaredField(object, propertyName);
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		Object result = null;
		try {
			result = field.get(object);
		} catch (IllegalAccessException e) {
		}
		field.setAccessible(accessible);
		return result;
	}

	public static Field getDeclaredField(Class<?> clazz, String propertyName)
			throws NoSuchFieldException {
		try {
			return clazz.getDeclaredField(propertyName);
		} catch (NoSuchFieldException e) {
		}
		throw new NoSuchFieldException("No such field: " + clazz.getName()
				+ '.' + propertyName);
	}

	public static Field getDeclaredField(Object object, String propertyName)
			throws NoSuchFieldException {
		return getDeclaredField(object.getClass(), propertyName);
	}
}
