package com.sparrow.collect.analyze.support;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.exactik.ExactIKAnalyzer;
import org.apache.lucene.dictionary.MarketDic;

/**
 * Created by yangtao on 2016/2/4.
 */
public class MarketAnalyze extends Analyze {
    private Analyzer analyzer;

    public MarketAnalyze() {
        this.analyzer = new ExactIKAnalyzer(MarketDic.getInstance(), true);
    }

    @Override
    public Analyzer getAnalyzer() {
        return this.analyzer;
    }
}
