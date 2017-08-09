package com.sparrow.common.jdbc;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

public class NameRule {
	/**
	 * 对象属性转换为字段 例如：userName to user_name
	 * 
	 * @param property
	 *            字段名
	 * @return
	 */
	public static String fieldToColumn(String property) {
		if (null == property) {
			return "";
		}
		char[] chars = property.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (char c : chars) {
			if (CharUtils.isAsciiAlphaUpper(c)) {
				sb.append("_" + StringUtils.lowerCase(CharUtils.toString(c)));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 字段转换成对象属性 例如：user_name to userName
	 * 
	 * @param field
	 * @return
	 */
	public static String columnTofield(String field) {
		if (null == field) {
			return "";
		}
		char[] chars = field.toLowerCase().toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c == '_') {
				int j = i + 1;
				if (j < chars.length) {
					sb.append(StringUtils.upperCase(CharUtils
							.toString(chars[j])));
					i++;
				}
			} else {
				sb.append(c);
			}
		}
		String n = sb.toString();
		if ("int".equals(n))
			n = "int_";
		else if ("long".equals(n))
			n = "long_";
		else if ("double".equals(n))
			n = "double_";
		else if ("short".equals(n))
			n = "short_";
		else if ("float".equals(n))
			n = "float_";
		else if ("char".equals(n))
			n = "char_";
		else if ("byte".equals(n))
			n = "byte_";
		else if ("boolean".equals(n))
			n = "boolean_";
		return n;
	}

	/**
	 * 字段转换成对象属性 例如：user_name to UserName
	 * 
	 * @param field
	 * @return
	 */
	public static String toBeanName(String field) {
		if (null == field) {
			return "";
		}
		char[] chars = field.toLowerCase().toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (i == 0) {
				sb.append(StringUtils.upperCase(CharUtils.toString(chars[i])));
				continue;
			}
			if (c == '_') {
				int j = i + 1;
				if (j < chars.length) {
					sb.append(StringUtils.upperCase(CharUtils
							.toString(chars[j])));
					i++;
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
