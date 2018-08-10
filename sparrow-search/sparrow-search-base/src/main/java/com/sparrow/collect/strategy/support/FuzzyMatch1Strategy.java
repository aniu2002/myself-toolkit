package com.sparrow.collect.strategy.support;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.strategy.IStrategy;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;

/**
 * Created by yangtao on 2015/12/29.
 */
public class FuzzyMatch1Strategy implements IStrategy {
    @Override
    public Query create(String fieldName, String fieldValue, IAnalyze analyze) {
        if(StringUtils.isBlank(fieldName)) {
            return null;
        }
        if(StringUtils.isBlank(fieldValue)) {
            return null;
        }
        //去除特殊字符
        String noSpecialCharValue = com.sparrow.collect.utils.StringUtils.removeSpecialChars(fieldValue);
        return new FuzzyQuery(new Term(fieldName, noSpecialCharValue), 1);
    }
}
