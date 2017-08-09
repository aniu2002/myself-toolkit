package com.sparrow.collect.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

public final class StringUtils {
	/**
	 * 
	 * <p>
	 * Description: judge the string is null or empty
	 * </p>
	 * 
	 * @param str
	 * @return
	 * @author Yzc
	 */
	public static boolean isNullOrEmpty(String str) {
		if (str == null || "".equals(str.trim()))
			return true;
		return false;
	}

	public static boolean hasLength(CharSequence str) {
		return (str != null && str.length() > 0);
	}

	public static boolean hasLength(String str) {
		return hasLength((CharSequence) str);
	}

	public static String replace(String inString, String oldPattern,
			String newPattern) {
		if (!hasLength(inString) || !hasLength(oldPattern)
				|| newPattern == null) {
			return inString;
		}
		StringBuilder sb = new StringBuilder();
		int pos = 0; // our position in the old string
		int index = inString.indexOf(oldPattern);
		// the index of an occurrence we've found, or -1
		int patLen = oldPattern.length();
		while (index >= 0) {
			sb.append(inString.substring(pos, index));
			sb.append(newPattern);
			pos = index + patLen;
			index = inString.indexOf(oldPattern, pos);
		}
		sb.append(inString.substring(pos));
		// remember to append any characters to the right of a match
		return sb.toString();
	}

	public static String[] tokenizeToStringArray(String str, String delimiters) {
		if (str == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(str, delimiters);
		List<String> tokens = new ArrayList<String>();
		String token;
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if (token.length() > 0) {
				token = token.trim();
				tokens.add(token);
			}
		}
		return toStringArray(tokens);
	}

	public static String[] toStringArray(Collection<String> collection) {
		if (collection == null) {
			return null;
		}
		return collection.toArray(new String[collection.size()]);
	}

    public static String removeSpecialChars(String src) {
        if (src == null)
            return "";
        StringBuilder result = new StringBuilder();
        for (char ch : src.toCharArray()) {
            //汉字
            if (Character.getType(ch) == Character.OTHER_LETTER) {
                result.append(ch);
            }
            //数字
            else if (Character.isDigit(ch)) {
                result.append(ch);
            }
            //字母
            else if (Character.isLetter(ch)) {
                result.append(ch);
            }
            //空格
            else if (Character.isSpaceChar(ch)) {
                result.append(ch);
            }
            //空白
            else if (Character.isWhitespace(ch)){
                result.append(ch);
            }
            //保留负号
            else if (ch == '-'){
                result.append(ch);
            }
            else{
                result.append(" ");
            }
        }
        return result.toString().replaceAll("\\s{2,}", " ");
    }

}
