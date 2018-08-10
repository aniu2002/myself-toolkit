package com.sparrow.collect.strategy;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

import java.util.List;

/**
 * Created by yangtao on 2015/12/22.
 */
public class StrategyFactory {

    public static Query create(String keyword, List<FieldStrategy> fieldStrategies) {
        BooleanQuery query = new BooleanQuery();
        for(FieldStrategy fieldStrategy : fieldStrategies) {
            BooleanQuery fieldQuery = new BooleanQuery();
            fieldQuery.setBoost(fieldStrategy.getFieldBoost());
            List<StrategyBean> strategyBeans = fieldStrategy.getStrategies();
            for(StrategyBean strategyBean : strategyBeans) {
                Query subQuery = strategyBean.getStrategy().create(fieldStrategy.getFieldName(), keyword, strategyBean.getAnalyze());
                if(subQuery == null) {
                    continue;
                }
                subQuery.setBoost(strategyBean.getBoost());
                fieldQuery.add(subQuery, strategyBean.getOccur());
            }
            if(!fieldQuery.clauses().isEmpty()) {
                query.add(fieldQuery, fieldStrategy.getOccur());
            }
        }
        if(query.clauses().isEmpty()) {
            return new MatchAllDocsQuery();
        }
        return query;
    }
}
