package com.sparrow.core.log.sql;

import org.apache.commons.lang3.StringUtils;

import com.sparrow.core.log.Logger;
import com.sparrow.core.log.MessageFormatter;


public class SqlLogImpl extends SqlLog {
	final static String FQCN = SqlLog.class.getName();
	boolean enabled = true;
	Logger logger = new Logger("%d [%p] %m%n", FQCN);

	@Override
	protected void info(String msg) {
		if (StringUtils.isBlank(msg))
			return;
		logger.log(Logger.INFO, FQCN, msg);
	}

	@Override
	protected void info(String msg, Object... args) {
		if (StringUtils.isBlank(msg))
			return;
		logger.log(Logger.INFO, FQCN, MessageFormatter.arrayFormat(msg, args));
	}
}
