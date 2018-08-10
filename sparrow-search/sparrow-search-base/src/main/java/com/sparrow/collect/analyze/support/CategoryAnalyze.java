package com.sparrow.collect.analyze.support;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.category.CategoryAnalyzer;
import org.apache.lucene.dictionary.CategoryDic;

/**
 * Created by yangtao on 2016/2/4.
 */
public class CategoryAnalyze extends Analyze {
    private Analyzer analyzer;

    public CategoryAnalyze() {
        this.analyzer = new CategoryAnalyzer(CategoryDic.getInstance(), true);
    }

    @Override
    public Analyzer getAnalyzer() {
        return this.analyzer;
    }
}
