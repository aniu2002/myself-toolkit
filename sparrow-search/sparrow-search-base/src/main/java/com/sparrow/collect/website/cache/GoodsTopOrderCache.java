package com.sparrow.collect.website.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dili.dd.searcher.basesearch.search.beans.BeansFactory;
import com.dili.dd.searcher.common.redis.JedisTemplate;
import com.dili.dd.searcher.datainterface.domain.goods.TopOrderType;
import com.diligrp.titan.sdk.TitanClient;
import com.diligrp.titan.sdk.constant.TopTypeEnum;
import com.diligrp.titan.sdk.domain.ProductSearchSort;
import com.diligrp.titan.sdk.output.BaseOutput;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.Jedis;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangtao on 2015/11/13.
 */
public class GoodsTopOrderCache {
    protected Log log = LogFactory.getLog(this.getClass());

    //商品id:排序号映射关系
    private Map<Long, Integer> scoreCache;
    //置顶类别
    private TopOrderType topType;
    //置顶类别值
    private Number topTypeValue;

    public GoodsTopOrderCache(TopOrderType topType, Number topTypeValue) {
        this.topType = topType;
        this.topTypeValue = topTypeValue;
    }

    public Integer getOrder(Long pid) {
        return scoreCache.get(pid);
    }

    /**
     * 初始化scoreCache
     */
    public void initCache() {
        this.scoreCache = readFromRedis(topType, topTypeValue);
        if(this.scoreCache != null) {
            return;
        }
        this.scoreCache = readFromRemoteApi(topType, topTypeValue);
        if(this.scoreCache != null) {
            saveToRedis(topType, topTypeValue, this.scoreCache);
        }
    }

    public Map<Long, Integer> readFromRemoteApi(TopOrderType topType, Number topTypeValue) {
        TopTypeEnum topTypeEnum = convert(topType);
        if(topTypeEnum == null) {
            return null;
        }
        return readFromRemoteApi(topTypeEnum, topTypeValue);
    }

    /**
     * 从接口中读取置顶商品数据
     * @param topTypeEnum
     * @param topTypeValue
     * @return
     */
    public Map<Long, Integer> readFromRemoteApi(TopTypeEnum topTypeEnum, Number topTypeValue) {
        Map<Long, Integer> orderMap = new LinkedHashMap();
        TitanClient titanClient = BeansFactory.getInstance().getTitanClient();
        BaseOutput<List<ProductSearchSort>> orderList = titanClient.getProductService().getProductSearchSort(topTypeValue.longValue(), topTypeEnum);
        if(orderList == null || CollectionUtils.isEmpty(orderList.getData())) {
            return orderMap;
        }
        for(ProductSearchSort searchSort : orderList.getData()) {
            orderMap.put(searchSort.getPid(), searchSort.getSearchSort());
        }
        return orderMap;
    }

    /**
     * 从redis中读取置顶商品数据
     * @param topType
     * @param topTypeValue
     * @return
     */
    public Map<Long, Integer> readFromRedis(TopOrderType topType, Number topTypeValue) {
        final String key = topType + "_" + topTypeValue;
        JedisTemplate jedisTemplate = BeansFactory.getInstance().getUserRefJedisTemplate();
        return jedisTemplate.execute(new JedisTemplate.JedisAction<Map<Long, Integer>>() {
            @Override
            public Map<Long, Integer> action(Jedis jedis) {
                try {
                    jedis.select(2);
                    String value = jedis.get(key);
                    if(value == null) {
                        return null;
                    }
                    return JSON.parseObject(value, new TypeReference<Map<Long, Integer>>() {
                    });
                } catch (Exception e) {
                    log.error("从redis中读取置顶商品信息异常:", e);
                }
                return null;
            }
        });
    }

    /**
     * 将置顶商品数据存入到redis中
     * @param topType
     * @param topTypeValue
     * @param scoreCache
     */
    public void saveToRedis(TopOrderType topType, Number topTypeValue, final Map<Long, Integer> scoreCache) {
        final String key = topType + "_" + topTypeValue;
        JedisTemplate jedisTemplate = BeansFactory.getInstance().getUserRefJedisTemplate();
        jedisTemplate.execute(new JedisTemplate.JedisActionNoResult() {
            @Override
            public void action(Jedis jedis) {
                try {
                    jedis.select(2);
                    jedis.set(key, JSON.toJSONString(scoreCache));
                } catch (Exception e) {
                    log.error("从redis中读取置顶商品信息异常:", e);
                }
            }
        });
    }

    public TopTypeEnum convert(TopOrderType topOrderType) {
        return TopTypeEnum.getEnum(topOrderType.getOrderType());
    }
}
