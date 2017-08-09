package com.sparrow.core.exception;

import com.sparrow.core.utils.StringUtils;

import java.lang.reflect.InvocationTargetException;

public class ExceptionHelper {
	static final String lineSeparator = System.getProperty("line.separator");

	/**
	 * 将抛出对象包裹成运行时异常，并增加自己的描述
	 * 
	 * @param e
	 *            抛出对象
	 * @param fmt
	 *            格式
	 * @param args
	 *            参数
	 * @return 运行时异常
	 */
	public static RuntimeException wrapThrow(Throwable e, String fmt,
			Object... args) {
		return new RuntimeException(String.format(fmt, args), e);
	}

	/**
	 * 用运行时异常包裹抛出对象，如果抛出对象本身就是运行时异常，则直接返回。
	 * <p>
	 * 如果是 InvocationTargetException，那么将其剥离，只包裹其 TargetException
	 * 
	 * @param e
	 *            抛出对象
	 * @return 运行时异常
	 */
	public static RuntimeException wrapThrow(Throwable e) {
		if (e instanceof RuntimeException)
			return (RuntimeException) e;
		if (e instanceof InvocationTargetException)
			return wrapThrow(((InvocationTargetException) e)
					.getTargetException());
		return new RuntimeException(e);
	}

	public static String formatExceptionMsg(Throwable t) {
		StackTraceElement[] traces = t.getStackTrace();
		int len = traces.length;
		StackTraceElement target = null;
		boolean flg = true;
		int i = 0;
		do {
			String str = traces[i].getClassName();
			if (str.startsWith("com.dili")) {
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

	public static String getFirstLineMsg(String msg) {
		if (StringUtils.isEmpty(msg))
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
