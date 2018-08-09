package com.sparrow.collect.website.cache;


import com.sparrow.collect.website.cache.filter.InactivityShopCache;
import com.sparrow.collect.website.cache.score.GoodsScoreCache;
import com.sparrow.collect.website.cache.score.ShopScoreCache;
import com.sparrow.collect.website.cache.user.UserRefCache;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangtao on 2015/7/23.
 */
public class CacheManager {
    public static Map<String, Cache> caches = new HashMap();

    public static void addCache(Cache cache) {
        caches.put(cache.getClass().getSimpleName(), cache);
    }

    /**
     * 初始化商品评分缓存
     */
    public static void initGoodsScoreCache() {
        GoodsScoreCache.getInstance().init();
        addCache(GoodsScoreCache.getInstance());
    }

    /**
     * 初始化店铺评分缓存
     */
    public static void initShopScoreCache() {
        ShopScoreCache.getInstance().init();
        addCache(ShopScoreCache.getInstance());
    }

    /**
     * 初始化用户喜好缓存
     */
    public static void initUserScoreCache() {
        UserRefCache.getInstance().init();
        addCache(UserRefCache.getInstance());
    }

    /**
     * 初始化市场缓存
     */
    public static void initMarketCache() {
        MarketCache.getInstance().init();
        addCache(MarketCache.getInstance());
    }

    /**
     * 初始化城市缓存
     */
    public static void initCityCache() {
        CityCache.getInstance().init();
        addCache(CityCache.getInstance());
    }

    /**
     * 初始化非活跃店铺缓存
     */
    public static void initInactivityShopCache() {
        InactivityShopCache.getInstance().init();
        addCache(InactivityShopCache.getInstance());
    }
}
