package com.sparrow.collect.index.config;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SynonymsAnalyzer;
import org.apache.lucene.analysis.category.CategoryAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.exactik.CommaAnalyzer;
import org.apache.lucene.analysis.exactik.ExactIKAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.dictionary.CategoryDic;
import org.apache.lucene.dictionary.MarketDic;
import org.apache.lucene.dictionary.RegionDic;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2019/2/25 0025.
 */
public class DefaultAnalyzers {
    private static final Map<String, Analyzer> ANALYZER_MAP = new ConcurrentHashMap<String, Analyzer>();
    private static final Analyzer DEFAULT_ANALYZER;

    static {
        ANALYZER_MAP.put("whitespace", new WhitespaceAnalyzer(Version.LUCENE_46));
        ANALYZER_MAP.put("standard", new StandardAnalyzer(Version.LUCENE_46));
        ANALYZER_MAP.put("smartIk", new IKAnalyzer(true));
        ANALYZER_MAP.put("synonymIk", new SynonymsAnalyzer(false));

        ANALYZER_MAP.put("category", new CategoryAnalyzer(CategoryDic.getInstance(), true, true));
        ANALYZER_MAP.put("region", new ExactIKAnalyzer(RegionDic.getInstance(), false, true));
        ANALYZER_MAP.put("market", new ExactIKAnalyzer(MarketDic.getInstance(), false, true));
        ANALYZER_MAP.put("comma", new CommaAnalyzer(Version.LUCENE_46));
        ANALYZER_MAP.put("default", new IKAnalyzer());
        DEFAULT_ANALYZER = new IKAnalyzer();
    }

    private DefaultAnalyzers() {

    }

    public static Analyzer getAnalyzer(String name) {
        return ANALYZER_MAP.get(name);
    }

    public static Analyzer getDefaultAnalyzer() {
        return DEFAULT_ANALYZER;
    }

    public static Analyzer getPerFieldAnalyzer(List<FieldSetting> fieldSettings) {
        Map<String, Analyzer> anaMap = new HashMap(5);
        fieldSettings.forEach(k -> anaMap.put(k.getName(), k.getAnalyzer()));
        return new PerFieldAnalyzerWrapper(DEFAULT_ANALYZER, anaMap);
    }
}
