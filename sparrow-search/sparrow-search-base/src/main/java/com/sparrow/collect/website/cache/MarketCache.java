package com.sparrow.collect.website.cache;

import com.dili.dd.searcher.basesearch.common.bean.MSBean;
import com.dili.dd.searcher.basesearch.search.beans.BeansFactory;
import com.dili.dd.searcher.datainterface.domain.attribute.AttributeValue;
import com.diligrp.website.web.interfaces.WebsiteClient;
import com.diligrp.website.web.interfaces.domain.input.PickupTypeEnum;
import com.diligrp.website.web.interfaces.domain.output.PickUpPointResp;
import com.diligrp.website.web.interfaces.domain.output.ResultResp;
import com.diligrp.website.web.interfaces.service.PickUpApiService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by yangtao on 2015/7/28.
 */
public class MarketCache implements Cache {
    private Log log = LogFactory.getLog(MarketCache.class);

    private MSBean<MarketCacheSupport> cacheMSBean = new MSBean<>();

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private static MarketCache instance;

    public static MarketCache getInstance() {
        if(instance == null) {
            synchronized (MarketCache.class) {
                if(instance == null) {
                    instance = new MarketCache();
                }
            }
        }
        return instance;
    }

    public AttributeValue get(String id) {
        try {
            lock.writeLock().lock();
            return cacheMSBean.getMaster().get(id);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<AttributeValue> get(List<String> ids) {
        try {
            lock.writeLock().lock();
            return cacheMSBean.getMaster().get(ids);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void init() {
        log.info("init begin");
        try {
            lock.writeLock().lock();
            cacheMSBean.getMaster().init();
        } finally {
            lock.writeLock().unlock();
        }
        log.info("init end");
    }

    public void switchOver() {
        log.info("switchOver begin");
        cacheMSBean.getSlave().init();
        try {
            lock.writeLock().lock();
            cacheMSBean.switchOver();
            cacheMSBean.getSlave().clear();
        } finally {
            lock.writeLock().unlock();
        }
        log.info("switchOver end");
    }

    private MarketCache() {
        MarketCacheSupport master = new MarketCacheSupport();
        MarketCacheSupport slave = new MarketCacheSupport();
        cacheMSBean.setMaster(master);
        cacheMSBean.setSlave(slave);
    }

    private class MarketCacheSupport {
        private Map<String, AttributeValue> markets;

        private PickUpApiService service;

        public MarketCacheSupport() {
            markets = new HashMap();
        }

        public void initService() {
            WebsiteClient websiteClient = BeansFactory.getInstance().getWebsiteClient();
            service = websiteClient.getPickUpApiService();
        }

        public void initMarkets() {
            ResultResp<List<PickUpPointResp>> resp = service.getAllPickupInfo(PickupTypeEnum.MARKET);
            if(resp == null) {
                return;
            }
            List<PickUpPointResp> list = resp.getValue();
            if(CollectionUtils.isEmpty(list)) {
                return;
            }
            AttributeValue value = null;
            for (PickUpPointResp pointResp : list) {
                value = new AttributeValue();
                value.setId(pointResp.getId().toString());
                value.setName(pointResp.getPickName());
                markets.put(value.getId(), value);
            }
        }

        public AttributeValue get(String id) {
            return markets.get(id);
        }

        public List<AttributeValue> get(List<String> ids) {
            List<AttributeValue> values = new ArrayList(ids.size());
            AttributeValue value = null;
            for(String id : ids) {
                value = get(id);
                if(value == null) {
                    continue;
                }
                values.add(value);
            }
            return values;
        }

        public void init() {
            initService();
            initMarkets();
        }

        public void clear() {
            markets.clear();
        }
    }
}
