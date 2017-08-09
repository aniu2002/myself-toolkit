package com.sparrow.data.tools.validate;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ValidatorUtils {
	public static Pattern createRegexPattern(String express) {
		return Pattern.compile(express);
	}

	public static String createRegexString(String filter) {
		if (StringUtils.isNotBlank(filter) && !"*".equals(filter)) {
			StringBuilder sb = new StringBuilder();
			String regex;
			boolean notFirst = false;
			for (StringTokenizer tokenizer = new StringTokenizer(filter, ","); tokenizer
					.hasMoreElements();) {
				if (!notFirst)
					notFirst = true;
				else
					sb.append('|');
				regex = tokenizer.nextToken().toLowerCase().replace(".", "\\.")
						.replace("?", ".?").replace("*", ".*");
				sb.append("(").append(regex).append(")");
			}
			return sb.toString();
		}
		return null;
	}

	static int parseInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
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

	public static boolean isNegativeInteger(String s) {
		int max = s.length();
		int idx = 0;
		boolean negative = false;
		if (max > 0) {
			char c = s.charAt(0);
			if (c == '+') {
				idx = 1;
			} else if (c == '-') {
				negative = true;
				idx = 1;
			}
		}
		for (int i = idx; i < max; i++) {
			if (Character.isDigit(s.charAt(i)) == false)
				return false;
		}
		return negative;
	}

	public static boolean isPositiveInteger(String s) {
		int max = s.length();
		int idx = 0;
		boolean positive = true;
		if (max > 0) {
			char c = s.charAt(0);
			if (c == '-') {
				idx = 1;
				positive = false;
			} else if (c == '+')
				idx = 1;
		}
		for (int i = idx; i < max; i++) {
			if (Character.isDigit(s.charAt(i)) == false)
				return false;
		}
		return positive;
	}

	public static boolean isRealNum(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isUpperCase(char c) {
		return Character.isUpperCase(c);
	}

	public static boolean isLowerCase(char c) {
		return Character.isLowerCase(c);
	}
}
