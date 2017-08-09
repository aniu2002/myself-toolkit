/**  
 * Project Name:http-server  
 * File Name:DefLog.java  
 * Package Name:com.sparrow.core.log.layout  
 * Date:2013-12-24下午1:17:16  
 *  
 */

package com.sparrow.core.log.layout;

/**
 * ClassName:DefLog <br/>
 * Date: 2013-12-24 下午1:17:16 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class DefLog {
	private static final String PREFIX = "log: ";
	private static final String ERR_PREFIX = "log:ERROR ";
	private static final String WARN_PREFIX = "log:WARN ";

	public static void error(String msg) {
		System.err.println(ERR_PREFIX + msg);
	}

	public static void error(String msg, Throwable t) {
		System.err.println(ERR_PREFIX + msg);
		if (t != null) {
			t.printStackTrace();
		}
	}

	public static void debug(String msg) {
		System.out.println(PREFIX + msg);
	}

	public static void warn(String msg) {
		System.err.println(WARN_PREFIX + msg);
	}
}
