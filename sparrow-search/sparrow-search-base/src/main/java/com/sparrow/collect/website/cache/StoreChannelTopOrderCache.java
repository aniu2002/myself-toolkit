package com.sparrow.collect.website.cache;

import com.alibaba.fastjson.JSONArray;
import com.sparrow.collect.website.BeansFactory;
import com.sparrow.collect.website.redis.JedisTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * 渠道,市场/区域,类目缓存置顶的店铺
 * Created by yaobo on 2015/12/29.
 */
public class StoreChannelTopOrderCache {
    protected Log log = LogFactory.getLog(this.getClass());

    public static String STORE_CHANNEL_TOP_PREFIX = "store_channel_top_";

    private JedisTemplate jedisTemplate;

    private Map<Long, Integer> orderCache;

    private List<Long> sids;

    private Map<Long, Integer> tags;

    public StoreChannelTopOrderCache(Long channelId, Long secondId) {
        try {
            this.jedisTemplate = BeansFactory.getInstance().getFilterJedisTemplate();
            this.init(channelId, secondId);
        } catch (Exception e) {
            log.error("创建StoreChannelTopOrderCache出错", e);
        }
    }

    public List<Long> getSids() {
        return sids;
    }

    public Integer getOrder(Long pid) {
        if (orderCache == null){
            return null;
        }
        return orderCache.get(pid);
    }

    public Map<Long, Integer> getTags(){
        return tags;
    }

    private void init(Long channelId, Long secondId) {
        List<Map> datas = this.getTopData(channelId, secondId);
        if (datas != null) {
            orderCache = new HashMap<>();
            sids = new ArrayList<>();
            tags = new HashMap<>();
            for (int i = 0; i < datas.size(); i++) {
                Map map = datas.get(i);
                Long sid = Long.valueOf(map.get("id").toString());
                Integer tag = Integer.valueOf(map.get("tag").toString());
                orderCache.put(sid, i);
                sids.add(sid);
                tags.put(sid, tag);
            }
        }
    }

    private List<Map> getTopData(Long channelId, Long secondId) {
        String key = this.createKey(channelId, secondId);
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

    private String createKey(Long channelId, Long secondId) {
        String key = new StringBuilder().append(STORE_CHANNEL_TOP_PREFIX).append(channelId).append("_").append(secondId).toString();
        return key;
    }

    public static void main(String[] args) {
        String array = "[{id:1,tag:0},{id:2,tag:1},{id:3,tag:3}]";
        List<Map> datas = JSONArray.parseArray(array, Map.class);
        System.out.println(Arrays.toString(datas.toArray(new Map[]{})));
    }

}
