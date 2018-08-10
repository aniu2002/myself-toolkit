package com.sparrow.collect.website.service;

import com.dili.dd.searcher.bsearch.common.space.IndexSpace;
import org.apache.lucene.search.IndexSearcher;

import java.io.IOException;

/**
 * 包装indexSpace和indexSearcher, 保证indexSearcher能正确释放
 * Created by yaobo on 2014/11/19.
 */
public class NRTSearcher {

    private IndexSpace indexSpace;

    private IndexSearcher indexSearcher;

    public NRTSearcher(IndexSpace indexSpace, IndexSearcher indexSearcher) {
        this.indexSpace = indexSpace;
        this.indexSearcher = indexSearcher;
    }

    public IndexSearcher get() {
        return indexSearcher;
    }

    public void release() {
        try {
            indexSpace.release(indexSearcher);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object search(SearchCallback callback) throws Exception{
        try {
            return callback.doSearch(get());
        } catch (Exception e) {
             throw e;
        } finally {
            release();
        }
    }
}
