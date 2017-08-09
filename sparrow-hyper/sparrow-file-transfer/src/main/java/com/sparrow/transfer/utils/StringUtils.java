package com.sparrow.transfer.utils;

public final class StringUtils {
	public static boolean isNullOrEmpty(String str) {
		if (str == null || "".equals(str.trim()))
			return true;
		return false;
	}

	/**
	 * 将字符串首字母大写
	 * 
	 * @param s
	 *            字符串
	 * @return 首字母大写后的新字符串
	 */
	public static String capitalize(CharSequence s) {
		if (null == s)
			return null;
		int len = s.length();
		if (len == 0)
			return "";
		char char0 = s.charAt(0);
		if (Character.isUpperCase(char0))
			return s.toString();
		StringBuilder sb = new StringBuilder(len);
		sb.append(Character.toUpperCase(char0)).append(s.subSequence(1, len));
		return sb.toString();
	}
}
