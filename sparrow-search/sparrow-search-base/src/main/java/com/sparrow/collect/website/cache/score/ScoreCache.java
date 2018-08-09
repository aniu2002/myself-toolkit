package com.sparrow.collect.website.cache.score;

import com.sparrow.collect.space.MSBean;
import com.sparrow.collect.website.cache.Cache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by yangtao on 2015/7/21.
 */
public abstract class ScoreCache<T> implements Cache {
    protected Log log = LogFactory.getLog(this.getClass());

    protected MSBean<ScoreCacheSupport> cacheMSBean = new MSBean();

    protected ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public float get(T key, Long userId) {
        try {
            lock.readLock().lock();
            return cacheMSBean.getMaster().getScore(key, userId);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void init(){
        log.info("init begin");
        try {
            lock.writeLock().lock();
            cacheMSBean.getMaster().init();
        } finally {
            lock.writeLock().unlock();
        }
        log.info("init end");
    }

    public void switchOver() {
        log.info("switchOver begin");
        cacheMSBean.getSlave().init();
        try {
            lock.writeLock().lock();
            cacheMSBean.switchOver();
            cacheMSBean.getSlave().clear();
        } finally {
            lock.writeLock().unlock();
        }
        log.info("switchOver end");
    }



    protected interface ScoreCacheSupport<T> {
        /**
         * 从缓存中根据key获取相应分值
         * @param key
         * @param userId    用户id
         * @return  分值
         */
        float getScore(T key, Long userId);

        /**
         * 初始化缓存
         */
        void init();

        /**
         * 清除缓存
         */
        void clear();
    }
}
