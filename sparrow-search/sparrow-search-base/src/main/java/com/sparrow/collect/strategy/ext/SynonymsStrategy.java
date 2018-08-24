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
 * Created by yangtao on 2015/12/10.
 */
public class SynonymsStrategy implements IStrategy {

    @Override
    public void parse(StrategyDefinition strategyBean, BooleanQuery bq) throws Exception {
        Object _fieldValue = strategyBean.getFieldValue();
        if (_fieldValue == null) {
            return;
        }
        String fieldValue = _fieldValue.toString();
        if (StringUtils.isBlank(fieldValue)) {
            return;
        }
        String fieldName = strategyBean.getFieldName();
        if (StringUtils.isBlank(fieldName)) {
            return;
        }
        IAnalyze analyze = strategyBean.getAnalyze();
        if (analyze == null) {
            return;
        }
        List<String> tokens = analyze.split(fieldValue);
        if (CollectionUtils.isEmpty(tokens)) {
            return;
        }
        String[] synonyms;
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
            } else {
                tokenQuery = new TermQuery(new Term(fieldName, token));
            }
            fieldQuery.add(tokenQuery, strategyBean.getInnerOccur());
        }
        if (strategyBean.getWeight() != 0) {
            fieldQuery.setBoost(strategyBean.getWeight());
        }
        //将域query进行组合
        bq.add(fieldQuery, strategyBean.getOuterOccur());
    }
}
