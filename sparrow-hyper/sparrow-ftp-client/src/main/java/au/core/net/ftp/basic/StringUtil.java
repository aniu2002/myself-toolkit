package au.core.net.ftp.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

public class StringUtil {

	public static String killNull(String text) {
		if (text == null) {
			return "";
		} else {
			return text;
		}
	}

	public static boolean isNullOrEmpty(String text) {
		if (text == null || "".equals(text.trim())) {
			return true;
		} else {
			return false;
		}
	}

	public static String generateIdsQuery(String str) {
		if (StringUtil.isNullOrEmpty(str))
			return null;
		boolean flag = str.indexOf(',') == -1;
		if (flag)
			return null;
		String ids[] = str.split(",");
		String istr = "";
		for (int i = 0; i < ids.length; i++) {
			if (i == 0)
				istr += "'" + ids[i] + "'";
			else
				istr += ",'" + ids[i] + "'";
		}
		if (!"".equals(istr.trim())) {
			istr = "(" + istr + ")";
			return istr;
		}
		return null;
	}

	/**
	 * 以指定的字符右填充
	 * 
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

	/**
	 * 以指定的字符右填充,如果源字符串长度大于等于指定的长度，原样返回，否则补位
	 * 
	 * @param value
	 *            需要填充的字串
	 * @param maxLen
	 *            填充后的长度
	 * @param way
	 *            填充字符的方向
	 * @return 充后的字串
	 */
	public static String addString(String value, int maxLen, String way) {
		int length = maxLen - value.length();
		String ret = value;

		char[] letter = new char[length];

		if (length > 0) { // 判断要填充的个数
			java.util.Arrays.fill(letter, 'A'); // 填充的字符可自定义，也可传入指定的参数
			String temp = new String(letter);

			if (way.equals("L"))
				ret = temp + ret; // 左补位
			else
				ret = ret + temp;// 右补位
		}
		return ret;
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
		return (String[]) collection.toArray(new String[collection.size()]);
	}
}
