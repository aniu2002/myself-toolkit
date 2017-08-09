package com.sparrow.data.tools.message;

public class ProcessResult {
	/** 结果状态,-1表示失败 0表示成功 */
	int state;
	/** 处理的结果信息 */
	Object result;

	public ProcessResult() {

	}

	public ProcessResult(int state) {
		this(state, null);
	}

	public ProcessResult(int state, Object result) {
		this.state = state;
		this.result = result;
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
