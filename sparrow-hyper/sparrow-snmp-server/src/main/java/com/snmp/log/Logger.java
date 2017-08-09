package com.snmp.log;

import java.io.PrintStream;
import java.util.Date;

import com.snmp.Constants;

/**
 * 
 * <p>
 * Title: Logger
 * </p>
 * <p>
 * Description: com.snmp.log
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Sobey
 * </p>
 * 
 * @author Yzc
 * @version 3.0
 * @date 2009-8-6
 */
public abstract class Logger {
	/** date format template */
	public static final String YYYY_MM_DD_FORMAT = "yyyy-MM-dd";
	/** time format template */
	public static final String YYYY_MM_DD_TIME = "yyyy-MM-dd kk:mm:ss";
	/** debug description */
	public static final String DEBUG = "DEBUG";
	/** info description */
	public static final String INFO = "INFO";
	/** error description */
	public static final String ERROR = "ERROR";
	/** warn description */
	public static final String WARN = "WARN";
	/** line separator setting */
	public final String line_separator = System.getProperty("line.separator");
	/** log's out put stream */
	protected PrintStream out = null;
	/** error log out put stream */
	protected PrintStream err = null;

	/** log debug enabled */
	private boolean debugEnabled;
	/** log info enabled */
	private boolean infoEnabled;
	/** log warn enabled */
	private boolean warnEnabled;
	/** log error enabled */
	private boolean errorEnabled;

	/**
	 * 
	 * <p>
	 * Description: get
	 * </p>
	 * 
	 * @author Yzc
	 */
	protected void checkLogStream() {
		if (this.out == null)
			this.out = System.out;
		if (this.err == null)
			this.err = System.err;
	}

	protected void log(String leval, String msg, String timeStr) {
		if (this.out == null)
			return;
		if (timeStr != null && !timeStr.equals(""))
			this.out.print(timeStr);
		this.out.print(Constants.SPACE);
		this.out.print(leval);
		this.out.print(Constants.SPACE);
		this.out.println(msg);
	}

	protected void error(String msg, String timeStr) {
		if (this.err == null)
			return;
		if (timeStr != null && !timeStr.equals(""))
			this.err.print(timeStr);
		this.err.print(Constants.SPACE);
		this.err.print("ERROR");
		this.err.print(Constants.SPACE);
		this.err.println(msg);
	}

	public void info(String msg) {
		if (!this.infoEnabled)
			return;
		this.log(INFO, msg, this.formatTime(null));
	}

	public void error(String msg) {
		if (!this.errorEnabled)
			return;
		this.error(msg, this.formatTime(null));
	}

	public void warn(String msg) {
		if (!this.warnEnabled)
			return;
		this.log(WARN, msg, this.formatTime(null));
	}

	public void debug(String msg) {
		if (!this.debugEnabled)
			return;
		this.log(DEBUG, msg, this.formatTime(null));
	}

	public abstract PrintStream getOut();

	public abstract PrintStream getErr();

	protected abstract String formatTime(Date time);

	public boolean isDebugEnabled() {
		return debugEnabled;
	}

	public void setDebugEnabled(boolean debugEnabled) {
		this.debugEnabled = debugEnabled;
	}

	public boolean isInfoEnabled() {
		return infoEnabled;
	}

	public void setInfoEnabled(boolean infoEnabled) {
		this.infoEnabled = infoEnabled;
	}

	public boolean isWarnEnabled() {
		return warnEnabled;
	}

	public void setWarnEnabled(boolean warnEnabled) {
		this.warnEnabled = warnEnabled;
	}

	public boolean isErrorEnabled() {
		return errorEnabled;
	}

	public void setErrorEnabled(boolean errorEnabled) {
		this.errorEnabled = errorEnabled;
	}

}
