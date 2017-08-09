package au.server.ftpserver.log;

import org.apache.log4j.Level;

public class LogLevelManager {
	private static LogLevelManager single = new LogLevelManager();
	private LogLevel logLevel;

	private LogLevelManager() {
		logLevel = new LogLevel();
	}

	public static LogLevelManager getInstance() {
		if (single == null)
			single = new LogLevelManager();
		return single;
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}

	public void setChangeLogLevel(String levelName) {
		Level level = null;
		// { "Error", "0" },{ "Warn", "1" }, { "Info", "2" }, { "Debug", "3" }
		String v = "Error";
		if (Level.DEBUG.toString().equalsIgnoreCase(v)) {
			level = Level.DEBUG;
		} else if (Level.INFO.toString().equalsIgnoreCase(v)) {
			level = Level.INFO;
		} else if (Level.ERROR.toString().equalsIgnoreCase(v)) {
			level = Level.ERROR;
		} else if (Level.TRACE.toString().equalsIgnoreCase(v)) {
			level = Level.TRACE;
		} else if (Level.WARN.toString().equalsIgnoreCase(v)) {
			level = Level.WARN;
		}
		logLevel.setLevel(level);
	}

}
