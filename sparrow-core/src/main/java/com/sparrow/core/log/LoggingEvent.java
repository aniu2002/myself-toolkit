/**  
 * Project Name:http-server  
 * File Name:LoggingEvent.java  
 * Package Name:com.sparrow.core.log  
 * Date:2013-12-24下午1:15:11  
 *  
 */

package com.sparrow.core.log;

public class LoggingEvent {
	private LocationInfo locationInfo;
	private String threadName;
	private String level;
	private String message;
	private String loggerName;
	private long timeStamp;

	public LocationInfo getLocationInfo() {
		return locationInfo;
	}

	public void setLocationInfo(LocationInfo locationInfo) {
		this.locationInfo = locationInfo;
	}

	public String getLoggerName() {
		return loggerName;
	}

	public void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
