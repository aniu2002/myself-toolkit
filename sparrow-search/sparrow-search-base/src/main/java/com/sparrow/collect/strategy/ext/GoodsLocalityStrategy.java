package com.sparrow.collect.strategy.ext;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.strategy.IStrategy;
import com.sparrow.collect.strategy.definition.StrategyDefinition;
import com.sparrow.collect.utils.CombineWord;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.utils.StringUtil;

import java.util.List;

/**
 * Created by yangtao on 2015/12/20.
 */
public class GoodsLocalityStrategy implements IStrategy {

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
        BooleanQuery fieldQuery = new BooleanQuery();
        String noSpecialCharValue = StringUtil.removeSpecialChars(fieldValue);
        //完整匹配(不分词)
        Query matchAllQuery = new TermQuery(new Term(fieldName, noSpecialCharValue));
        matchAllQuery.setBoost((float) Math.pow(2, 3));
        fieldQuery.add(matchAllQuery, BooleanClause.Occur.SHOULD);
        //完整匹配(分词)
        BooleanQuery _matchAllQuery = new BooleanQuery();
        _matchAllQuery.setBoost((float) Math.pow(2, 3));
        fieldQuery.add(_matchAllQuery, BooleanClause.Occur.SHOULD);
        Query tokenQuery;
        for (String token : tokens) {
            tokenQuery = new TermQuery(new Term(fieldName, token));
            _matchAllQuery.add(tokenQuery, BooleanClause.Occur.MUST);
        }
        int combineSize = tokens.size() > 1 ? tokens.size() - 1 : 1;
        //组合匹配
        List<String[]> combineTokens = CombineWord.select(tokens.toArray(new String[tokens.size()]), combineSize);
        BooleanQuery combineQuery = new BooleanQuery();
        combineQuery.setBoost((float) Math.pow(2, 4));
        for (String[] combine : combineTokens) {
            BooleanQuery combineOneQuery = new BooleanQuery();
            for (String token : combine)
                combineOneQuery.add(new TermQuery(new Term(fieldName, token)), BooleanClause.Occur.MUST);
            combineQuery.add(combineOneQuery, BooleanClause.Occur.SHOULD);
        }
        fieldQuery.add(combineQuery, BooleanClause.Occur.SHOULD);
        if (strategyBean.getWeight() != 0) {
            fieldQuery.setBoost(strategyBean.getWeight());
        }
        //将域query进行组合
        bq.add(fieldQuery, strategyBean.getOuterOccur());
    }
}
