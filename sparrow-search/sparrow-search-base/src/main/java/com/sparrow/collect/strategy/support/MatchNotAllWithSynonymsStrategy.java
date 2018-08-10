package com.sparrow.collect.strategy.support;

import com.sparrow.collect.analyze.IAnalyze;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

/**
 * Created by yangtao on 2015/12/23.
 */
public class MatchNotAllWithSynonymsStrategy extends BooleanStrategy {

    @Override
    public Query create(String fieldName, String fieldValue, IAnalyze analyze) {
        BooleanQuery query = (BooleanQuery)super.create(fieldName, fieldValue, analyze);
        if(query == null) {
            return null;
        }
        int size = query.clauses().size();
        int minMatch = (size - 1) / 2 + 1;
        query.setMinimumNumberShouldMatch(minMatch);
        return query;
    }

    @Override
    public Query getQuery(String fieldName, String token) {
        return new SynonymsStrategy().create(fieldName, token, null);
    }

    @Override
    public BooleanClause.Occur getOccur() {
        return BooleanClause.Occur.SHOULD;
    }
}
