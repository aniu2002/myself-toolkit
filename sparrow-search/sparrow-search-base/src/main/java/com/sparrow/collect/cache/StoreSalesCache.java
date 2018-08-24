package com.sparrow.collect.cache;

import com.sparrow.collect.space.MSBean;
import com.sparrow.collect.website.BeansFactory;
import com.sparrow.collect.website.redis.JedisTemplate;
import com.sparrow.collect.website.score.ExternalDataSource;
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
public class StoreSalesCache implements ExternalDataSource<Long, Integer> {
    private Log log = LogFactory.getLog(StoreSalesCache.class);

    private MSBean<StoreSalesCacheSupport> cacheMSBean = new MSBean<>();

    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    private static String storeSalesKeyPrefix = "socp_";

    public StoreSalesCache() {
        StoreSalesCacheSupport master = new StoreSalesCacheSupport();
        StoreSalesCacheSupport slaver = new StoreSalesCacheSupport();
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
    private class StoreSalesCacheSupport implements ExternalDataSource<Long, Integer> {

        private Log log = LogFactory.getLog(StoreSalesCacheSupport.class);

        private ConcurrentHashMap<Long, Integer> goodsSales = new ConcurrentHashMap();

        private JedisTemplate jedisTemplate;

        private int min = Integer.MAX_VALUE;

        private int max = 0;

        public StoreSalesCacheSupport() {
            jedisTemplate = BeansFactory.getInstance().getFilterJedisTemplate();
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
                    params.match(storeSalesKeyPrefix + "*");
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
                            Long sid = extractSid(keys.get(i));
                            if(sid == null) {
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
                            add(sid, saleCount);
                            count++;
                        }
                    } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
                    log.info("init store sales count = " + count);
                }
            });

            if (min == Integer.MAX_VALUE){
                min = 0;
            }

            long end = System.currentTimeMillis();
            log.info("wastetime = " + ((end - start) / 1000));
            log.info("init end");
        }

        public Long extractSid(String key) {
            if(key == null) {
                return null;
            }
            Long sid = null;
            try {
                sid = Long.valueOf(key.substring(storeSalesKeyPrefix.length()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return sid;
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
