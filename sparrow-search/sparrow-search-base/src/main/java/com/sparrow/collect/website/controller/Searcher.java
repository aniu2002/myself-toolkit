package com.sparrow.collect.website.controller;

import com.sparrow.collect.data.result.SearchResult;
import com.sparrow.collect.data.search.SearchBean;

/**
 * 搜索接口, 对外提供一致的搜索服务
 * Created by yaobo on 2014/6/10.
 */
public interface Searcher {

      SearchResult search(SearchBean searchBean) throws SearchException;

}
