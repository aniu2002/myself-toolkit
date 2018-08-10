package com.sparrow.collect.website.cache;

import com.alibaba.fastjson.JSONArray;
import com.sparrow.collect.website.BeansFactory;
import com.sparrow.collect.website.redis.JedisTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * 渠道,市场/区域,类目缓存置顶的商品
 * Created by yaobo on 2015/12/28.
 */
public class GoodsChannelTopOrderCache {
    protected Log log = LogFactory.getLog(this.getClass());

    public static String GOODS_CHANNEL_TOP_PREFIX = "goods_channel_top_";

    private JedisTemplate jedisTemplate;

    private Map<Long, Integer> orderCache;

    private List<Long> pids;

    private Map<Long, Integer> tags;

    public GoodsChannelTopOrderCache(Long channelId, Long secondId, Long cid) {
        try {
            this.jedisTemplate = BeansFactory.getInstance().getFilterJedisTemplate();
            this.init(channelId, secondId, cid);
        } catch (Exception e) {
            log.error("创建GoodsChannelTopOrderCache出错", e);
        }
    }

    public List<Long> getPids(){
        return pids;
    }

    public Integer getOrder(Long pid){
        if (orderCache == null){
            return null;
        }
        return orderCache.get(pid);
    }

    public Map<Long, Integer> getTags(){
        return tags;
    }

    private void init(Long channelId, Long secondId, Long cid) {
        List<Map> datas = this.getTopData(channelId, secondId, cid);
        if (datas != null) {
            orderCache = new HashMap<>();
            pids = new ArrayList<>();
            tags = new HashMap<>();
            for (int i = 0; i < datas.size(); i++) {
                Map map = datas.get(i);
                Long pid = Long.valueOf(map.get("id").toString());
                Integer tag = Integer.valueOf(map.get("tag").toString());
                orderCache.put(pid, i);
                pids.add(pid);
                tags.put(pid, tag);
            }
        }
    }

    private List<Map> getTopData(Long channelId, Long secondId, Long cid) {
        String key = this.createKey(channelId, secondId, cid);
        if (key == null) {
            return null;
        } else {
            String value = jedisTemplate.get(key);
            if (value == null) {
                return null;
            }
            List<Map> datas = JSONArray.parseArray(value, Map.class);
            return datas;
        }
    }

    private String createKey(Long channelId, Long secondId, Long cid) {
        String key = null;
        if (channelId != null && secondId != null && cid != null) {
            key = new StringBuilder().append(GOODS_CHANNEL_TOP_PREFIX).append(channelId).append("_").append(secondId).append("_").append(cid).toString();
        } else if (channelId != null && secondId != null) {
            key = new StringBuilder().append(GOODS_CHANNEL_TOP_PREFIX).append(channelId).append("_").append(secondId).toString();
        }
        return key;
    }

    public static void main(String[] args) {
        String array = "[{id:1,tag:0},{id:2,tag:1},{id:3,tag:3}]";
        List<Map> datas = JSONArray.parseArray(array, Map.class);
        System.out.println(Arrays.toString(datas.toArray(new Map[]{})));
    }

}
