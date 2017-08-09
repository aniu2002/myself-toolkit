package com.sparrow.common.backend;

public class ProcessStatus {
	/** 消息关联id */
	private String sid;
	/** 结果状态,-1表示失败 0表示成功 */
	private int state;
	/** 处理的结果信息 */
	private Object result;
	/** 进度百分比 */
	private int percent;
	/** 已经处理时间 */
	private double costSeconds;
	/** 消息列表 */
	private Object[] msgs;

	public ProcessStatus() {

	}

	public ProcessStatus(int state) {
		this(null, state, null);
	}

	public ProcessStatus(String sid, int state, Object result) {
		this.sid = sid;
		this.state = state;
		this.result = result;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}

	public double getCostSeconds() {
		return costSeconds;
	}

	public void setCostSeconds(double costSeconds) {
		this.costSeconds = costSeconds;
	}

	public Object[] getMsgs() {
		return msgs;
	}

	public void setMsgs(Object[] msgs) {
		this.msgs = msgs;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}
