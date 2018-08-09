package com.sparrow.collect.website.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by yaobo on 2014/8/25.
 */
public class PLCGoodsCommentCache implements ExternalDataSource<Long, Float> {
    private Log log = LogFactory.getLog(PLCGoodsCommentCache.class);

    private MSBean<GoodsCommentCacheSupport> cacheMSBean = new MSBean<>();

    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    private static String goodsCommentKeyPrefix = "gooodsCommentPreifx_";

    public PLCGoodsCommentCache() {
        GoodsCommentCacheSupport master = new GoodsCommentCacheSupport();
        GoodsCommentCacheSupport slaver = new GoodsCommentCacheSupport();
        cacheMSBean.setMaster(master);
        cacheMSBean.setSlave(slaver);
    }

    @Override
    public Float getValue(Long aLong) {
        try {
            rwLock.readLock().lock();
            return cacheMSBean.getMaster().getValue(aLong);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void init() {
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

    /**
     * 从redis中加载商品销售数据
     * Created by yaobo on 2014/8/25.
     */
    private class GoodsCommentCacheSupport implements ExternalDataSource<Long, Float> {

        private Log log = LogFactory.getLog(GoodsCommentCacheSupport.class);

        private ConcurrentHashMap<Long, Float> positiveFeedbacks = new ConcurrentHashMap();

        private JedisTemplate jedisTemplate;

        public GoodsCommentCacheSupport() {
            jedisTemplate = BeansFactory.getInstance().getRecommendJedisTemplate();
        }


        @Override
        public Float getValue(Long aLong) {
            return positiveFeedbacks.get(aLong);
        }

        public void init() {
            log.info("init begin");
            long start = System.currentTimeMillis();
            positiveFeedbacks.clear();
            //read from redis

            jedisTemplate.execute(new JedisTemplate.JedisActionNoResult() {
                @Override
                public void action(Jedis jedis) {
                    int count = 0;
                    //取出所有goodsCommentPreifx_的key. (kv=pid:pfb)
                    ScanParams params = new ScanParams();
                    params.match(goodsCommentKeyPrefix + "*");
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
                            Float comment = convert(values.get(i));
                            if(comment == null) {
                                continue;
                            }
                            positiveFeedbacks.put(pid, comment);
                            count++;
                        }
                    } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
                    log.info("init plc goods comment count = " + count);
                }
            });

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
                pid = Long.valueOf(key.substring(goodsCommentKeyPrefix.length()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return pid;
        }

        public Float convert(String value) {
            if(value == null) {
                return null;
            }
            Float comment = null;
            try {
                Map<String, String> map = JSON.parseObject(value, new TypeReference<Map<String, String>>() {});
                if(map.containsKey("positiveFeedBack")) {
                    comment = Float.valueOf(map.get("positiveFeedBack"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return comment;
        }

        public void clear() {
            this.positiveFeedbacks.clear();
        }
    }
}
