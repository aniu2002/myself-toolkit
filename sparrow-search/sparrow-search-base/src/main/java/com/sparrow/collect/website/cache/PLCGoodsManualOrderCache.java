package com.sparrow.collect.website.cache;

import com.dili.dd.searcher.basesearch.common.bean.MSBean;
import com.dili.dd.searcher.basesearch.common.bean.MSModel;
import com.dili.dd.searcher.basesearch.common.sort.ExternalDataSource;
import com.diligrp.titan.sdk.TitanClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by yaobo on 2014/8/29.
 */
public class PLCGoodsManualOrderCache implements ExternalDataSource<String, Integer>, MSModel {

    private Log log = LogFactory.getLog(PLCGoodsManualOrderCache.class);

    private MSBean<GoodsManualOrderCacheSupport> cacheMSBean = new MSBean<>();

    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    private TitanClient titanClient;

    public PLCGoodsManualOrderCache() {
        GoodsManualOrderCacheSupport master = new GoodsManualOrderCacheSupport();
        GoodsManualOrderCacheSupport slaver = new GoodsManualOrderCacheSupport();
        cacheMSBean.setMaster(master);
        cacheMSBean.setSlave(slaver);

//        Configuration config = new Configuration();
//        config.addResource(PLCGoodsManualOrderCache.class.getClassLoader().getResource("searcher-env.xml"));
//        titanClient = new TitanClient(config.get("searcher.datainterface.titan.accesskey"), config.get("searcher.datainterface.titan.secretkey"));

    }

    public PLCGoodsManualOrderCache(GoodsManualOrderCacheSupport master, GoodsManualOrderCacheSupport slaver) {
        cacheMSBean.setMaster(master);
        cacheMSBean.setSlave(slaver);
    }

    @Override
    public Integer getValue(String aLong) {
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

    public void add(String pid, Integer order) {
        try {
            rwLock.writeLock().lock();
            cacheMSBean.getMaster().add(pid, order);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    class GoodsManualOrderCacheSupport implements ExternalDataSource<String, Integer> {
        private Map<String, Integer> manualOrders = new ConcurrentHashMap();


        @Override
        public Integer getValue(String aLong) {
            return manualOrders.get(aLong);
        }

        public void init() {
            //read from titan interface
            log.info("init begin");
//            BaseOutput<List<ProductSearchSort>> productSearchSort = titanClient.getProductService().getProductSearchSort();
//            if (productSearchSort != null){
//                List<ProductSearchSort> sorts = productSearchSort.getData();
//                if (sorts != null){
//                    log.info("titan data size: " + sorts.size());
//                    for (ProductSearchSort sort : sorts) {
//                        add(sort.getPid().toString(), sort.getSearchSort());
//                    }
//                }
//            }
            log.info("searcher data : " + manualOrders.toString());
            log.info("init end");
        }

        public void add(String pid, Integer order) {
            this.manualOrders.put(pid, order);
        }

        public void clear() {
            manualOrders.clear();
        }
    }
}
