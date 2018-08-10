package com.sparrow.collect.strategy;

import com.sparrow.collect.analyze.IAnalyze;
import org.apache.lucene.search.Query;

/**
 * Created by yangtao on 2015/12/22.
 */
public interface IStrategy {
    Query create(String fieldName, String fieldValue, IAnalyze analyze);
}
