package com.sparrow.collect.strategy.support;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.strategy.PcStrategy;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;

/**
 * Created by yangtao on 2016/1/7.
 */
public class LengthFuzzyMatch1Strategy implements PcStrategy {

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
        //特殊判断，当字符长度>=3时，才进行模糊匹配
        if(noSpecialCharValue.length() >= 3) {
            return new FuzzyQuery(new Term(fieldName, noSpecialCharValue), 1);
        }
        return null;
    }
}
