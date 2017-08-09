package com.sparrow.orm.dyna.common;

import java.io.Serializable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Tool {
	private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new HashMap<Class<?>, Class<?>>(
			8);
	private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<String, Class<?>>(
			32);
	private static Class<?> stringClass = String.class;

	static {
		primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
		primitiveWrapperTypeMap.put(Byte.class, byte.class);
		primitiveWrapperTypeMap.put(Character.class, char.class);
		primitiveWrapperTypeMap.put(Double.class, double.class);
		primitiveWrapperTypeMap.put(Float.class, float.class);
		primitiveWrapperTypeMap.put(Integer.class, int.class);
		primitiveWrapperTypeMap.put(Long.class, long.class);
		primitiveWrapperTypeMap.put(Short.class, short.class);

		Set<Class<?>> primitiveTypes = new HashSet<Class<?>>(32);
		primitiveTypes.addAll(primitiveWrapperTypeMap.values());
		primitiveTypes.addAll(Arrays.asList(new Class<?>[] { boolean[].class,
				byte[].class, char[].class, double[].class, float[].class,
				int[].class, long[].class, short[].class }));
		primitiveTypes.add(void.class);
		for (Class<?> primitiveType : primitiveTypes) {
			primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
		}
	}

	public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
		return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
	}

	public static boolean isPrimitiveWrapper(Class<?> clazz) {
		return primitiveWrapperTypeMap.containsKey(clazz);
	}

	public static boolean isSimpleValueType(Class<?> clazz) {
		return isPrimitiveOrWrapper(clazz) || clazz.isEnum()
				|| CharSequence.class.isAssignableFrom(clazz)
				|| Number.class.isAssignableFrom(clazz)
				|| Date.class.isAssignableFrom(clazz)
				|| clazz.equals(URI.class) || clazz.equals(URL.class)
				|| clazz.equals(Locale.class) || clazz.equals(Class.class);
	}

	public static boolean isSimpleProperty(Class<?> clazz) {
		return isSimpleValueType(clazz)
				|| (clazz.isArray() && isSimpleValueType(clazz
						.getComponentType()));
	}

	public static Class<?> resolvePrimitiveClassName(String name) {
		Class<?> result = null;
		if (name != null && name.length() <= 8) {
			result = primitiveTypeNameMap.get(name);
		}
		return result;
	}

	public static boolean isObject(Class<?> clazz) {
		return clazz == Object.class || clazz == Serializable.class;
	}

	public static boolean isInteger(Class<?> clazz) {
		return clazz == Integer.TYPE || clazz == Integer.class;
	}

	public static boolean isList(Class<?> clazz) {
		return List.class.isAssignableFrom(clazz)
				|| Set.class.isAssignableFrom(clazz);
	}

	public static boolean isArray(Class<?> type) {
		return type.isArray();
	}

	public static boolean isMap(Class<?> clazz) {
		return clazz == Object.class || Map.class.isAssignableFrom(clazz);
	}

	public static Class<?> getReturnType(Class<?> clazz) {
		if (List.class.isAssignableFrom(clazz)) {
			Method method;
			try {
				method = clazz.getMethod("get");
				return method.getReturnType();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		return clazz;
	}

	public static boolean isJavaClass(Class<?> clz) {
		return clz != null && clz.getClassLoader() == null;
	}

	public static boolean isVoid(Class<?> clazz) {
		if (clazz == null)
			return true;
		return clazz == Void.TYPE || clazz == Void.class;
	}

	public static boolean isBaseType(Class<?> clazz) {
		if (clazz == stringClass) {
			return true;
		} else if (clazz == Integer.TYPE || clazz == Integer.class) {
			return true;
		} else if (clazz == Boolean.TYPE || clazz == Boolean.class) {
			return true;
		} else if (clazz == Long.TYPE || clazz == Long.class) {
			return true;
		} else if (clazz == Double.TYPE || clazz == Double.class) {
			return true;
		} else if (clazz == Character.TYPE || clazz == Character.class) {
			return true;
		} else if (clazz == Byte.TYPE || clazz == Byte.class) {
			return true;
		} else if (clazz == Float.TYPE || clazz == Float.class) {
			return true;
		} else if (clazz == Short.TYPE || clazz == Short.class) {
			return true;
		} else if (clazz == java.util.Date.class
				|| clazz == java.sql.Date.class
				|| java.util.Date.class.isAssignableFrom(clazz)) {
			return true;
		} else if (clazz == java.sql.Timestamp.class) {
			return true;
		} else
			return false;
	}

	public static Class<?> getType(Class<?> clazz) {
		Type types[] = clazz.getTypeParameters();
		Type type = null;

		for (int i = 0; i < types.length; i++)
			System.out.println("d" + types[i]);
		if ((Set.class.isAssignableFrom(clazz) || List.class
				.isAssignableFrom(clazz)) && type instanceof ParameterizedType) {
			try {
				type = ((ParameterizedType) type).getActualTypeArguments()[0];
				if (type instanceof GenericArrayType)
					clazz = (Class<?>) ((GenericArrayType) type)
							.getGenericComponentType();
				else
					clazz = (Class<?>) type;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (Map.class.isAssignableFrom(clazz)
				&& type instanceof ParameterizedType) {
			try {
				type = ((ParameterizedType) type).getActualTypeArguments()[0];
				System.out.println(type);
				clazz = (Class<?>) type;
				System.out.println(clazz);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (Enum.class.isAssignableFrom(clazz)) {
			clazz = Enum.class;
		}

		return clazz;
	}

	public static boolean isNumeric(String str) {
		if (str == null)
			return false;
		int sz = str.length();
		for (int i = 0; i < sz; i++)
			if (Character.isDigit(str.charAt(i)) == false)
				return false;
		return true;
	}

	public void getX(List<Map<String, Object>> map) {

	}

	public static void main(String args[]) {
		// BeanUtils.isSimpleProperty(clazz);
		Class<?> c = null;
		try {
			Method mts[] = Tool.class.getMethods();
			for (int i = 0; i < mts.length; i++) {
				Method m = mts[i];
				if ("getX".equals(m.getName())) {
					c = m.getParameterTypes()[0];
					Type paramType = m.getGenericParameterTypes()[0];
					if (paramType instanceof ParameterizedType)/**//* 如果是泛型类型 */{
						Type[] types = ((ParameterizedType) paramType)
								.getActualTypeArguments();// 泛型类型列表
						System.out.println("  TypeArgument: ");
						for (Type type : types) {
							System.out.println("   " + type);
						}
					}
				}
			}
			System.out.println(getType(c));
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
}
