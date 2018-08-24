package com.sparrow.collect.strategy.ext;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.strategy.IStrategy;
import com.sparrow.collect.strategy.definition.StrategyDefinition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.dictionary.SynonymsDic;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.util.List;

/**
 * Created by yangtao on 2015/12/17.
 */
public class MatchAllPriorityStrategy implements IStrategy {

    @Override
    public void parse(StrategyDefinition definition, BooleanQuery bq) throws Exception {
        Object _fieldValue = definition.getFieldValue();
        if (_fieldValue == null) {
            return;
        }
        String fieldValue = _fieldValue.toString();
        if (StringUtils.isBlank(fieldValue)) {
            return;
        }
        String fieldName = definition.getFieldName();
        if (StringUtils.isBlank(fieldName)) {
            return;
        }
        IAnalyze analyze = definition.getAnalyze();
        if (analyze == null) {
            return;
        }
        List<String> tokens = analyze.split(fieldValue);
        if (CollectionUtils.isEmpty(tokens)) {
            return;
        }
        String[] synonyms;
        BooleanQuery matchAllQuery = new BooleanQuery();
        BooleanQuery fieldQuery = new BooleanQuery();
        Query tokenQuery;
        for (String token : tokens) {
            synonyms = SynonymsDic.getInstance().get(token);
            //判断是否有同义词
            if (synonyms != null && synonyms.length > 0) {
                BooleanQuery synonymsQuery = new BooleanQuery();
                for (String synonym : synonyms) {
                    synonymsQuery.add(new TermQuery(new Term(fieldName, synonym)), BooleanClause.Occur.SHOULD);
                }
                tokenQuery = synonymsQuery;
            } else
                tokenQuery = new TermQuery(new Term(fieldName, token));
            matchAllQuery.add(tokenQuery, BooleanClause.Occur.MUST);
            fieldQuery.add(tokenQuery, definition.getInnerOccur());
        }
        matchAllQuery.setBoost(20.0f);
        bq.add(matchAllQuery, BooleanClause.Occur.SHOULD);
        if (definition.getWeight() != 0)
            fieldQuery.setBoost(definition.getWeight());
        //将域query进行组合
        bq.add(fieldQuery, definition.getOuterOccur());
    }
}
