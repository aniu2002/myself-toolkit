package com.sparrow.transfer.feedback;

import java.math.BigDecimal;

import com.sparrow.transfer.data.Task;
import com.sparrow.transfer.transfer.IProgress;

public class DefaultProgress implements IProgress {
	private Task task;
	private long size;
	private long transferBytes;
	private long beginTime;
	private long finishTime;
	private long offset;
	private long lastTime;

	public DefaultProgress(Task task) {
		this.task = task;
	}

	@Override
	public void failure(String code, String why) {
		System.out.println("ERROR: " + code + " " + why);
	}

	@Override
	public void finished() {
		this.finishTime = System.currentTimeMillis();

		long intverl = this.finishTime - this.beginTime;
		double seconds = intverl / 1000.0;
		int m = (int) size / (1024 * 1024);
		double mms = m / seconds;
		BigDecimal a = new BigDecimal(mms);
		a = a.setScale(2, 2);
		System.out.println("Seconds:" + seconds + "  Speed:" + a.toString()
				+ "m/s");
	}

	@Override
	public void progress(long transfered) {
		System.out.println("transfered:" + transfered);
		// this.transferBytes += transfered;
		//
		// long nowTime = System.currentTimeMillis();
		// long intverl = nowTime - this.lastTime;
		// double seconds = intverl / 1000.0;
		// int m = (int) transfered / (1024 * 1024);
		// m = (int) (m / seconds);
		// System.out.println("Speed:" + m + "m/s");
		// this.lastTime = nowTime;
	}

	@Override
	public void started(long size, long offset) {
		// this.task = tsk;
		this.size = size;
		this.offset = offset;
		this.beginTime = System.currentTimeMillis();
		this.lastTime = this.beginTime;
		System.out.println("size:" + size + " offset:" + offset);
	}

	@Override
	public void stopped() {

	}

	@Override
	public void dropTask() {

	}

	@Override
	public Task getTask() {
		return this.task;
	}

	public long getSize() {
		return size;
	}

	public long getOffset() {
		return offset;
	}

	public long getTransferBytes() {
		return transferBytes;
	}

	public long getBeginTime() {
		return beginTime;
	}

	public long getFinishTime() {
		return finishTime;
	}

	public long getLastTime() {
		return lastTime;
	}

}
