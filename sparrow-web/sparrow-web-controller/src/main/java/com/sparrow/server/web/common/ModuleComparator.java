package com.sparrow.server.web.common;

import java.util.Comparator;

import com.sparrow.server.web.config.ControllerClassConfig;

public class ModuleComparator implements Comparator<ControllerClassConfig> {

	@Override
	public int compare(ControllerClassConfig s, ControllerClassConfig t) {
		if (t == null || s == null)
			return 0;
		int sl = s.getModuleLen();
		int ol = t.getModuleLen();
		return ol - sl;
	}

}
