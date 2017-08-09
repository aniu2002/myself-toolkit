package com.sparrow.tools.pogen;

import com.sparrow.core.log.SysLogger;

public class DefaultLog implements Log {

	@Override
	public void info(Object msg) {
		SysLogger.info((String) msg);
	}

	@Override
	public void msg(Object msg) {
		SysLogger.info((String) msg);
	}

	@Override
	public void setStep(double step) {
		
	}

}
