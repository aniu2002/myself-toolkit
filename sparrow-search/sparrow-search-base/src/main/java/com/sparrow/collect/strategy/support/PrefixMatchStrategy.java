package com.sparrow.collect.strategy.support;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.strategy.IStrategy;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;

/**
 * Created by yangtao on 2015/12/22.
 */
public class PrefixMatchStrategy implements IStrategy {

    @Override
    public Query create(String fieldName, String fieldValue, IAnalyze analyze) {
        if (StringUtils.isBlank(fieldName)) {
            return null;
        }
        if (StringUtils.isBlank(fieldValue)) {
            return null;
        }
        //去除特殊字符
        String noSpecialCharValue = com.sparrow.collect.utils.StringUtils.removeSpecialChars(fieldValue);
        return new PrefixQuery(new Term(fieldName, noSpecialCharValue));
    }
}
