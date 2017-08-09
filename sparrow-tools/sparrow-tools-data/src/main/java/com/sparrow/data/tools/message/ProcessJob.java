package com.sparrow.data.tools.message;

public abstract class ProcessJob implements Runnable {
	private final ProcessMessage processMessage;

	public ProcessJob(String sid) {
		this(sid, null);
	}

	public ProcessJob(String sid, String label) {
		ProcessMessage msg = MessageManager.newMessage(sid);
		msg.setLabel(label);
		this.processMessage = msg;
	}

	@Override
	public void run() {
		processMessage.begin();
		try {
			ProcessResult result = this.doExecute();
			processMessage.end(result);
		} catch (Throwable t) {
			t.printStackTrace();
			processMessage.error(t);
		}
	}

	protected void notifyProcess(int percent, String msg) {
		processMessage.notifyProcess(percent, msg);
	}

	protected abstract ProcessResult doExecute();

}
