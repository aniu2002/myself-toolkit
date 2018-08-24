package com.sparrow.collect.strategy.ext;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.strategy.IStrategy;
import com.sparrow.collect.strategy.definition.StrategyDefinition;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PrefixQuery;

import java.util.List;

/**
 * <B>Description</B>前缀策略 <br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 *
 * @author zhanglin
 * @createTime 2014年6月19日 下午8:00:27
 */
public class PrefixStrategy implements IStrategy {

    @Override
    public void parse(StrategyDefinition strategyBean, BooleanQuery bq)
            throws Exception {
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
                PrefixQuery pq = new PrefixQuery(new Term(fieldName, str));
                bqTmp.add(pq, strategyBean.getInnerOccur());
            }
            if (bqTmp.getClauses().length > 0) {
                if (strategyBean.getWeight() != 0)
                    bqTmp.setBoost(strategyBean.getWeight());
                bq.add(bqTmp, strategyBean.getOuterOccur());
            }
        }
    }

}
