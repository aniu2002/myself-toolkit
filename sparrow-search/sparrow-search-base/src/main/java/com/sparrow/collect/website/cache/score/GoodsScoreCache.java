package com.sparrow.collect.website.cache.score;

import com.sparrow.collect.website.BeansFactory;
import com.sparrow.collect.website.redis.JedisTemplate;
import com.sparrow.collect.website.utils.NumericUtil;
import org.apache.commons.collections.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangtao on 2015/7/20.
 * 商品评分
 */
public class GoodsScoreCache extends ScoreCache<Long> {
    private static GoodsScoreCache instance;
    private static final int VALUES_COUNT = 2;
    private static final int COUNT = 1000;
    private static final String PREFIX = "search_score:pid:";

    public static GoodsScoreCache getInstance() {
        if(instance == null) {
            synchronized (GoodsScoreCache.class) {
                if(instance == null) {
                    instance = new GoodsScoreCache();
                }
            }
        }
        return instance;
    }

    private GoodsScoreCache() {
        GoodsScoreCacheSupport master = new GoodsScoreCacheSupport();
        GoodsScoreCacheSupport slaver = new GoodsScoreCacheSupport();
        cacheMSBean.setMaster(master);
        cacheMSBean.setSlave(slaver);
    }

    private class GoodsScoreCacheSupport implements ScoreCacheSupport<Long> {
        //pid : score[](对应各个算法计算所得值)
        private Map<Long, float[]> cache;
        private JedisTemplate jedis;

        public GoodsScoreCacheSupport() {
            jedis = BeansFactory.getInstance().getFilterJedisTemplate();
        }

        @Override
        public float getScore(Long key, Long userId) {
            return getScore(key, userId.hashCode());
        }

        /**
         * 根据用户标识和商品id确定分值
         * @param pid
         * @param userSign
         * @return
         */
        public float getScore(long pid, int userSign) {
            if(cache.containsKey(pid)) {
                int algorithm = userSign % VALUES_COUNT;
                return cache.get(pid)[algorithm];
            }
            return 0f;
        }

        @Override
        public void init() {
            cache = new HashMap();
            jedis.execute(new JedisTemplate.JedisActionNoResult() {
                @Override
                public void action(Jedis jedis) {
                    ScanParams params = new ScanParams();
                    params.match(PREFIX + "*");
                    params.count(COUNT);
                    //游标指针位置
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
                        Long pid = null;
                        float[] scores = null;
                        for (int i = 0; i < keys.size(); i++) {
                            pid = extractPid(keys.get(i));
                            if (pid == null) {
                                continue;
                            }
                            if (values.get(i) == null) {
                                continue;
                            }
                            scores = convert(values.get(i));
                            cache.put(pid, scores);
                        }
                    } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
                }
            });
        }

        @Override
        public void clear() {
            cache.clear();
            cache = null;
        }

        public Long extractPid(String key) {
            key = key.replaceFirst(PREFIX, "");
            return NumericUtil.toLong(key);
        }

        public float[] convert(String values) {
            float[] scores = new float[VALUES_COUNT];
            String[] vals = values.split(",", -1);
            int length = vals.length > VALUES_COUNT ? VALUES_COUNT : vals.length;
            for(int i=0; i<length; i++) {
                scores[i] = toFloat(vals[i]);
            }
            return scores;
        }

        public float toFloat(String value) {
            try {
                return Float.parseFloat(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return 0f;
        }
    }

}
