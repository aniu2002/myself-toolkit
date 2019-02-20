package com.sparrow.collect.query;

import org.apache.lucene.search.BooleanQuery;

/**
 * Created by Administrator on 2019/2/20 0020.
 */
public interface QueryStrategy {
    void parse(BooleanQuery bq);
}
