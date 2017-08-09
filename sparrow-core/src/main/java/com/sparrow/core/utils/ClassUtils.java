package com.sparrow.core.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ClassUtils {
	public static Class<?> loadClass(String className)
			throws ClassNotFoundException {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (classLoader == null) {
			classLoader = ClassUtils.class.getClassLoader();
		}
		return (classLoader.loadClass(className));
	}

	public static Class<?> loadClass(String className, ClassLoader loader)
			throws ClassNotFoundException {
		if (loader == null) {
			loader = Thread.currentThread().getContextClassLoader();
		}
		return (loader.loadClass(className));
	}

	public static Object instance(Class<?> clazz)
			throws InstantiationException, IllegalAccessException {
		return clazz.newInstance();
	}

	public static Object instance(String className)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException {
		return instance(loadClass(className));
	}

	public static <T> T instance(String className, Class<T> claz) {
		T p = null;
		Object s;
		try {
			s = instance(loadClass(className));
			p = claz.cast(s);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return p;
	}

	public static Method[] getAllDeclaredMethods(Class<?> klass, Class<?> top) {
		Class<?> cc = klass;
		HashMap<String, Method> map = new HashMap<String, Method>();
		while (null != cc && !(cc == Object.class)) {
			Method[] fs = cc.getDeclaredMethods();
			for (int i = 0; i < fs.length; i++) {
				String key = fs[i].getName()
						+ getParamDescriptor(fs[i].getParameterTypes());
				if (!map.containsKey(key))
					map.put(key, fs[i]);
			}
			cc = cc.getSuperclass() == top ? null : cc.getSuperclass();
		}
		return map.values().toArray(new Method[map.size()]);
	}

	/**
	 * 相当于 getAllDeclaredMethods(Object.class)
	 * 
	 * @return 方法数组
	 */
	public static Method[] getAllDeclaredMethodsWithoutTop(Class<?> klass) {
		return getAllDeclaredMethods(klass, Object.class);
	}

	/**
	 * @param parameterTypes
	 *            函数的参数类型数组
	 * @return 参数的描述符
	 */
	public static String getParamDescriptor(Class<?>[] parameterTypes) {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for (Class<?> pt : parameterTypes)
			sb.append(getTypeDescriptor(pt));
		sb.append(')');
		String s = sb.toString();
		return s;
	}

	/**
	 * @param klass
	 *            类型
	 * @return 获得一个类型的描述符
	 */
	public static String getTypeDescriptor(Class<?> klass) {
		if (klass.isPrimitive()) {
			if (klass == void.class)
				return "V";
			else if (klass == int.class)
				return "I";
			else if (klass == long.class)
				return "J";
			else if (klass == byte.class)
				return "B";
			else if (klass == short.class)
				return "S";
			else if (klass == float.class)
				return "F";
			else if (klass == double.class)
				return "D";
			else if (klass == char.class)
				return "C";
			else
				/* if(klass == boolean.class) */
				return "Z";
		}
		StringBuilder sb = new StringBuilder();
		if (klass.isArray()) {
			return sb.append('[')
					.append(getTypeDescriptor(klass.getComponentType()))
					.toString();
		}
		return sb.append('L').append(getPath(klass)).append(';').toString();
	}

	public static String getPath(Class<?> klass) {
		return klass.getName().replace('.', '/');
	}

	public static void setStaticValue(Class<?> klass, String fieldName,
			Object value) {
		try {
			Field field = getField(klass, fieldName);
			setStaticValue(null, field, value);
		} catch (NoSuchFieldException e1) {
			e1.printStackTrace();
		}
	}

	public static void setStaticValue(Object obj, Field field, Object value) {
		boolean acc = field.isAccessible();
		if (!acc)
			field.setAccessible(true);
		try {
			field.set(obj, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		field.setAccessible(acc);
	}

	public static Field getField(Class<?> klass, String name)
			throws NoSuchFieldException {
		Field f;
		while (null != klass && !(klass == Object.class)) {
			try {
				f = klass.getDeclaredField(name);
				return f;
			} catch (NoSuchFieldException e) {
				klass = klass.getSuperclass();
			}
		}
		throw new NoSuchFieldException(
				String.format(
						"Can NOT find field [%s] in class [%s] and it's parents classes",
						name, klass.getName()));
	}

	public static Method getSetter(Class<?> klass, String fieldName,
			Class<?> paramType) {
		try {
			String setterName = getSetterName(fieldName);
			try {
				return klass.getMethod(setterName, paramType);
			} catch (Exception e) {
				try {
					return klass.getMethod(fieldName, paramType);
				} catch (Exception e1) {
					throw new Exception();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.printf("Fail to find setter for [%s]->[%s(%s)]",
					klass.getName(), fieldName, paramType.getName());
			throw new RuntimeException(String.format(
					"Fail to find setter for [%s]->[%s(%s)]", klass.getName(),
					fieldName, paramType.getName()));
		}
	}

	public static String getSetterName(String fieldName) {
		return new StringBuilder("set").append(
				StringUtils.capitalize(fieldName)).toString();
	}

	public static Object born(Class<?> klass, Object... args) {
		if (args.length == 0)
			return born(klass);
		else {
			Class<?> parameterTypes[] = new Class[args.length];
			Object obj;
			for (int i = 0; i < args.length; i++) {
				obj = args[i];
				if (obj != null)
					parameterTypes[i] = obj.getClass();
				else
					parameterTypes[i] = Object.class;
			}

			try {
				Constructor<?> constructor = klass
						.getConstructor(parameterTypes);
				return constructor.newInstance(args);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public static Object born(Class<?> klass) {
		try {
			return klass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back to system
			// class loader...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = ClassUtils.class.getClassLoader();
		}
		return cl;
	}

	public static String classPackageAsResourcePath(Class<?> clazz) {
		if (clazz == null) {
			return "";
		}
		String className = clazz.getName();
		int packageEndIndex = className.lastIndexOf('.');
		if (packageEndIndex == -1) {
			return "";
		}
		String packageName = className.substring(0, packageEndIndex);
		return packageName.replace('.', '/');
	}
}
