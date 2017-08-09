package com.sparrow.collect.crawler.conf.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/12/2.
 */
public abstract class PoolFactory {
    public static final int MIN_THREADS = 15;
    public static final int MAX_THREADS = 30;
    public static final int CHECK_PERIOD = 2;
    public static final int QUEUE_SIZE = 500;

    private static ThreadPoolExecutor defaultPool;
    private static final Object synObject = new Object();

    public static ThreadPoolExecutor getDefault() {
        if (defaultPool == null) {
            synchronized (synObject) {
                if (defaultPool == null)
                    defaultPool = newPool(MIN_THREADS, MAX_THREADS, CHECK_PERIOD, QUEUE_SIZE);
            }
        }
        return defaultPool;
    }

    public static final void initializeDefaultPool(PoolConfig config) {
        if (defaultPool == null) {
            synchronized (synObject) {
                if (defaultPool == null)
                    defaultPool = newPool(config.getMin(), config.getMax(), config.getPeriod(), config.getSize());
            }
        }
    }

    public static final ThreadPoolExecutor newPool(PoolConfig config) {
        return newPool(config.getMin(), config.getMax(), config.getPeriod(), config.getSize());
    }

    public static final ThreadPoolExecutor newPool(int minThreadCount, int maxThreadCount, int checkPeriod, int queueSize) {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                minThreadCount,
                maxThreadCount,
                checkPeriod,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<Runnable>(queueSize),
                new ThreadPoolExecutor.CallerRunsPolicy());
        //threadPool.prestartCoreThread()
        return threadPool;
    }

    public static void closeThreadPool(ThreadPoolExecutor pool) {
        if (pool == null)
            return;
        pool.shutdown();
        try {
            while (pool.getActiveCount() > 0) {
                pool.awaitTermination(1, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void waitActiveTasks(ThreadPoolExecutor pool) {
        if (pool == null)
            return;
        try {
            while (pool.getActiveCount() > 0) {
                pool.awaitTermination(1, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
