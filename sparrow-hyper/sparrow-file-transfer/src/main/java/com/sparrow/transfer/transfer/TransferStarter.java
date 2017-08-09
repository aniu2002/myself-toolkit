package com.sparrow.transfer.transfer;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.sparrow.transfer.constant.StatusCode;
import com.sparrow.transfer.data.Task;
import com.sparrow.transfer.exceptions.TaskException;


public class TransferStarter {
	public Queue<Runnable> queue = new ArrayBlockingQueue<Runnable>(100);
	/** use JDK thread pool */
	private ExecutorService executorService = null;

	{
		this.executorService = Executors.newFixedThreadPool(100);
	}

	/**
	 * 
	 * <p>
	 * Description: accept a task request
	 * </p>
	 * 
	 * @param req
	 * @author Yzc
	 * @throws TaskException
	 */
	public void accept(Task task, String command) throws TaskException {
		if ("add".equals(command)) {
			Transfer transfer = TransferBuilder.generatorTransfer(task);
			if (transfer != null) {
				this.executorService.submit(transfer);
				this.queue.add(transfer);
			}
		} else if ("stop".equals(command)) {
			Transfer job = this.findTransfer(task.getTaskId());
			if (job != null)
				job.stop();
		} else if ("start".equals(command)) {
			Transfer job = this.findTransfer(task.getTaskId());
			if (job != null && job.isStop()) {
				job.reset();
				this.executorService.submit(job);
			}
		} else if ("drop".equals(command)) {
			this.removeTransfer(task.getTaskId());
		} else {
			throw new TaskException(StatusCode.UNKNOW_OPERATE,
					"Unknow Command : " + command);
		}
	}

	private Transfer findTransfer(String taskId) {
		Iterator<Runnable> iterator = this.queue.iterator();
		while (iterator.hasNext()) {
			Transfer job = (Transfer) iterator.next();
			if (job.getTranferId().equals(taskId)) {
				return job;
			}
		}
		return null;
	}

	public void removeTransferQueue(Transfer transfer) {
		this.queue.remove(transfer);
	}

	private void removeTransfer(String taskId) {
		Iterator<Runnable> iterator = this.queue.iterator();
		while (iterator.hasNext()) {
			Transfer job = (Transfer) iterator.next();
			if (job.getTranferId().equals(taskId)) {
				job.drop();
				iterator.remove();
				return;
			}
		}
	}

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
		Iterator<Runnable> iterator = this.queue.iterator();
		while (iterator.hasNext()) {
			Transfer job = (Transfer) iterator.next();
			job.stop();
		}
	}

}
