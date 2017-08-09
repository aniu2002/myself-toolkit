package com.sparrow.core.log;

import com.sparrow.core.log.layout.PatternLayout;
import com.sparrow.core.utils.StackTraceHelper;
import com.sparrow.core.config.SystemConfig;

public class SysLogger {
	/**
	 * '%-5p'右补齐5位字符（占位符最少5个字符）， '%5p' 左补齐5个字符
	 * '%3.4p'右补齐5位字符（占位符最少3个字符，操作不管，'.'后面限制字符输出最多四个，截取总长度-允许长度5）， 存在 INFO EBUG
	 * 输出
	 */
	static final PatternLayout layout = new PatternLayout(
			"%d [%p] %C{2}-%M %m%n");
	// "%d %-5p [%T]-%C-%M %m %L%n"
	public static boolean DEBUG_ENABLED = false;
	static final String INFO = "INFO";
	static final String DEBUG = "DEBUG";
	static final String ERROR = "ERROR";
	static final String WARN = "WARN";
	static final boolean LOG_ENABLE;

	static {
		LOG_ENABLE = "true".equals(SystemConfig.getProperty("log.enable",
				"false"));
	}

	public static void info(String message) {
		log(INFO, message);
	}

	public static void error(String message) {
		err(ERROR, message);
	}

	public static void debug(String message) {
		log(DEBUG, message);
	}

	public static void warn(String message) {
		log(WARN, message);
	}

	public static void info(String message, Object... args) {
		log(INFO, MessageFormatter.arrayFormat(message, args));
	}

	public static void error(String message, Object... args) {
		err(ERROR, MessageFormatter.arrayFormat(message, args));
	}

	public static void debug(String message, Object... args) {
		log(DEBUG, MessageFormatter.arrayFormat(message, args));
	}

	public static void warn(String message, Object... args) {
		log(WARN, MessageFormatter.arrayFormat(message, args));
	}

	public static void log(String level, String message) {
		if (LOG_ENABLE) {
			String msg = layout.format(createLogEvent(level, message));
			System.out.print(msg);
			//Application.app().onMessage(msg);
		}
	}

	static void err(String level, String message) {
		if (LOG_ENABLE) {
			String msg = layout.format(createLogEvent(level, message));
			System.err.print(msg);
			//Application.app().onMessage(msg);
		}
	}

	static LoggingEvent createLogEvent(String level, String message) {
		LoggingEvent e = new LoggingEvent();
		e.setLevel(level);
		e.setLoggerName("com.sparrow.core.log.Logger");
		e.setMessage(message);
		e.setThreadName(Thread.currentThread().getName());
		e.setTimeStamp(System.currentTimeMillis());

		LocationInfo locationInfo = new LocationInfo();
		StackTraceElement target = StackTraceHelper
				.findStackTraceElement(SysLogger.class.getName());
		locationInfo.setClassName(target.getClassName());
		locationInfo.setLineNumber(String.valueOf(target.getLineNumber()));
		locationInfo.setFullInfo(target.getFileName());
		locationInfo.setMethodName(target.getMethodName());
		e.setLocationInfo(locationInfo);
		return e;
	}

	public static void main(String args[]) {
		info("haha");
		debug("haha");
	}
}
