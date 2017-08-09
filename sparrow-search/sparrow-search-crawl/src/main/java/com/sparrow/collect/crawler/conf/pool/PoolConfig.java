package com.sparrow.collect.crawler.conf.pool;

import com.sparrow.collect.crawler.conf.AbstractConfigured;

/**
 * Created by Administrator on 2016/12/6.
 */
public class PoolConfig extends AbstractConfigured {
    //线程池最小线程数，核心线程
    private int min = PoolFactory.MIN_THREADS;
    //最大线程数
    private int max = PoolFactory.MAX_THREADS;
    //池的检测周期
    private int period = PoolFactory.CHECK_PERIOD;
    //队列大小
    private int size = PoolFactory.QUEUE_SIZE;
    //是否单实例
    @Deprecated
    private boolean single = false;
    //使用默认线程池，所有抓取过程中使用一个线程池
    private boolean useDefault = true;

    public boolean isSingle() {
        return single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isUseDefault() {
        return useDefault;
    }

    public void setUseDefault(boolean useDefault) {
        this.useDefault = useDefault;
    }
}
