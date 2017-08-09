package com.snmp.log;

import java.io.PrintStream;
import java.util.Properties;

import com.snmp.tools.PropertiesFileUtil;

public class LogFactory {
	private static PrintStream sys_err = null;
	private static PrintStream sys_out = null;
	/** log debug enabled */
	private static boolean debugEnabled;
	/** log info enabled */
	private static boolean infoEnabled;
	/** log warn enabled */
	private static boolean warnEnabled;
	/** log error enabled */
	private static boolean errorEnabled;
	/** log error enabled */
	private static boolean fileLogEnabled;
	/** log error enabled */
	private static String filePath;

	static {
		Properties props = PropertiesFileUtil
				.getPropertiesEl("config/log.properties");
		if (props != null) {
			debugEnabled = compare(props.getProperty("log.debug.enabled"),
					"true");
			infoEnabled = compare(props.getProperty("log.info.enabled"), "true");
			warnEnabled = compare(props.getProperty("log.warn.enabled"), "true");
			errorEnabled = compare(props.getProperty("log.error.enabled"),
					"true");
			fileLogEnabled = compare(props.getProperty("log.file.enabled"),
					"true");
			if (fileLogEnabled) {
				filePath = props.getProperty("log.file.path", "default.log");
				if (filePath == null || filePath.equals(""))
					filePath = "default.log";
			}
		}
	}

	/**
	 * 
	 * <p>
	 * Description: compare
	 * </p>
	 * 
	 * @param value
	 * @param eqal
	 * @return
	 * @author Yzc
	 */
	private static boolean compare(String value, String equal) {
		if (equal == null) {
			if (value == null)
				return true;
		} else {
			return equal.equals(value);
		}
		return false;
	}

	private LogFactory() {
		sys_err = System.err;
		sys_out = System.out;
	}

	/**
	 * 
	 * @return
	 * @see 获取系统默认的日志
	 */
	public static Logger getLogger() {
		Logger logger = null;
		if (fileLogEnabled) {
			logger = new FileLogger(filePath);
		} else {
			logger = new SystemLogger();
		}
		logger.setDebugEnabled(debugEnabled);
		logger.setErrorEnabled(errorEnabled);
		logger.setInfoEnabled(infoEnabled);
		logger.setWarnEnabled(warnEnabled);
		return logger;
	}

	/**
	 * 
	 * @param fileName
	 * @return
	 * @see 获取文件输出日志
	 */
	public static FileLogger getFileLogger(String fileName) {
		return new FileLogger(fileName, fileName);
	}

	/**
	 * 
	 * @param fileName
	 * @return
	 * @see 获取文件输出日志
	 */
	public static FileLogger getFileLogger(String outFile, String errorFile) {
		return new FileLogger(outFile, errorFile);
	}

	public void reset() {
		System.setOut(sys_out);
		System.setErr(sys_err);
	}
}
