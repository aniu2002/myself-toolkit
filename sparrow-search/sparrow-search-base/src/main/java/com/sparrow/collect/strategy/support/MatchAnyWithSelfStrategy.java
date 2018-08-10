package com.sparrow.collect.strategy.support;

import com.sparrow.collect.analyze.IAnalyze;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

/**
 * Created by yangtao on 2016/1/7.
 */
public class MatchAnyWithSelfStrategy extends MatchAnyStrategy {

    @Override
    public Query create(String fieldName, String fieldValue, IAnalyze analyze) {
        BooleanQuery query = (BooleanQuery) super.create(fieldName, fieldValue, analyze);
        if (query == null) {
            return null;
        }
        String keyword = com.sparrow.collect.utils.StringUtils.removeSpecialChars(fieldValue);
        //特殊判断，当字符长度<=3时，将其加入搜索
        if (keyword.length() <= 3) {
            query.add(getQuery(fieldName, keyword), getOccur());
        }
        return query;
    }
}
