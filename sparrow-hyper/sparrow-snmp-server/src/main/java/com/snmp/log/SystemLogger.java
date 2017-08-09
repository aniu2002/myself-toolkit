package com.snmp.log;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemLogger extends Logger {

	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			YYYY_MM_DD_TIME);

	public SystemLogger() {
		this.out = System.out;
		this.err = System.err;
	}

	/**
	 * @return out
	 */
	public PrintStream getOut() {
		return out;
	}

	@Override
	public PrintStream getErr() {
		return this.err;
	}

	@Override
	public String formatTime(Date time) {
		if (time == null)
			time = new java.util.Date();
		String timestr = sdf.format(time);
		return timestr;
	}

}
