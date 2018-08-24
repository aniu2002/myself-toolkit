package com.sparrow.collect.strategy.ext;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.strategy.IStrategy;
import com.sparrow.collect.strategy.definition.StrategyDefinition;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.util.BytesRef;

import java.util.List;

/**
 * <B>Description</B> 短语策略 <br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 *
 * @author zhanglin
 * @createTime 2014年6月19日 下午8:00:51
 */
public class PhraseStrategy implements IStrategy {

    @Override
    public void parse(StrategyDefinition strategyBean, BooleanQuery bq)
            throws Exception {

        PhraseQuery phraseQuery = new PhraseQuery();
        phraseQuery.setSlop(strategyBean.getSlop());
        Object fieldValue = strategyBean.getFieldValue();
        if (null == fieldValue)
            return;
        String valueStr = fieldValue.toString();
        String fieldName = strategyBean.getFieldName();
        IAnalyze analyze = strategyBean.getAnalyze();
        if (null == fieldName || analyze == null)
            return;
        List<String> splitStrList = analyze.split(valueStr);
        if (null != splitStrList) {
            for (String str : splitStrList) {
                phraseQuery.add(new Term(fieldName, new BytesRef(str)));
            }
            if (strategyBean.getWeight() != 0)
                phraseQuery.setBoost(strategyBean.getWeight());
            bq.add(phraseQuery, strategyBean.getOuterOccur());
        }
    }

}
