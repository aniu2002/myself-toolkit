package com.sparrow.collect.data.search;

import com.dili.dd.searcher.basesearch.search.beans.search.support.SimpleSearchBeanFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yaobo on 2014/12/15.
 */
public class SearchBeanFactoryController {
    private Log log = LogFactory.getLog(SearchBeanFactoryController.class);

    public static SearchBeanFactoryController instance = new SearchBeanFactoryController();

    public static SearchBeanFactoryController getInstance() {
        return instance;
    }

    private Map<String, SearchBeanFactory> searchBeanFactoryProvider = new ConcurrentHashMap<>();

    private SearchBeanFactory defaultFactory = new SimpleSearchBeanFactory();

    private SearchBeanFactoryController() {
    }

    public void registerSearchBeanFactory() throws Exception {
        log.info("register SearchBeanFactory begin");

        register("$base-search", new SearchBeanFactory());

        log.info("register SearchBeanFactory end");
    }

    public void register(String searchId, SearchBeanFactory beanFactory) {
        searchBeanFactoryProvider.put(searchId, beanFactory);
        log.info("register SearchBeanFactory " + searchId + " : " + beanFactory.getClass().getName());
    }

    public SearchBeanFactory getSearchBeanFactory(String searchId) {
        SearchBeanFactory searchBeanFactory = searchBeanFactoryProvider.get(searchId);
        if (searchBeanFactory == null) {
            return defaultFactory;
        }
        return searchBeanFactory;
    }
}
