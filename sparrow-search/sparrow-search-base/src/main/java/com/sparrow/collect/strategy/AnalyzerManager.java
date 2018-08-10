package com.sparrow.collect.strategy;


import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.analyze.support.*;
import org.apache.lucene.dictionary.NongFengDic;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangtao on 2015/12/22.
 * 分词器管理
 */
public class AnalyzerManager {
    Map<String, IAnalyze> analyzers;

    private static AnalyzerManager instance = new AnalyzerManager();

    private AnalyzerManager(){}

    public void init() {
        if(analyzers == null) {
            analyzers = new HashMap();
        }
        //analyzers.put("ansj", new AnsjAnalyze());
        analyzers.put("default", new DefaultAnalyze());
        analyzers.put("ik", new IKAnalyze());
        analyzers.put("smartIk", new SmartIKAnalyze());
        analyzers.put("standard", new StandAnalyze());
        analyzers.put("whitespace", new WhiteSpaceAnalyze());
        analyzers.put("exactIk", new ExactIKAnalyze(NongFengDic.getInstance()));
        analyzers.put("category", new CategoryAnalyze());
        analyzers.put("region", new RegionAnalyze());
        analyzers.put("market", new MarketAnalyze());
    }

    public static AnalyzerManager getInstance() {
        return instance;
    }

    public IAnalyze getAnalyzer(String key) {
        return analyzers.get(key);
    }

    public String toString() {
        if(analyzers == null || analyzers.isEmpty()) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        for(String name : analyzers.keySet()) {
            buffer.append(name).append(", ");
        }
        return buffer.substring(0, buffer.length()-2);
    }
}
