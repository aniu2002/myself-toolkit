package com.sparrow.collect.analyze.support;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.exactik.ExactIKAnalyzer;
import org.apache.lucene.dictionary.ik.IKDict;

/**
 * Created by yangtao on 2016/1/26.
 */
public class ExactIKAnalyze extends Analyze {
    private Analyzer analyzer;

    public ExactIKAnalyze(IKDict ikDict) {
        this.analyzer = new ExactIKAnalyzer(ikDict);
    }

    public ExactIKAnalyze(IKDict ikDict, boolean useSmart) {
        this.analyzer = new ExactIKAnalyzer(ikDict, useSmart);
    }

    @Override
    public Analyzer getAnalyzer() {
        return this.analyzer;
    }
}
