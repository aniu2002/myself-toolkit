package com.sparrow.transfer.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Yzc
 * @version 3.0
 * @date 2009-8-13
 */
public class Task implements Serializable {
	private static final long serialVersionUID = -508355134607424193L;
	/**
	 * 任务ID号 The id of this task.
	 */
	private String taskId = null;
	/**
	 * 启动时间 Start up time
	 */
	private String startTime = null;
	/**
	 * 终止时间 End time.
	 */
	private String endTime = null;
	/**
	 * 完成百分比 The complete percent.
	 */
	private int percent;
	/**
	 * 
	 */
	private List<TaskFile> files;
	/**
	 * 任务状态 The task state.
	 */
	private int state;
	/** need cover target file */
	private boolean cover = true;
	/** how long time to transfer the file */
	private float seconds;
	private String desc;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public float getSeconds() {
		return seconds;
	}

	public void setSeconds(float seconds) {
		this.seconds = seconds;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public boolean isCover() {
		return cover;
	}

	public void setCover(boolean cover) {
		this.cover = cover;
	}

	public List<TaskFile> getFiles() {
		return files;
	}

	public void setFiles(List<TaskFile> files) {
		this.files = files;
	}

	public void addFile(TaskFile file) {
		if (this.files != null) {
			this.files.add(file);
		} else {
			this.files = new ArrayList<TaskFile>();
			this.files.add(file);
		}
	}
}
