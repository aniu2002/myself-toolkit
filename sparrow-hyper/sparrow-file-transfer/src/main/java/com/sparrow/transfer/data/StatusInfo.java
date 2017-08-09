package com.sparrow.transfer.data;

public class StatusInfo implements Cloneable {
	public String taskId;
	public String errorCode;
	public String errorMsg;
	public int percentage;
	public int status;
	public float speed;
	public long size;
	public long transfered;
	public long beginTime;
	public long finishTime;
	public long offset;
	public long latestTime;

	public StatusInfo clone() {
		try {
			return (StatusInfo) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
