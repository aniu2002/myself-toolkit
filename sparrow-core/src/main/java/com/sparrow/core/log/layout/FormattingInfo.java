/**  
 * Project Name:http-server  
 * File Name:FormattingInfo.java  
 * Package Name:com.sparrow.core.log.layout  
 * Date:2013-12-24下午1:15:37  
 *  
 */

package com.sparrow.core.log.layout;

public class FormattingInfo {
	int min = -1;
	int max = 0x7FFFFFFF;
	boolean leftAlign = false;

	void reset() {
		min = -1;
		max = 0x7FFFFFFF;
		leftAlign = false;
	}

	void dump() {
		DefLog.debug("min=" + min + ", max=" + max + ", leftAlign=" + leftAlign);
	}
}
