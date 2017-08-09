package com.sparrow.transfer.feedback;

import java.math.BigDecimal;

import com.sparrow.transfer.data.StatusInfo;
import com.sparrow.transfer.data.Task;
import com.sparrow.transfer.transfer.IProgress;
import com.sparrow.transfer.utils.DateUtils;

public class EmbProgress implements IProgress {
	/** feed back manager */
	private IFeedBackManager manager;
	private Task task;
	private StatusInfo info;

	public EmbProgress(IFeedBackManager manager, Task task) {
		this.manager = manager;
		this.task = task;
		this.info = new StatusInfo();
	}

	@Override
	public void failure(String code, String why) {
		System.out.println(" - " + code + " " + why);
		this.task.setState(-1);
		if (this.manager != null) {
			this.manager.errorFeedback(this.task.getTaskId(), code, why);
		}
	}

	@Override
	public void finished() {
		info.finishTime = System.currentTimeMillis();
		long intverl = info.finishTime - info.beginTime;
		double seconds = intverl / 1000.0;
		long m = (info.size - info.offset) / (1024 * 1024);
		double speed = m / seconds;
		BigDecimal a = new BigDecimal(speed);
		a = a.setScale(2, 2);
		System.out.println(" - Time-consuming seconds:" + seconds + " speed:"
				+ a.floatValue() + "m/s");
		this.task.setState(0);
		this.task.setSeconds((float) seconds);
		if (this.manager != null) {
			this.manager.finishFeedback(info.taskId, 0, a.floatValue(),
					(float) seconds);
		}
	}

	@Override
	public void progress(long transfered) {
		info.transfered += transfered;
		updatePercentage(info);
		// info.offset += transfered;
		if (this.manager != null)
			this.manager.progressFeedback(this.info.clone());
	}

	void updateSpeed(StatusInfo info) {
		long curTime = System.currentTimeMillis();
		long intverl = curTime - info.beginTime;
		double seconds = intverl / 1000.0;
		long m = info.transfered / (1024 * 1024);
		double mms = m / seconds;
		BigDecimal a = new BigDecimal(mms);
		a = a.setScale(2, 2);
		info.speed = a.floatValue();
		// info.latestTime = curTime;
		System.out.println(m + "m" + " " + seconds + "s");
	}

	private void updatePercentage(StatusInfo info) {
		double length = info.offset + info.transfered;
		double mms = length / info.size;
		BigDecimal a = new BigDecimal(mms);
		a = a.setScale(2, 2);
		float f = a.floatValue() * 100;
		info.percentage = (int) f;
	}

	@Override
	public void started(long size, long offset) {
		info.taskId = this.task.getTaskId();
		info.size = size;
		info.offset = offset;
		info.status = 2;
		info.transfered = 0;
		info.beginTime = System.currentTimeMillis();
		info.latestTime = info.beginTime;
		System.out.println(" - Start task:" + this.task.getTaskId() + " size:"
				+ size + " offset:" + offset);
		this.task.setState(2);
		this.task.setStartTime(DateUtils.currentTime(null));
		if (this.manager != null)
			this.manager.startTask(this.task);
	}

	@Override
	public void stopped() {
		this.task.setState(-2);
		if (this.manager != null)
			this.manager.taskStop(this.task.getTaskId());
	}

	@Override
	public void dropTask() {
		// this.task.setState(-1);
		if (this.manager != null)
			this.manager.dropTask(this.task);
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
}
