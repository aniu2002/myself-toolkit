package com.sparrow.collect.website.lucene.template;

import com.sparrow.collect.website.lucene.InstanceCache;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.FormatAnalyzerWapper;
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
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SearcherFactory {
    private static Map<String, Analyzer> analyzeMap = new ConcurrentHashMap<String, Analyzer>();
    private static Map<String, Analyzer> fieldAnalyzes = new ConcurrentHashMap<String, Analyzer>();

    static {
        analyzeMap.put("whitespace", new WhitespaceAnalyzer(Version.LUCENE_46));
        analyzeMap.put("standard", new StandardAnalyzer(Version.LUCENE_46));
        analyzeMap.put("smartIk", new IKAnalyzer(true));
        analyzeMap.put("synonymIk", new SynonymsAnalyzer(false));

        analyzeMap.put("category", new CategoryAnalyzer(CategoryDic.getInstance(), true, true));
        analyzeMap.put("region", new ExactIKAnalyzer(RegionDic.getInstance(), false, true));
        analyzeMap.put("market", new ExactIKAnalyzer(MarketDic.getInstance(), false, true));
        analyzeMap.put("comma", new CommaAnalyzer(Version.LUCENE_46));
        analyzeMap.put("default", new StandardAnalyzer(Version.LUCENE_46));
    }

    public static IndexSearcher createSearcher() throws IOException {
        return createSearcher(InstanceCache.INDEX_DIRECTORIES);
    }

    public static IndexSearcher createSearcher(String[] directories) throws IOException {
        if (directories == null || directories.length == 0) {
            throw new IOException(" the directories must be specified.");
        }

        int size = 0;
        if (directories != null) {
            size = directories.length;
        }
        IndexReader[] readers = new IndexReader[size];

        if (directories != null) {
            for (int index = 0; index < directories.length; index++) {
                readers[index] = DirectoryReader.open(FSDirectory.open(new File(directories[index])));
            }
        }
        IndexSearcher indexSearcher = new IndexSearcher(new MultiReader(readers));
        return indexSearcher;
    }

    public static Analyzer getPerFieldAnalyzer(List<String> fieldSet) {
        Map<String, Analyzer> anaMap = new HashMap<String, Analyzer>();
        for (String f : fieldSet) {
            //modify by yb: 包装为需要对数据进行格式化过滤
            String anlName = System.getProperty("analyzer.d." + f);
            Analyzer analyzer = new FormatAnalyzerWapper(analyzeMap.get(anlName));
            anaMap.put(f, analyzer);
        }
        Analyzer analyzer = new PerFieldAnalyzerWrapper(analyzeMap.get("default"), anaMap);
        return analyzer;
    }
}
