package com.sparrow.collect.strategy.ext;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.strategy.IStrategy;
import com.sparrow.collect.strategy.definition.StrategyDefinition;
import com.sparrow.collect.utils.CombineWord;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.dictionary.SynonymsDic;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.util.List;

/**
 * Created by yangtao on 2015/12/19.
 */
public class ShopNameStrategy implements IStrategy {
    private Analyzer analyzer = new IKAnalyzer(true);

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

        //完整匹配(分词)
        BooleanQuery _matchAllQuery = new BooleanQuery();
        _matchAllQuery.setBoost((float) Math.pow(2, 8));
        fieldQuery.add(_matchAllQuery, BooleanClause.Occur.SHOULD);
        //任意匹配
        BooleanQuery _matchAnyQuery = new BooleanQuery();
        _matchAnyQuery.setBoost((float) Math.pow(2, 2));
        fieldQuery.add(_matchAnyQuery, BooleanClause.Occur.SHOULD);
        String[] synonyms;
        Query tokenQuery;
        for (String token : tokens) {
            synonyms = SynonymsDic.getInstance().get(token);
            //判断是否有同义词
            if (synonyms != null && synonyms.length > 0) {
                BooleanQuery synonymsQuery = new BooleanQuery();
                for (String synonym : synonyms)
                    synonymsQuery.add(new TermQuery(new Term(fieldName, synonym)), BooleanClause.Occur.SHOULD);
                tokenQuery = synonymsQuery;
            } else {
                tokenQuery = new TermQuery(new Term(fieldName, token));
            }
            _matchAnyQuery.add(tokenQuery, BooleanClause.Occur.SHOULD);
            _matchAllQuery.add(tokenQuery, BooleanClause.Occur.MUST);
//            _matchAnyQuery.add(new TermQuery(new Term(fieldName, token)), BooleanClause.Occur.SHOULD);
        }
        if (tokens.size() > 2) {
            //组合匹配
            List<String[]> combineTokens = CombineWord.select(tokens.toArray(new String[tokens.size()]), tokens.size() > 1 ? tokens.size() - 1 : 1);
            BooleanQuery combineQuery = new BooleanQuery();
            combineQuery.setBoost((float) Math.pow(2, 4));
            for (String[] combine : combineTokens) {
                BooleanQuery combineOneQuery = new BooleanQuery();
                for (String token : combine) {
                    combineOneQuery.add(new TermQuery(new Term(fieldName, token)), BooleanClause.Occur.MUST);
                }
                combineQuery.add(combineOneQuery, BooleanClause.Occur.SHOULD);
            }
            fieldQuery.add(combineQuery, BooleanClause.Occur.SHOULD);
        }
        if (strategyBean.getWeight() != 0) {
            fieldQuery.setBoost(strategyBean.getWeight());
        }
        //将域query进行组合
        bq.add(fieldQuery, strategyBean.getOuterOccur());
    }
}
