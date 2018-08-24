package com.sparrow.collect.strategy.ext;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.strategy.IStrategy;
import com.sparrow.collect.strategy.definition.StrategyDefinition;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;

import java.util.List;

/**
 * <B>Description</B> 精确策略 <br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 *
 * @author zhanglin
 * @createTime 2014年6月19日 下午8:02:50
 */
public class PreciseStrategy implements IStrategy {

    @Override
    public void parse(StrategyDefinition strategyBean, BooleanQuery bq)
            throws Exception {
        Object fieldValue = strategyBean.getFieldValue();
        if (null == fieldValue)
            return;
        if (fieldValue.toString().trim().equalsIgnoreCase(""))
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
                TermQuery termQuery = new TermQuery(new Term(fieldName, new BytesRef(str)));
                bqTmp.add(termQuery, Occur.MUST);
            }
            if (bqTmp.getClauses().length > 0) {
                if (strategyBean.getWeight() != 0)
                    bqTmp.setBoost(strategyBean.getWeight());
                bq.add(bqTmp, strategyBean.getOuterOccur());
            }

        }
    }

}
