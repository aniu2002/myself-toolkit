package com.sparrow.collect.website.controller;

import com.sparrow.collect.website.SearchIdDef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Searcher控制器, 根据searchId返回对应的Searcher
 * Created by yaobo on 2014/6/10.
 */
public class SearcherController {

    private Log log = LogFactory.getLog(SearcherController.class);

    public static SearcherController instance = new SearcherController();

    public static SearcherController getInstance() {
        return instance;
    }

    private Map<String, Searcher> searcherProvider = new ConcurrentHashMap<>();

    private SearcherController() {
    }

    public void registerSearcher(String searchId, Searcher searcher) {
        searcherProvider.put(searchId, searcher);
        log.info("registerSearcher " + searchId + " : " + searcher.getClass().getName());
    }

    public void registSearcher() throws Exception {
        log.info("registSearcher begin");

        //========================================SIMPLE================================================
        registerSearcher(SearchIdDef.SIMPLE_SEARCHER, new SimpleSearcher());

        registerSearcher(SearchIdDef.GOODS_SEARCHER_FACADE, new GoodsSerialSearcherFacade());
        registerSearcher(SearchIdDef.GOODS_SEARCHER, new GoodsSearcher());
        registerSearcher(SearchIdDef.APP_GOODS_SEARCHER, new AppGoodsSearcher());
        registerSearcher(SearchIdDef.PC_GOODS_SEARCHER, new PcGoodsSearcher());

        registerSearcher(SearchIdDef.CATEGORY_SEARCHER, new CategoryDeduceSearcher());
        registerSearcher(SearchIdDef.ATTRIBUTE_SEARCHER, new AttributeDeduceSearcher());

        //============================================STORE==============================================
        registerSearcher(SearchIdDef.STORE_SEARCHER, new StoreSearcher());
        registerSearcher(SearchIdDef.CMS_GOODS_SEARCHER, new CmsGoodsSearcher());
        registerSearcher(SearchIdDef.CMS_STORE_SEARCHER, new CmsStoreSearcher());
        registerSearcher(SearchIdDef.APP_STORE_SEARCHER, new AppStoreSearcher());
//        registerSearcher(SearchIdDef.BUYAGNET_SEARCHER, new BuyAgentSearcher());
        registerSearcher(SearchIdDef.PC_STORE_SEARCHER, new PcStoreSearcher());

        //============================================SYSTEM_LOG===============================================
        registerSearcher(SearchIdDef.SYSLOG_SEARCHER, new BaseSearcher());

        log.info("registerSearcher end");
    }

    public Searcher getSearcher(String searchId) {
        Searcher searcher = searcherProvider.get(searchId);
        if (searcher == null){
            searcher = searcherProvider.get(SearchIdDef.SIMPLE_SEARCHER);
        }
        return searcher;
    }

}
