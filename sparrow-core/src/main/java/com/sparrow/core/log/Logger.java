package com.sparrow.core.log;

import com.sparrow.core.log.layout.PatternLayout;
import com.sparrow.core.utils.StackTraceHelper;

public class Logger {
	/**
	 * '%-5p'右补齐5位字符（占位符最少5个字符）， '%5p' 左补齐5个字符
	 * '%3.4p'右补齐5位字符（占位符最少3个字符，操作不管，'.'后面限制字符输出最多四个，截取总长度-允许长度5）， 存在 INFO EBUG
	 * 输出
	 */
	static final PatternLayout DEFAULT_LAYOUT = new PatternLayout(
			"%d [%p] %C{2}-%M %m%n");
	// "%d %-5p [%T]-%C-%M %m %L%n"
	public static boolean DEBUG_ENABLED = false;
	public static final String INFO = "INFO";
	public static final String DEBUG = "DEBUG";
	public static final String ERROR = "ERROR";
	public static final String WARN = "WARN";
	public static final String FN = "com.sparrow.core.log.Logger";

	private final PatternLayout layout;
	private final String fqcn;

	public Logger() {
		this(DEFAULT_LAYOUT);
	}

	public Logger(String layout) {
		this(new PatternLayout(layout));
	}

	public Logger(String layout, String fqcn) {
		this(new PatternLayout(layout), fqcn);
	}

	public Logger(PatternLayout layout) {
		this(layout, FN);
	}

	public Logger(PatternLayout layout, String fqcn) {
		this.layout = layout;
		this.fqcn = fqcn;
	}

	public void info(String message) {
		log(INFO, message);
	}

	public void error(String message) {
		err(ERROR, message);
	}

	public void debug(String message) {
		log(DEBUG, message);
	}

	public void warn(String message) {
		log(WARN, message);
	}

	public void info(String message, Object... args) {
		log(INFO, MessageFormatter.arrayFormat(message, args));
	}

	public void error(String message, Object... args) {
		err(ERROR, MessageFormatter.arrayFormat(message, args));
	}

	public void debug(String message, Object... args) {
		log(DEBUG, MessageFormatter.arrayFormat(message, args));
	}

	public void warn(String message, Object... args) {
		log(WARN, MessageFormatter.arrayFormat(message, args));
	}

	public void log(String level, String message) {
		this.log(level, this.fqcn, message);
	}

	public void err(String level, String message) {
		this.err(level, this.fqcn, message);
	}

	public void log(String level, String fn, String message) {
		String msg = this.layout.format(createLogEvent(level, fn, message));
		System.out.print(msg);
	}

	public void err(String level, String fn, String message) {
		String msg = this.layout.format(createLogEvent(level, fn, message));
		System.err.print(msg);
	}

	protected LoggingEvent createLogEvent(String level, String fn,
			String message) {
		LoggingEvent e = new LoggingEvent();
		e.setLevel(level);
		e.setLoggerName(fn);
		e.setMessage(message);
		e.setThreadName(Thread.currentThread().getName());
		e.setTimeStamp(System.currentTimeMillis());

		LocationInfo locationInfo = new LocationInfo();
		StackTraceElement target = StackTraceHelper.findStackTraceElement(fn);
		locationInfo.setClassName(target.getClassName());
		locationInfo.setLineNumber(String.valueOf(target.getLineNumber()));
		locationInfo.setFullInfo(target.getFileName());
		locationInfo.setMethodName(target.getMethodName());
		e.setLocationInfo(locationInfo);
		return e;
	}
}
