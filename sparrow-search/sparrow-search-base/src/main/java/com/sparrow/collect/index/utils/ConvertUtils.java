package com.sparrow.collect.index.utils;

import org.apache.commons.lang3.StringUtils;

public class ConvertUtils {
	private static Class<?> stringClass = String.class;
	private static Boolean defaultBoolean = Boolean.FALSE;
	private static Byte defaultByte = new Byte((byte) 0);
	private static Character defaultCharacter = new Character(' ');
	private static Double defaultDouble = new Double((double) 0.0);
	private static Float defaultFloat = new Float((float) 0.0);
	private static Integer defaultInteger = new Integer(0);
	private static Long defaultLong = new Long((long) 0);
	private static Short defaultShort = new Short((short) 0);

	public static Object convert(String value, Class<?> clazz) {
		if (clazz == stringClass) {
			if (value == null)
				return ((String) null);
			else
				return (value);
		} else if (clazz == Integer.TYPE) {
			return (convertInteger(value, defaultInteger));
		} else if (clazz == Boolean.TYPE) {
			return (convertBoolean(value, defaultBoolean));
		} else if (clazz == Long.TYPE) {
			return (convertLong(value, defaultLong));
		} else if (clazz == Double.TYPE) {
			return (convertDouble(value, defaultDouble));
		} else if (clazz == Character.TYPE) {
			return (convertCharacter(value, defaultCharacter));
		} else if (clazz == Byte.TYPE) {
			return (convertByte(value, defaultByte));
		} else if (clazz == Float.TYPE) {
			return (convertFloat(value, defaultFloat));
		} else if (clazz == Short.TYPE) {
			return (convertShort(value, defaultShort));
		} else if (clazz == Integer.class) {
			return (convertInteger(value, null));
		} else if (clazz == Boolean.class) {
			return (convertBoolean(value, null));
		} else if (clazz == Long.class) {
			return (convertLong(value, null));
		} else if (clazz == Double.class) {
			return (convertDouble(value, null));
		} else if (clazz == Character.class) {
			return (convertCharacter(value, null));
		} else if (clazz == Byte.class) {
			return (convertByte(value, null));
		} else if (clazz == Float.class) {
			return (convertFloat(value, null));
		} else if (clazz == Short.class) {
			return (convertShort(value, null));
		} else if (clazz == java.util.Date.class) {
			return (convertDate(value));
		} else if (clazz == java.sql.Date.class) {
			return (convertSqlDate(value));
		} else if (clazz == java.sql.Time.class) {
			return (convertSqlTime(value));
		} else if (clazz == java.sql.Timestamp.class) {
			return (convertSqlTimestamp(value));
		} else {
			if (value == null)
				return ((String) null);
			else
				return (value.toString());
		}
	}

	public static Object convert(String values[], Class<?> clazz) {
		Class<?> type = clazz.getComponentType();
		if (type == stringClass) {
			if (values == null)
				return null;
			else
				return values;
		}

		int len = values.length;

		if (type == Integer.TYPE) {
			int array[] = new int[len];
			for (int i = 0; i < len; i++)
				array[i] = convertInteger(values[i], defaultInteger).intValue();
			return (array);
		} else if (type == Boolean.TYPE) {
			boolean array[] = new boolean[len];
			for (int i = 0; i < len; i++)
				array[i] = convertBoolean(values[i], defaultBoolean)
						.booleanValue();
			return (array);
		} else if (type == Long.TYPE) {
			long array[] = new long[len];
			for (int i = 0; i < len; i++)
				array[i] = convertLong(values[i], defaultLong).longValue();
			return (array);
		} else if (type == Double.TYPE) {
			double array[] = new double[len];
			for (int i = 0; i < len; i++)
				array[i] = convertDouble(values[i], defaultDouble)
						.doubleValue();
			return (array);
		} else if (type == Character.TYPE) {
			char array[] = new char[len];
			for (int i = 0; i < len; i++)
				array[i] = convertCharacter(values[i], defaultCharacter)
						.charValue();
			return (array);
		} else if (type == Byte.TYPE) {
			byte array[] = new byte[len];
			for (int i = 0; i < len; i++)
				array[i] = convertByte(values[i], defaultByte).byteValue();
			return (array);
		} else if (type == Float.TYPE) {
			float array[] = new float[len];
			for (int i = 0; i < len; i++)
				array[i] = convertFloat(values[i], defaultFloat).floatValue();
			return (array);
		} else if (type == Short.TYPE) {
			short array[] = new short[len];
			for (int i = 0; i < len; i++)
				array[i] = convertShort(values[i], defaultShort).shortValue();
			return (array);
		} else if (type == Integer.class) {
			Integer array[] = new Integer[len];
			for (int i = 0; i < len; i++)
				array[i] = convertInteger(values[i], null);
			return (array);
		} else if (type == Boolean.class) {
			Boolean array[] = new Boolean[len];
			for (int i = 0; i < len; i++)
				array[i] = convertBoolean(values[i], null);
			return (array);
		} else if (type == Long.class) {
			Long array[] = new Long[len];
			for (int i = 0; i < len; i++)
				array[i] = convertLong(values[i], null);
			return (array);
		} else if (type == Double.class) {
			Double array[] = new Double[len];
			for (int i = 0; i < len; i++)
				array[i] = convertDouble(values[i], null);
			return (array);
		} else if (type == Character.class) {
			Character array[] = new Character[len];
			for (int i = 0; i < len; i++)
				array[i] = convertCharacter(values[i], null);
			return (array);
		} else if (type == Byte.class) {
			Byte array[] = new Byte[len];
			for (int i = 0; i < len; i++)
				array[i] = convertByte(values[i], null);
			return (array);
		} else if (type == Float.class) {
			Float array[] = new Float[len];
			for (int i = 0; i < len; i++)
				array[i] = convertFloat(values[i], null);
			return (array);
		} else if (type == Short.class) {
			Short array[] = new Short[len];
			for (int i = 0; i < len; i++)
				array[i] = convertShort(values[i], null);
			return (array);
		} else {
			String array[] = new String[len];
			for (int i = 0; i < len; i++)
				array[i] = values[i].toString();
			return (array);
		}
	}

