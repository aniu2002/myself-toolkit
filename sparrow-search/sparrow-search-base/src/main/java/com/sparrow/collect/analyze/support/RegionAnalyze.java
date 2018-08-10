package com.sparrow.collect.analyze.support;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.exactik.ExactIKAnalyzer;
import org.apache.lucene.dictionary.RegionDic;

/**
 * Created by yangtao on 2016/2/4.
 */
public class RegionAnalyze extends Analyze {
    private Analyzer analyzer;

    public RegionAnalyze() {
        this.analyzer = new ExactIKAnalyzer(RegionDic.getInstance(), true);
    }

    @Override
    public Analyzer getAnalyzer() {
        return this.analyzer;
    }
}
