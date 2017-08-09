package com.sparrow.tools.common;

import com.sparrow.core.config.SystemConfig;

/**
 * 
 * @author YZC
 * 
 */
final public class Constants {

	public static String getValue(String key) {
		return System.getProperty(key, SystemConfig.getProps(key));
	}
}
