package com.sparrow.common.backend;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 处理信息记录，集群时这些信息单机内存中，如何同步？
 * 
 * @author YZC
 * @version 1.0 (2014-4-19)
 * @modify
 */
public class ProcessMessage {
	private static final int MAX_MSG_SIZE = 50;
	/** 超时时间是20分钟，消息存活时间是20分钟 */
	private static final long LEASE_TIME_MILLIS = 20 * 60 * 1000;
	/** 消息关联id */
	private String sid;
	/** 消息标签（描述信息） */
	private String label;
	/** 消息数 */
	private List<String> msgs;
	/** 处理开始时间 */
	private long startTime;
	/** 已经处理时间 */
	private double costSeconds;
	/** 处理消息的状态 -1 失败 ，2 成功，1运行 */
	private int state;
	/** 进度百分比 */
	private int percent;
	/** 消息最大存活时间 */
	private volatile long timeToLive = LEASE_TIME_MILLIS;
	/** 存取的消息大小 */
	private volatile int msgSize = 0;
	/** 处理的结果信息 */
	private Object result;

	public ProcessMessage() {
	}

	public ProcessMessage(String sid) {
		this.sid = sid;
	}

	public ProcessMessage(String sid, String label) {
		this.sid = sid;
		this.label = label;
	}

	public int getState() {
		return state;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double getCostSeconds() {
		return costSeconds;
	}

	public long getStartTime() {
		return startTime;
	}

	public String getSid() {
		return sid;
	}

	public void begin() {
		this.state = 1;
		this.startTime = ProcessMsgManager.now();
	}

	public void end(ProcessResult processResult) {
		if (processResult != null) {
			this.result = processResult.result;
			if (processResult.state == -1)
				this.state = -1;
			else {
				this.state = 2;
				this.percent = 100;
			}
		} else {
			this.state = 2;
			this.percent = 100;
		}
		this.setCostTime();
	}

	public void error(Throwable t) {
		this.state = -1;
		this.setCostTime();
		this.addMessage(t.getMessage());
	}

	void setCostTime() {
		this.costSeconds = ProcessMsgManager.convert(ProcessMsgManager.now()
				- this.startTime);
	}

	public void notifyProcess(int percent, String msg) {
		this.percent = percent;
		this.setCostTime();
		this.addMessage(msg);
	}

	void addMessage(String msg) {
		int n = this.msgSize;
		if (n >= MAX_MSG_SIZE)
			return;
		if (this.msgs == null)
			this.msgs = new ArrayList<String>();
		this.msgs.add(msg);
		n++;
		this.msgSize = n;
	}

	public ProcessStatus fetchData() {
		ProcessStatus status = new ProcessStatus(this.sid, this.state,
				this.result);
		status.setCostSeconds(this.costSeconds);
		status.setPercent(this.percent);
		if (this.msgs == null || this.msgs.isEmpty())
			status.setMsgs(null);
		else {
			status.setMsgs(this.msgs.toArray());
			this.msgs.clear();
			this.msgSize = 0;
		}
		// 处理成功或者失败
		if (this.state == 2 || this.state == -1) {
			this.msgs = null;
			this.msgSize = 0;
			ProcessMsgManager.removeMessage(this.sid);
		}
		return status;
	}

	public int getPercent() {
		return percent;
	}

	/**
	 * 
	 * 记录已经逝去了多少时间，活动时间-逝去时间
	 * 
	 * @param aDeltaMillis
	 * @author YZC
	 */
	public void age(long aDeltaMillis) {
		timeToLive -= aDeltaMillis;
	}

	/**
	 * 
	 * 是否消息已经过期
	 * 
	 * @return
	 * @author YZC
	 */
	public boolean isExpired() {
		return timeToLive <= 0;
	}

	/**
	 * @see 重新开始计算失效时间
	 */
	public void kick() {
		timeToLive = LEASE_TIME_MILLIS;
	}
}
