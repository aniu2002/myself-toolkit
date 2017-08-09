package com.sparrow.data.tools.concurrency;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ConcurrencyJobManager {
	private static ConcurrencyJobManager instance;
	private final ThreadPoolExecutor threadPool;

	private ConcurrencyJobManager() {
		this.threadPool = new ThreadPoolExecutor(
				MuiltThreadSetting.threadMinCount,
				MuiltThreadSetting.threadMaxCount,
				MuiltThreadSetting.checkPeriod, TimeUnit.MINUTES,
				new ArrayBlockingQueue<Runnable>(
						MuiltThreadSetting.threadMinCount),
				new ThreadPoolExecutor.CallerRunsPolicy());
	}

	public static ConcurrencyJobManager getImportJobManager() {
		if (instance == null) {
			synchronized (ConcurrencyJobManager.class) {
				if (instance == null)
					instance = new ConcurrencyJobManager();
			}
		}
		return instance;
	}

	public Future<?> submit(Runnable runable) {
		return this.threadPool.submit(runable);
	}

	public void submitAndWait(Runnable[] runables) {
		ThreadPoolExecutor threadPool = this.threadPool;
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
}
