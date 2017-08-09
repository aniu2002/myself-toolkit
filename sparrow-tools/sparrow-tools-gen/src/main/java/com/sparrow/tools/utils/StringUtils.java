package com.sparrow.tools.utils;

import java.io.PrintWriter;
import java.util.*;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 12-11-28 Time: 上午10:51 To change
 * this template use File | Settings | File Templates.
 */
public class StringUtils {
	public static boolean isEmpty(String str) {
		return str == null || "".equals(str.trim());
	}

	public static String[] tokenizeToStringArray(String str, String delimiters) {
		StringTokenizer st = new StringTokenizer(str, delimiters);
		List<String> tokens = new ArrayList<String>();
		boolean trimTokens = true, ignoreEmptyTokens = true;
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || token.length() > 0) {
				tokens.add(token);
			}
		}
		return toStringArray(tokens);
	}

	public static String[] toStringArray(Collection<?> collection) {
		if (collection == null) {
			return null;
		}
		return collection.toArray(new String[collection.size()]);
	}

	public static String stringReplacing(String str) {
		String title = str;
		title = title.replaceAll("&quot;", " ");
		title = title.replaceAll("&amp;", " ");
		title = title.replaceAll("&lt;", " ");
		title = title.replaceAll("&gt;", " ");

		title = title.replaceAll("/", " ");
		// title=title.replaceAll("\\", " ");
		title = title.replaceAll(";", " ");
		title = title.replaceAll("'", " ");
		title = title.replaceAll("\"", " ");
		title = title.replaceAll("&", " ");
		title = title.replaceAll("￥", " ");
		title = title.replaceAll("\\*", " ");
		title = title.replaceAll("\\$", " ");
		title = title.replaceAll(":", " ");
		// title=title.replaceAll("：", " ");
		title = title.replaceAll("\\?", " ");
		title = title.replaceAll("<", " ");
		title = title.replaceAll(">", " ");
		// title =title.replaceAll("《", " ");
		// title =title.replaceAll("》", " ");
		// title=title.replaceAll("\\？", " ");
		return title;

	}

	static final String AND_SIGN = ",";
	static final String EQUAL_SIGN = "=";

	public static Map<String, String> parserParas(String str) {
		if (str == null || "".equals(str.trim()))
			return null;
		Map<String, String> map = new HashMap<String, String>();
		StringTokenizer st = new StringTokenizer(str, AND_SIGN);
		String tmpStr = "";
		while (st.hasMoreTokens()) {
			tmpStr = (String) st.nextElement();
			String[] strings = tmpStr.split(EQUAL_SIGN);
			if (strings.length == 2)
				map.put(strings[0], strings[1]);
			else
				map.put(strings[0], "");
		}
		return map;
	}

	/**
	 * @param s
	 *            需要填充的字串
	 * @param len
	 *            填充后的长度
	 * @param c
	 *            指定的填充字符
	 * @return 右填充零后的字串
	 */
	public static String padRight(String s, int len, char c) {
		if (s == null) {
			s = "";
		}
		s = s.trim();
		if (s.getBytes().length >= len) {
			return s;
		}
		int fill = len - s.getBytes().length;
		StringBuffer d = new StringBuffer(s);
		while (fill-- > 0) {
			d.append(c);
		}
		return d.toString();
	}

	public static void padRight(PrintWriter sb, int s, int len) {
		padRight(sb, String.valueOf(s), len);
	}

	public static void padRight(PrintWriter sb, long s, int len) {
		padRight(sb, String.valueOf(s), len);
	}

	public static void padRight(PrintWriter sb, String s, int len) {
		int length = getSepLen(s);
		length = length / 8;
		sb.append(s);
		if (length > len) {
			sb.append('\t');
			return;
		}
		for (int i = length; i < len; i++)
			sb.append('\t');
	}

	public static void genRight(StringBuilder sb, int s, int len) {
		genRight(sb, String.valueOf(s), len);
	}

	public static void genRight(StringBuilder sb, long s, int len) {
		genRight(sb, String.valueOf(s), len);
	}

	public static void genRight(StringBuilder sb, String s, int len) {
		int length = getSepLen(s);
		length = length / 8;
		sb.append(s);
		if (length > len) {
			sb.append('\t');
			return;
		}
		for (int i = length; i < len; i++)
			sb.append('\t');
	}

	public static int getSepLen(String input) {
		if (isEmpty(input))
			return 0;
		int count = 0;
		char[] chars = input.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			count = (c <= 0xff) ? count + 1 : count + 2;
		}
		return count;
	}

	public static String padRight(int s, int len, char c) {
		return padRight(String.valueOf(s), len, c);
	}

	public static String padRight(long s, int len, char c) {
		return padRight(String.valueOf(s), len, c);
	}

	public static String padLeft(String s, int len, char c) {
		if (s == null) {
			s = "";
		}
		s = s.trim();
		int n = s.length();
		if (n >= len) {
			return s;
		}
		int fill = len - n;
		StringBuffer d = new StringBuffer();
		while (fill-- > 0) {
			d.append(c);
		}
		d.append(s);
		return d.toString();
	}
}
