package com.sparrow.core.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-5-29 Time: 下午4:19 To change this
 * template use File | Settings | File Templates.
 */
public class StackTraceHelper {
	static final String lineSeparator = System.getProperty("line.separator");

	public static String formatExceptionMsg(Throwable t) {
		java.lang.StackTraceElement[] traces = t.getStackTrace();
		int len = traces.length;
		StackTraceElement target = null;
		boolean flg = true;
		int i = 0;
		do {
			String str = traces[i].getClassName();
			if (str.startsWith("com.zhe800")) {
				target = traces[i];
				flg = false;
			}
			i = i + 1;
		} while (flg && i < len);
		if (target != null)
			return target.getClassName().substring(25).replace("$", "") + "#"
					+ target.getMethodName() + "#" + target.getLineNumber()
					+ "#" + getFirstLineMsg(t.getMessage());
		else
			return getFirstLineMsg(t.getMessage());
	}

	public static StackTraceElement findStackTraceElement(String name) {
		Throwable t = new Throwable();
		java.lang.StackTraceElement[] traces = t.getStackTrace();
		int len = traces.length;
		StackTraceElement target = null;
		boolean flg = true;
		boolean found = false;
		int i = 0;
		do {
			String str = traces[i].getClassName();
			if (str.equals(name)) {
				found = true;
			} else if (found) {
				target = traces[i];
				break;
			}
			i = i + 1;
		} while (flg && i < len);
		if (target == null)
			target = traces[i - 1];
		return target;
	}

	public static String getFirstLineMsg(String msg) {
		if (StringUtils.isBlank(msg))
			return " * ";
		else {
			String str = msg;
			int idx = str.indexOf(lineSeparator);
			if (idx != -1)
				str = str.substring(0, idx);
			idx = str.lastIndexOf(':');
			if (idx != -1)
				str = str.substring(idx + 1);
			return str;
		}
	}
}
