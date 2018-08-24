package com.sparrow.collect.strategy.ext;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.strategy.IStrategy;
import com.sparrow.collect.strategy.definition.StrategyDefinition;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.WildcardQuery;

import java.util.List;

/**
 * Created by rhdlzl on 2014/7/8.
 */
public class WildcardStrategy implements IStrategy {

    @Override
    public void parse(StrategyDefinition strategyBean, BooleanQuery bq) throws Exception {

        Object fieldValue = strategyBean.getFieldValue();
        if (null == fieldValue)
            return;
        if (StringUtils.isBlank(fieldValue.toString()))
            return;
        String valueStr = fieldValue.toString();
        String fieldName = strategyBean.getFieldName();
        IAnalyze analyze = strategyBean.getAnalyze();
        if (null == fieldName || null == analyze)
            return;
        List<String> splitStrList = analyze.split(valueStr);
        if (null != splitStrList) {
            BooleanQuery bqTmp = new BooleanQuery();
            for (String str : splitStrList) {
                WildcardQuery fuzzyQuery = new WildcardQuery(new Term(fieldName, "*" + str + "*"));
                bqTmp.add(fuzzyQuery, strategyBean.getInnerOccur());
            }
            if (bqTmp.getClauses().length > 0) {
                if (strategyBean.getWeight() != 0)
                    bqTmp.setBoost(strategyBean.getWeight());
                bq.add(bqTmp, strategyBean.getOuterOccur());
            }
        }
    }

}
