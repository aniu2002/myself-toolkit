package com.sparrow.common.concurrency;

import java.util.concurrent.*;

public class ThreadPool {
	public static final int DEFAULT_MIN_COUNT = 100;
	public static final int DEFAULT_MAX_COUNT = 200;
	public static final int DEFAULT_CHECK_PERIOD = 60;
	private ThreadPoolExecutor threadPool;
	private int minThreads = DEFAULT_MIN_COUNT;
	private int maxThreads = DEFAULT_MAX_COUNT;
	private int checkPeriod = DEFAULT_CHECK_PERIOD;

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

	ThreadPoolExecutor checkAndCreatePool() {
		if (this.threadPool != null)
			return this.threadPool;
		synchronized (this.synObject) {
			if (this.threadPool == null)
				this.threadPool = new ThreadPoolExecutor(this.minThreads,
						this.maxThreads, this.checkPeriod, TimeUnit.MINUTES,
						new ArrayBlockingQueue<Runnable>(this.minThreads),
						new ThreadPoolExecutor.CallerRunsPolicy());
		}
		return this.threadPool;
	}

	public Future<?> submit(Runnable runable) {
		ThreadPoolExecutor threadPool = this.checkAndCreatePool();
		return threadPool.submit(runable);
	}

	public <T> Future<T> submit(Callable<T> task) {
		ThreadPoolExecutor threadPool = this.checkAndCreatePool();
		return threadPool.submit(task);
	}

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
		if (pool == null)
			return;
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
