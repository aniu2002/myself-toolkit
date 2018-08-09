package com.sparrow.collect.website.cache;

import com.dili.dd.searcher.basesearch.common.bean.MSBean;
import com.dili.dd.searcher.basesearch.common.bean.MSModel;
import com.dili.dd.searcher.basesearch.common.sort.ExternalDataSource;
import com.diligrp.titan.sdk.TitanClient;
import com.diligrp.titan.sdk.domain.ProductSearchSort;
import com.diligrp.titan.sdk.output.BaseOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by yaobo on 2014/8/29.
 */
public class GoodsManualOrderCache implements ExternalDataSource<Long, Integer>, MSModel {

    private Log log = LogFactory.getLog(GoodsManualOrderCache.class);

    private MSBean<GoodsManualOrderCacheSupport> cacheMSBean = new MSBean<>();

    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    private TitanClient titanClient;

    public GoodsManualOrderCache() {
        GoodsManualOrderCacheSupport master = new GoodsManualOrderCacheSupport();
        GoodsManualOrderCacheSupport slaver = new GoodsManualOrderCacheSupport();
        cacheMSBean.setMaster(master);
        cacheMSBean.setSlave(slaver);

        Configuration config = new Configuration();
        config.addResource(GoodsManualOrderCache.class.getClassLoader().getResource("searcher-env.xml"));
        titanClient = new TitanClient(config.get("searcher.datainterface.titan.accesskey"),
                config.get("searcher.datainterface.titan.secretkey"),
                config.get("searcher.datainterface.titan.url"));

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

    public void init() {
        try {
            rwLock.writeLock().lock();
            cacheMSBean.getMaster().init();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public boolean switchOver() {
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
        return true;
    }

    public void add(Long pid, Integer order) {
        try {
            rwLock.writeLock().lock();
            cacheMSBean.getMaster().add(pid, order);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    class GoodsManualOrderCacheSupport implements ExternalDataSource<Long, Integer> {
        private Map<Long, Integer> manualOrders = new ConcurrentHashMap();


        @Override
        public Integer getValue(Long aLong) {
            return manualOrders.get(aLong);
        }

        public void init() {
            //read from titan interface
            log.info("init begin");
//            BaseOutput<List<ProductSearchSort>> productSearchSort = titanClient.getProductService().getProductSearchSort(1);
            BaseOutput<List<ProductSearchSort>> productSearchSort = null;
            if (productSearchSort != null){
                List<ProductSearchSort> sorts = productSearchSort.getData();
                if (sorts != null){
                    log.info("titan data size: " + sorts.size());
                    for (ProductSearchSort sort : sorts) {
                        add(sort.getPid(), sort.getSearchSort());
                    }
                }
            }
            log.info("searcher data : " + manualOrders.toString());
            log.info("init end");
        }

        public void add(Long pid, Integer order) {
            this.manualOrders.put(pid, order);
        }

        public void clear() {
            manualOrders.clear();
        }
    }
}
