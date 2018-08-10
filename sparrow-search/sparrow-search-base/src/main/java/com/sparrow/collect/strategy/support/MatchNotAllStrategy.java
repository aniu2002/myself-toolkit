package com.sparrow.collect.strategy.support;

import com.sparrow.collect.analyze.IAnalyze;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangtao on 2015/12/23.
 */
public class MatchNotAllStrategy extends BooleanStrategy {

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
        tokens = handleFieldValueByLength(fieldValue, tokens);
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
        int size = query.clauses().size();
        if(size <= 2) {
            //分词个数小于2个的不组合
            return query;
        }
        int minMatch = size <=5 ? 2 : 3;
        query.setMinimumNumberShouldMatch(minMatch);
        return query;
    }

    public List<String> handleFieldValueByLength(String fieldValue, List<String> tokens) {
        if(CollectionUtils.isEmpty(tokens)) {
            tokens = new ArrayList<>(1);
        }
        String _fieldValue = com.sparrow.collect.utils.StringUtils.removeSpecialChars(fieldValue);
        //特殊判断，当字符长度<=3时，将其加入搜索
        if(_fieldValue.length() <= 3 && !tokens.contains(_fieldValue)) {
            tokens.add(_fieldValue);
        }
        return tokens;
    }

    @Override
    public Query getQuery(String fieldName, String token) {
        return new TermQuery(new Term(fieldName, token));
    }

    public BooleanClause.Occur getOccur() {
        return BooleanClause.Occur.SHOULD;
    }
}
