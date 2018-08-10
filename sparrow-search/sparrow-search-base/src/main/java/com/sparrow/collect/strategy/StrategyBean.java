package com.sparrow.collect.strategy;

import com.sparrow.collect.analyze.IAnalyze;
import org.apache.lucene.search.BooleanClause;

/**
 * Created by yangtao on 2015/12/22.
 */
public class StrategyBean {
    //query创建策略
    private IStrategy strategy;
    //分词策略
    private IAnalyze analyze;
    //query boost
    private float boost = 1.0f;
    //query logic
    private BooleanClause.Occur occur = BooleanClause.Occur.SHOULD;

    public IStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(IStrategy strategy) {
        this.strategy = strategy;
    }

    public IAnalyze getAnalyze() {
        return analyze;
    }

    public void setAnalyze(IAnalyze analyze) {
        this.analyze = analyze;
    }

    public float getBoost() {
        return boost;
    }

    public void setBoost(float boost) {
        this.boost = boost;
    }

    public BooleanClause.Occur getOccur() {
        return occur;
    }

    public void setOccur(BooleanClause.Occur occur) {
        this.occur = occur;
    }
}