	private static Boolean convertBoolean(String value, Boolean defaultValue) {
		if (value == null)
			return (defaultValue);
		else if (value.equalsIgnoreCase("yes")
				|| value.equalsIgnoreCase("true")
				|| value.equalsIgnoreCase("on"))
			return (Boolean.TRUE);
		else if (value.equalsIgnoreCase("no")
				|| value.equalsIgnoreCase("false")
				|| value.equalsIgnoreCase("off"))
			return (Boolean.FALSE);
		else
			return (defaultValue);
	}

	private static Byte convertByte(String value, Byte defaultValue) {
		if (StringUtils.isEmpty(value))
			return defaultValue;
		try {
			return new Byte(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	private static Character convertCharacter(String value,
			Character defaultValue) {
		if (StringUtils.isEmpty(value))
			return defaultValue;
		else if (value.length() == 0)
			return new Character(' ');
		else
			return defaultValue;

	}

	private static Double convertDouble(String value, Double defaultValue) {
		if (StringUtils.isEmpty(value))
			return defaultValue;
		try {
			return new Double(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	private static Float convertFloat(String value, Float defaultValue) {
		if (StringUtils.isEmpty(value))
			return defaultValue;
		try {
			return new Float(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}

	}

	private static Integer convertInteger(String value, Integer defaultValue) {
		if (StringUtils.isEmpty(value))
			return defaultValue;
		try {
			return new Integer(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	private static Long convertLong(String value, Long defaultValue) {
		if (StringUtils.isEmpty(value))
			return defaultValue;
		try {
			return new Long(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}

	}

	private static Short convertShort(String value, Short defaultValue) {
		if (StringUtils.isEmpty(value))
			return defaultValue;
		try {
			return new Short(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	private static java.util.Date convertDate(String value) {
		if (StringUtils.isEmpty(value))
			return new java.util.Date();
		try {
			if (value.indexOf(':') != -1)
				return TimeUtils.string2Date(TimeUtils.DATE_STANDARD_FORMAT,
                        value);
			else
				return TimeUtils
						.string2Date(TimeUtils.YYYY_MM_DD_FORMAT, value);
		} catch (NumberFormatException e) {
			return new java.util.Date();
		}
	}

	private static java.sql.Date convertSqlDate(String value) {
		if (StringUtils.isEmpty(value))
			return new java.sql.Date(System.currentTimeMillis());
		try {
			java.util.Date da;
			if (value.indexOf(':') != -1)
				da = TimeUtils.string2Date(TimeUtils.DATE_STANDARD_FORMAT,
						value);
			else
				da = TimeUtils.string2Date(TimeUtils.YYYY_MM_DD_FORMAT, value);
			return new java.sql.Date(da.getTime());
		} catch (NumberFormatException e) {
			return new java.sql.Date(System.currentTimeMillis());
		}
	}

	private static java.sql.Time convertSqlTime(String value) {
		if (StringUtils.isEmpty(value))
			return new java.sql.Time(System.currentTimeMillis());
		try {
			java.util.Date da;
			if (value.indexOf(':') != -1)
				da = TimeUtils.string2Date(TimeUtils.DATE_STANDARD_FORMAT,
						value);
			else
				da = TimeUtils.string2Date(TimeUtils.YYYY_MM_DD_FORMAT, value);
			return new java.sql.Time(da.getTime());
		} catch (NumberFormatException e) {
			return new java.sql.Time(System.currentTimeMillis());
		}
	}

	private static java.sql.Timestamp convertSqlTimestamp(String value) {
		if (StringUtils.isEmpty(value))
			return new java.sql.Timestamp(System.currentTimeMillis());
		try {
			java.util.Date da;
			if (value.indexOf(':') != -1)
				da = TimeUtils.string2Date(TimeUtils.DATE_STANDARD_FORMAT,
						value);
			else
				da = TimeUtils.string2Date(TimeUtils.YYYY_MM_DD_FORMAT, value);
			return new java.sql.Timestamp(da.getTime());
		} catch (NumberFormatException e) {
			return new java.sql.Timestamp(System.currentTimeMillis());
		}
	}

	public static void main(String args[]) {
		convert("1", Short.class);
	}
}
