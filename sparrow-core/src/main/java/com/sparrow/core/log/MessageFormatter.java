package com.sparrow.core.log;

public class MessageFormatter {
	static final char DELIM_START = '{';
	static final char DELIM_STOP = '}';
	static final String DELIM_STR = "{}";
	private static final char ESCAPE_CHAR = '\\';

	public static final String format(String messagePattern, Object arg) {
		return arrayFormat(messagePattern, new Object[] { arg });
	}

	public static final String format(String messagePattern, Object arg1,
			Object arg2) {
		return arrayFormat(messagePattern, new Object[] { arg1, arg2 });
	}

	public static final String arrayFormat(String messagePattern,
			Object[] argArray) {
		if (messagePattern == null) {
			return null;
		}
		if (argArray == null) {
			return messagePattern;
		}
		int i = 0;

		StringBuffer sbuf = new StringBuffer(messagePattern.length() + 50);

		for (int L = 0; L < argArray.length; ++L) {
			int j = messagePattern.indexOf(DELIM_STR, i);

			if (j == -1) {
				if (i == 0)
					return messagePattern;
				sbuf.append(messagePattern.substring(i, messagePattern.length()));
				return sbuf.toString();
			}

			if (isEscapedDelimeter(messagePattern, j)) {
				if (!isDoubleEscaped(messagePattern, j)) {
					--L;
					sbuf.append(messagePattern.substring(i, j - 1));
					sbuf.append(DELIM_START);
					i = j + 1;
				} else {
					sbuf.append(messagePattern.substring(i, j - 1));
					deeplyAppendParameter(sbuf, argArray[L]);
					i = j + 2;
				}
			} else {
				sbuf.append(messagePattern.substring(i, j));
				deeplyAppendParameter(sbuf, argArray[L]);
				i = j + 2;
			}

		}

		sbuf.append(messagePattern.substring(i, messagePattern.length()));
		return sbuf.toString();
	}

	static final boolean isEscapedDelimeter(String messagePattern,
			int delimeterStartIndex) {
		if (delimeterStartIndex == 0) {
			return false;
		}
		char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);

		return potentialEscape == ESCAPE_CHAR;
	}

	static final boolean isDoubleEscaped(String messagePattern,
			int delimeterStartIndex) {
		return (delimeterStartIndex >= 2)
				&& (messagePattern.charAt(delimeterStartIndex - 2) == ESCAPE_CHAR);
	}

	private static void deeplyAppendParameter(StringBuffer sbuf, Object o) {
		if (o == null) {
			sbuf.append("null");
			return;
		}
		if (!o.getClass().isArray()) {
			sbuf.append(o);
		} else if (o instanceof boolean[])
			sbuf.append("Array[boolean]");
		else if (o instanceof byte[])
			sbuf.append("Array[byte]");
		else if (o instanceof char[])
			sbuf.append("Array[char]");
		else if (o instanceof short[])
			sbuf.append("Array[short]");
		else if (o instanceof int[])
			sbuf.append("Array[int]");
		else if (o instanceof long[])
			sbuf.append("Array[long]");
		else if (o instanceof float[])
			sbuf.append("Array[float]");
		else if (o instanceof double[])
			sbuf.append("Array[double]");
		else
			sbuf.append("Array[object]");
	}
}
