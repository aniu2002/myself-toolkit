/**  
 * Project Name:http-server  
 * File Name:LocationInfo.java  
 * Package Name:com.sparrow.core.log  
 * Date:2013-12-24下午1:39:21  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.core.log;

public class LocationInfo {
	private String fullInfo;
	private String methodName;
	private String lineNumber;
	private String className;

	public String getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getFullInfo() {
		return fullInfo;
	}

	public void setFullInfo(String fullInfo) {
		this.fullInfo = fullInfo;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

}
