package com.sparrow.collect.website.cache;

import com.dili.dd.searcher.basesearch.common.bean.MSBean;
import com.dili.dd.searcher.basesearch.common.sort.ExternalDataSource;
import com.dili.dd.searcher.basesearch.search.beans.BeansFactory;
import com.dili.dd.searcher.common.redis.JedisTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by yaobo on 2014/8/25.
 */
public class GoodsSalesCache implements ExternalDataSource<Long, Integer> {
    private Log log = LogFactory.getLog(com.dili.dd.searcher.basesearch.search.searcher.cache.GoodsSalesCache.class);

    private MSBean<GoodsSalesCacheSupport> cacheMSBean = new MSBean<>();

    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    private static String goodsSalesKeyPrefix = "goodsOrderCountPrefix_";

    public GoodsSalesCache() {
        GoodsSalesCacheSupport master = new GoodsSalesCacheSupport();
        GoodsSalesCacheSupport slaver = new GoodsSalesCacheSupport();
        cacheMSBean.setMaster(master);
        cacheMSBean.setSlave(slaver);
    }

    @Override
    public Integer getValue(Long aLong) {
        try {
            rwLock.readLock().lock();
           return cacheMSBean.getMaster().getValue(aLong);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void init(){
        try {
            rwLock.writeLock().lock();
            cacheMSBean.getMaster().init();
            //read from redis
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void switchOver() {
        log.info("switchOver begin");
        cacheMSBean.getSlave().init();
        try {
            rwLock.writeLock().lock();
            log.info("lock");
            cacheMSBean.switchOver();
            cacheMSBean.getSlave().clear();
        } finally {
            rwLock.writeLock().unlock();
            log.info("unlock");
        }
        log.info("switchOver end");
    }

    public int getMax(){
        try {
            rwLock.writeLock().lock();
            return cacheMSBean.getMaster().max;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public int getMin(){
        try {
            rwLock.writeLock().lock();
            return cacheMSBean.getMaster().min;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void add(Long pid, Integer sales) {
        try {
            rwLock.writeLock().lock();
            cacheMSBean.getMaster().add(pid, sales);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * 从redis中加载商品销售数据
     * Created by yaobo on 2014/8/25.
     */
    private class GoodsSalesCacheSupport implements ExternalDataSource<Long, Integer> {

        private Log log = LogFactory.getLog(GoodsSalesCacheSupport.class);

        private ConcurrentHashMap<Long, Integer> goodsSales = new ConcurrentHashMap();

        private JedisTemplate jedisTemplate;

        private int min = Integer.MAX_VALUE;

        private int max = 0;

        public GoodsSalesCacheSupport() {
            jedisTemplate = BeansFactory.getInstance().getRecommendJedisTemplate();
        }


        @Override
        public Integer getValue(Long aLong) {
            return goodsSales.get(aLong);
        }

        public void init() {
            log.info("init begin");
            long start = System.currentTimeMillis();

            goodsSales.clear();
            //read from redis

            jedisTemplate.execute(new JedisTemplate.JedisActionNoResult() {
                @Override
                public void action(Jedis jedis) {
                    int count = 0;
                    //取出所有gocp_的key. (kv=pid:sales)
                    ScanParams params = new ScanParams();
                    params.match(goodsSalesKeyPrefix + "*");
                    params.count(200);
                    String cursor = ScanParams.SCAN_POINTER_START;
                    //scan结果
                    ScanResult<String> scanResult = null;
                    //key
                    List<String> keys = null;
                    //value
                    List<String> values = null;
                    do {
                        scanResult = jedis.scan(cursor, params);
                        cursor = scanResult.getStringCursor();
                        keys = scanResult.getResult();
                        if (CollectionUtils.isEmpty(keys)) {
                            continue;
                        }
                        values = jedis.mget(keys.toArray(new String[]{}));
                        if (CollectionUtils.isEmpty(values)) {
                            continue;
                        }
                        for (int i = 0; i < keys.size(); i++) {
                            Long pid = extractPid(keys.get(i));
                            if(pid == null) {
                                continue;
                            }
                            Integer saleCount = convert(values.get(i));
                            if(saleCount == null) {
                                continue;
                            }
                            if (saleCount > max){
                                max = saleCount;
                            }
                            if (saleCount < min){
                                min = saleCount;
                            }
                            add(pid, saleCount);
                            count++;
                        }
                    } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
                    log.info("init goods sales count = " + count);
                }
            });

            if (min == Integer.MAX_VALUE){
                min = 0;
            }

            long end = System.currentTimeMillis();
            log.info("wastetime = " + ((end - start) / 1000));
            log.info("init end");
        }

        public Long extractPid(String key) {
            if(key == null) {
                return null;
            }
            Long pid = null;
            try {
                pid = Long.valueOf(key.substring(goodsSalesKeyPrefix.length()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return pid;
        }

        public Integer convert(String value) {
            if(value == null) {
                return null;
            }
            Integer saleCount = null;
            try {
                saleCount = Integer.valueOf(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return saleCount;
        }

        public void add(Long pid, Integer sales) {
            goodsSales.put(pid, sales);
        }

        public void clear() {
            this.goodsSales.clear();
        }
    }
}
