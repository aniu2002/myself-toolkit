package com.sparrow.collect.website.cache.filter;


import com.sparrow.collect.space.MSBean;
import com.sparrow.collect.website.BeansFactory;
import com.sparrow.collect.website.cache.Cache;
import com.sparrow.collect.website.redis.JedisTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by yangtao on 2015/12/29.
 */
public class InactivityShopCache implements Cache {
    private Log log = LogFactory.getLog(this.getClass());
    private static InactivityShopCache instance;
    private static final int COUNT = 1000;
    private static final String KEY = "hd_zombie_shop_stats";
    private static final Integer DATABASE = 1;

    protected ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    protected MSBean<CacheSupport> cacheMSBean = new MSBean();

    public static InactivityShopCache getInstance() {
        if(instance == null) {
            synchronized (InactivityShopCache.class) {
                if(instance == null) {
                    instance = new InactivityShopCache();
                }
            }
        }
        return instance;
    }

    private InactivityShopCache() {
        CacheSupport master = new CacheSupport();
        CacheSupport slaver = new CacheSupport();
        cacheMSBean.setMaster(master);
        cacheMSBean.setSlave(slaver);
    }

    private class CacheSupport {
        //shopId缓存
        private Set<Long> cache;
        private JedisTemplate jedis;

        public CacheSupport() {
            jedis = BeansFactory.getInstance().getFilterJedisTemplate();
        }

        public void init() {
            cache = new HashSet();
            jedis.execute(new JedisTemplate.JedisActionNoResult() {
                @Override
                public void action(Jedis jedis) {
                    //value
                    jedis.select(DATABASE);
                    List<String> values = jedis.lrange(KEY, 0, -1);
                    if(CollectionUtils.isEmpty(values)) {
                        return;
                    }
                    for(String value : values) {
                        try {
                            cache.add(Long.valueOf(value));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        public void clear() {
            cache.clear();
            cache = null;
        }

        public boolean contains(Long key) {
            return cache.contains(key);
        }
    }

    public boolean filter(Long shopId) {
        try {
            lock.writeLock().lock();
            return cacheMSBean.getMaster().contains(shopId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void init() {
        log.info("init begin");
        try {
            lock.writeLock().lock();
            cacheMSBean.getMaster().init();
        } finally {
            lock.writeLock().unlock();
        }
        log.info("init end");
    }

    @Override
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
}
