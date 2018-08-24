package com.sparrow.collect.website;

import com.sparrow.collect.cache.StoreSalesCache;
import com.sparrow.collect.website.redis.JedisPoolFactory;
import com.sparrow.collect.website.redis.JedisTemplate;
import com.sparrow.core.log.Logger;
import com.sparrow.core.log.LoggerManager;

/**
 * Created by Administrator on 2018/8/9.
 */
public class BeansFactory {
    private static final Logger log = LoggerManager.getSysLog();
    private static BeansFactory beansFactory = new BeansFactory();
    private JedisTemplate filterJedisTemplate;
    private JedisTemplate userRefJedisTemplate;
    private StoreSalesCache storeSalesCache;

    private BeansFactory() {
        this.initJedisTemplate();
        this.initStoreSalesCache();
    }

    public static BeansFactory getInstance() {
        return beansFactory;
    }

    private void initJedisTemplate() {
        log.info("initJedisTemplate begin");
        String host = Configs.get("redis.recommend.host");
        Integer port = Configs.getInt("redis.recommend.port");
        Integer timeout = Configs.getInt("redis.recommend.timeout");
        Integer threadCount = Configs.getInt("redis.recommend.thread.count");
        filterJedisTemplate = new JedisTemplate(JedisPoolFactory.createJedisPool(host, port, timeout, threadCount));
        log.info("initJedisTemplate end");

        host = Configs.get("redis.score.userDef.host");
        port = Configs.getInt("redis.score.userDef.port");
        timeout = Configs.getInt("redis.score.userDef.timeout");
        threadCount = Configs.getInt("redis.score.userDef.thread.count");
        userRefJedisTemplate = new JedisTemplate(JedisPoolFactory.createJedisPool(host, port, timeout, threadCount));
    }

    public JedisTemplate getFilterJedisTemplate() {
        return filterJedisTemplate;
    }

    private void initStoreSalesCache() {
        log.info("initStoreSalesCache begin");
        storeSalesCache = new StoreSalesCache();
        storeSalesCache.init();
        log.info("initStoreSalesCache end");
    }

    public StoreSalesCache getStoreSalesCache() {
        return storeSalesCache;
    }

    public JedisTemplate getUserRefJedisTemplate() {
        return userRefJedisTemplate;
    }
}
