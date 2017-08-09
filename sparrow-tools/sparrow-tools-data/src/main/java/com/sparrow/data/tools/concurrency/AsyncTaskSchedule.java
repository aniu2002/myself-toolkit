package com.sparrow.data.tools.concurrency;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 导入处理协调管理器，支持多线程同时导入，增强导入并发性能，而不占有http响应线程
 * 
 * @author YZC
 * @version 1.0 (2014-3-28)
 * @modify
 */
public class AsyncTaskSchedule {
	/** 线程池 */
	private ThreadPoolExecutor threadPool;
	/** 线程池最小线程数 */
	private int minThreads = MuiltThreadSetting.threadMinCount;
	/** 线程池最大线程数 */
	private int maxThreads = MuiltThreadSetting.threadMaxCount;
	/** 线程池检查时间间隔 */
	private int checkPeriod = MuiltThreadSetting.checkPeriod;

	/** 同步信号量 */
	private Object synObject = new Object();

	public int getMinThreads() {
		return minThreads;
	}

	public void setMinThreads(int minThreads) {
		this.minThreads = minThreads;
	}

	public int getMaxThreads() {
		return maxThreads;
	}

	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	public int getCheckPeriod() {
		return checkPeriod;
	}

	public void setCheckPeriod(int checkPeriod) {
		this.checkPeriod = checkPeriod;
	}

	/**
	 * 
	 * 检测和创建threadPool，当使用并发协调时，第一次才创建线程池。根据同步信号量synObject控制并发访问
	 * 
	 * @author YZC
	 */
	ThreadPoolExecutor checkAndCreatePool() {
		if (this.threadPool != null)
			return this.threadPool;
		synchronized (this.synObject) {
			if (this.threadPool == null)
				this.threadPool = new ThreadPoolExecutor(
						MuiltThreadSetting.threadMinCount,
						MuiltThreadSetting.threadMaxCount,
						MuiltThreadSetting.checkPeriod, TimeUnit.MINUTES,
						new ArrayBlockingQueue<Runnable>(
								MuiltThreadSetting.threadMinCount),
						new ThreadPoolExecutor.CallerRunsPolicy());
		}
		return this.threadPool;
	}

	/**
	 * 
	 * 提交一个job到线程池，异步运行
	 * 
	 * @param runable
	 *            异步job任务
	 * @return 返回job的状态特征信息
	 * @author YZC
	 */
	public Future<?> submit(Runnable runable) {
		ThreadPoolExecutor threadPool = this.checkAndCreatePool();
		return threadPool.submit(runable);
	}

	public <T> Future<T> submit(Callable<T> task) {
		ThreadPoolExecutor threadPool = this.checkAndCreatePool();
		return threadPool.submit(task);
	}

	/**
	 * 
	 * 提交一组job到线程池，多个job并发运行。并等待所有并发任务结束后返回
	 * 
	 * @param runables
	 *            多个jobs
	 * @author YZC
	 */
	public void submitAndWait(Runnable[] runables) {
		ThreadPoolExecutor threadPool = this.checkAndCreatePool();
		Future<?> futures[] = new Future[runables.length];
		Runnable runable;
		for (int i = 0; i < runables.length; i++) {
			runable = runables[i];
			futures[i] = threadPool.submit(runable);
		}

		boolean allDone;
		do {
			allDone = true;
			// 遍历任务的结果
			for (Future<?> fs : futures) {
				if (!fs.isDone()) {
					allDone = false;
					break;
				}
			}
		} while (!allDone);
	}

	public void asynWait(Object obj) {
		try {
			synchronized (obj) {
				obj.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void asynNotify(Object obj) {
		synchronized (obj) {
			obj.notify();
		}
	}

	public void close() {
		this.closeThreadPool(this.threadPool);
	}

	protected void closeThreadPool(ThreadPoolExecutor pool) {
		pool.shutdown();
		try {
			while (pool.getActiveCount() > 0) {
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
