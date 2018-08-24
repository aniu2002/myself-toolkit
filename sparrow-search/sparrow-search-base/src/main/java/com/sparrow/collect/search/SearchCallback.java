package com.sparrow.collect.search;

import org.apache.lucene.search.IndexSearcher;

/**
 * Created by yaobo on 2014/11/19.
 */
public interface SearchCallback {
    public Object doSearch(IndexSearcher searcher) throws Exception;
}
