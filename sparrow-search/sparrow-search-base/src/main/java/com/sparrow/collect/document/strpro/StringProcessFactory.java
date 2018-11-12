package com.sparrow.collect.document.strpro;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class StringProcessFactory {
    static Map<String, IStringProcessor> strPros = new ConcurrentHashMap();

    static {
        strPros.put("float", new FloatStringProcessor());
        strPros.put("standard", new StandardStringProcessor());
        strPros.put("reverse", new ReverseStringProcessor());
        strPros.put("fuzzy", new FuzzyPinyinStringProcessor());
        strPros.put("pinyin", new PinyinsStringProcessor());
        strPros.put("pinyin-header", new PinyinHeadersStringProcessor());
        strPros.put("meanful", new MeanfulSameStringProcessor());
        strPros.put("same", new SameStringProcessor());
        strPros.put("split", new SplitStringProcessor());
    }

    static void addStrProcess(String key, IStringProcessor stringProcessor) {
        strPros.put(key, stringProcessor);
    }


    public static IStringProcessor getStrProcess(String spName) {
        return strPros.get(spName);
    }
}
