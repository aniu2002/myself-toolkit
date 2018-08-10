package com.sparrow.collect.strategy.support;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 * Created by yangtao on 2015/12/22.
 */
public class MatchAnyStrategy extends BooleanStrategy {

    @Override
    public Query getQuery(String fieldName, String token) {
        return new TermQuery(new Term(fieldName, token));
    }

    @Override
    public BooleanClause.Occur getOccur() {
        return BooleanClause.Occur.SHOULD;
    }
}
