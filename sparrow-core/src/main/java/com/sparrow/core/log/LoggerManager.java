package com.sparrow.core.log;

import com.sparrow.core.log.sql.SqlLog;
import com.sparrow.core.log.sql.SqlLogImpl;

public final class LoggerManager {
	private static final SqlLog sqlLog = new SqlLogImpl();
	private static final Logger sysLog = new Logger();

	public static SqlLog getSqlLog() {
		return sqlLog;
	}

	public static Logger getSysLog() {
		return sysLog;
	}
}
