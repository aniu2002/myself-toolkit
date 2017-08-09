package com.sparrow.transfer.feedback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.sparrow.transfer.data.StatusInfo;
import com.sparrow.transfer.data.Task;


public class EmbFeedBackManager implements IFeedBackManager {
	private static EmbFeedBackManager instance;
	/** logger setting */
	/** UI listener */
	private TaskListener listener;
	/** SNMP client */
	/** use JDK thread pool */
	private ExecutorService executorService = null;
	private int counter = 0;

	private EmbFeedBackManager() {
		this.initialize();
	}

	public static EmbFeedBackManager getInstance() {
		if (instance == null)
			instance = new EmbFeedBackManager();
		return instance;
	}

	/**
	 * 
	 * <p>
	 * Description: initialize the status feed back end point and progress feed
	 * back
	 * </p>
	 * 
	 * @author Yzc
	 */
	private void initialize() {
	}

	@Override
	public void start() {
		// try {
		// this.timer.startTimer();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	@Override
	public void stop() {
		// 线程池不再接收新的任务，但是会继续执行完工作队列中现有的任务
		this.executorService.shutdown();
		// 等待关闭线程池，每次等待的超时时间为30秒
		while (!this.executorService.isTerminated()) {
			try {
				this.executorService.awaitTermination(30, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void timerNotice() {
	}

	@Override
	public void progressFeedback(StatusInfo info) {
	}

	@Override
	public void finishFeedback(String tid, int status, float speed,
			float seconds) {
		if (this.listener != null) {
			this.listener.finishTask(tid, seconds, speed);
		}
	}

	@Override
	public void errorFeedback(String tid, String code, String errMsg) {
		if (this.listener != null) {
			this.listener.errorTask(tid, errMsg);
		}
	}

	@Override
	public void startTask(Task task) {
		this.counter++; // accept a transfer task
		if (this.listener != null) {
			this.listener.beginTask(task);
		}
	}

	@Override
	public void taskStop(String tid) {
		this.counter--;
		if (this.listener != null) {
			this.listener.taskStopped(tid);
		}
	}

	@Override
	public void dropTask(Task task) {
		if (this.listener != null) {
			this.listener.deleteTask(task.getTaskId());
		}
	}

	@Override
	public void addListener(TaskListener listener) {
		this.listener = listener;
	}

	public int getCounter() {
		return counter;
	}
	
}
