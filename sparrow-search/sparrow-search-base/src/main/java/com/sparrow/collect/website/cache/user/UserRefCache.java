package com.sparrow.collect.website.cache.user;

import com.sparrow.collect.website.BeansFactory;
import com.sparrow.collect.website.cache.score.ScoreCache;
import com.sparrow.collect.website.redis.JedisTemplate;
import com.sparrow.collect.website.utils.NumericUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.*;

/**
 * Created by yangtao on 2015/7/23.
 */
public class UserRefCache extends ScoreCache<Map> {
    private static UserRefCache instance;
    private static final int COUNT = 1000;
    private static final String PREFIX = "search_score:user_id:";

    public static UserRefCache getInstance() {
        if(instance == null) {
            synchronized (UserRefCache.class) {
                if(instance == null) {
                    instance = new UserRefCache();
                }
            }
        }
        return instance;
    }

    private UserRefCache() {
        UserRefCacheSupport master = new UserRefCacheSupport();
        UserRefCacheSupport slaver = new UserRefCacheSupport();
        cacheMSBean.setMaster(master);
        cacheMSBean.setSlave(slaver);
    }

    private class UserRefCacheSupport implements ScoreCacheSupport<Map> {
        private Map<Long, UserRef> cache;
        private JedisTemplate jedis;

        public UserRefCacheSupport() {
            jedis = BeansFactory.getInstance().getFilterJedisTemplate();
        }

        @Override
        public float getScore(Map key, Long userId) {
            if(!cache.containsKey(userId)) {
                return 0f;
            }
            float score = 0f;
            UserRef userRef = cache.get(userId);

            Long price = (Long)key.get("price");
            if(price != null) {
                score += scorePrice(userRef, price);
            }
            Integer cid = (Integer)key.get("cid");
            if(cid != null) {
                score += scoreCategory(userRef, cid);
            }
            Long pid = (Long)key.get("pid");
            if(pid != null) {
                score += scoreProduct(userRef, pid);
            }
            Long shopId = (Long)key.get("shopId");
            if(shopId != null) {
                score += scoreShop(userRef, shopId);
            }
            return score;
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
                Map<String, String> values = null;

                do {
                    scanResult = jedis.scan(cursor, params);
                    cursor = scanResult.getStringCursor();
                    keys = scanResult.getResult();
                    if (CollectionUtils.isEmpty(keys)) {
                        continue;
                    }
                    for (String key : keys) {
                        values = jedis.hgetAll(key);
                        if (values == null) {
                            continue;
                        }
                        Long userId = extractUserId(key);
                        if (userId == null) {
                            continue;
                        }
                        cache.put(userId, build(values));
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

        public float scorePrice(UserRef userRef, Long price) {
            if(price >= userRef.getPrices()[0] && price <= userRef.getPrices()[1]) {
                return 0.1f;
            }
            return 0.0f;
        }

        public float scoreShop(UserRef userRef, Long shopId) {
            if(userRef.getAttentionShops().contains(shopId)) {
                return 0.3f;
            }
            return 0.0f;
        }

        public float scoreCategory(UserRef userRef, Integer cid) {
            if(userRef.getAttentionCategories().contains(cid)) {
                return 0.3f;
            }
            return 0.0f;
        }

        public float scoreProduct(UserRef userRef, Long pid) {
            if(userRef.getGoodComment().contains(pid)) {
                return 0.3f;
            } else if(userRef.getBadComment().contains(pid)) {
                return -0.3f;
            }
            return 0f;
        }

        public Long extractUserId(String key) {
            key = key.replaceFirst(PREFIX, "");
            return NumericUtil.toLong(key);
        }

        public UserRef build(Map<String, String> values) {
            UserRef userRef = new UserRef();
            setPrices(userRef, values.get("intend"));
            setAttentionCategories(userRef, values.get("category"));
            setAttentionShop(userRef, values.get("shop_id"));
            setGoodCommet(userRef, values.get("good_rate"));
            setBadComment(userRef, values.get("bad_rate"));
            return userRef;
        }

        public Set<Long> getPids(String values) {
            Set<Long> goodsIds = null;
            if(values == null) {
                goodsIds = new HashSet(0);
                return goodsIds;
            }
            String[] vals = values.split(",", -1);
            goodsIds = new HashSet(vals.length);
            String pid = null;
            for (int i=0; i<vals.length; i++) {
                pid = vals[i];
                if(NumberUtils.isNumber(pid)) {
                    goodsIds.add(NumberUtils.createLong(pid));
                }
            }
            return goodsIds;
        }

        /**
         * 设置给予过差评的商品
         * @param userRef
         * @param values
         */
        public void setBadComment(UserRef userRef, String values) {
            Set<Long> goodsIds = getPids(values);
            userRef.setBadComment(goodsIds);
        }

        /**
         * 设置给予过好评的商品
         * @param userRef
         * @param values
         */
        public void setGoodCommet(UserRef userRef, String values) {
            Set<Long> goodsIds = getPids(values);
            userRef.setGoodComment(goodsIds);
        }

        /**
         * 设置关注店铺
         * @param userRef
         * @param values
         */
        public void setAttentionShop(UserRef userRef, String values) {
            Set<Long> shopIds = null;
            if(values == null) {
                shopIds = new HashSet(0);
                userRef.setAttentionShops(shopIds);
                return;
            }
            String[] vals = values.split(",", -1);
            shopIds = new HashSet(vals.length);
            for (int i=0; i<vals.length; i++) {
                if(NumberUtils.isNumber(vals[i])) {
                    shopIds.add(NumberUtils.createLong(vals[i]));
                }
            }
            userRef.setAttentionShops(shopIds);
        }

        /**
         * 设置关注分类
         * @param userRef
         * @param values
         */
        public void setAttentionCategories(UserRef userRef, String values) {
            Set<Integer> categoryIds = null;
            if(values == null) {
                categoryIds = new HashSet(0);
                userRef.setAttentionCategories(categoryIds);
                return;
            }
            String[] vals = values.split(",", -1);
            categoryIds = new HashSet(vals.length);
            for (int i=0; i<vals.length; i++) {
                if(NumberUtils.isNumber(vals[i])) {
                    categoryIds.add(NumberUtils.createInteger(vals[i]));
                }
            }
            userRef.setAttentionCategories(categoryIds);
        }

        /**
         * 设置价格区间
         * @param userRef
         * @param values
         */
        public void setPrices(UserRef userRef, String values) {
            long[] prices = new long[2];
            if (values == null) {
                userRef.setPrices(prices);
                return;
            }
            String[] vals = values.split(",", -1);
            int length = vals.length > 2 ? 2 : vals.length;
            for(int i=0; i<length; i++) {
                prices[i] = toLong(vals[i]);
            }
            userRef.setPrices(prices);
        }

        public long toLong(String value) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return 0l;
        }
    }

}
