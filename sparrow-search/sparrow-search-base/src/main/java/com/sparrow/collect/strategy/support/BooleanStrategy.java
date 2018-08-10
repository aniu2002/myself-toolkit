package com.sparrow.collect.strategy.support;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.strategy.IStrategy;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import java.util.List;

/**
 * Created by yangtao on 2015/12/23.
 */
public abstract class BooleanStrategy implements IStrategy {

    @Override
    public Query create(String fieldName, String fieldValue, IAnalyze analyze) {
        if(StringUtils.isBlank(fieldName)) {
            return null;
        }
        if(StringUtils.isBlank(fieldValue)) {
            return null;
        }
        assert analyze != null;
        //分词
        List<String> tokens = analyze.split(fieldValue);
        if(CollectionUtils.isEmpty(tokens)) {
            return null;
        }
        BooleanQuery query = new BooleanQuery();
        Query tokenQuery = null;
        for(String token : tokens) {
            tokenQuery = getQuery(fieldName, token);
            if(tokenQuery != null) {
                query.add(tokenQuery, getOccur());
            }
        }
        if(query.clauses().isEmpty()) {
            return null;
        }
        return query;
    }

    public abstract Query getQuery(String fieldName, String token);

    public abstract BooleanClause.Occur getOccur();
}
