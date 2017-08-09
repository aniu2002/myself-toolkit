package com.milgra.server.log;

import com.milgra.server.Library;

public class Logger {
	private static Logger logger;
	private boolean enable = false;

	private Logger(boolean enable) {
		this.enable = enable;
	}

	public static Logger getLogger() {
		if (logger == null)
			logger = new Logger(Library.debug);
		return logger;
	}

	public void debug(String msg) {
		if (this.enable)
			System.out.println("DEBUG " + msg );
	}

	public boolean isEnable() {
		return enable;
	}
}
