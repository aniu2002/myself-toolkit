package com.sparrow.collect.strategy.support;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;

/**
 * Created by yangtao on 2015/12/22.
 */
public class MatchAllWithSynonymsStrategy extends BooleanStrategy {

    @Override
    public Query getQuery(String fieldName, String token) {
        return new SynonymsStrategy().create(fieldName, token, null);
    }

    @Override
    public BooleanClause.Occur getOccur() {
        return BooleanClause.Occur.MUST;
    }
}
