package com.sparrow.collect.document.parse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 */
public class FieldStrategyFactory {
    private static Map<String, FieldParseStrategy> parsers = new ConcurrentHashMap();

    static {
        parsers.put("text", new AnalysisNoStoreFieldStrategy());
        parsers.put("text-store", new AnalysisStoreFieldStrategy());
        parsers.put("int", new IntFieldStrategy());
        parsers.put("long", new LongFieldStrategy());
        parsers.put("pinyin", new PinyinFieldStrategy());
        parsers.put("pinyin-header", new PinyinHeaderFieldStrategy());
        parsers.put("reverse", new ReverseFieldStrategy());
        parsers.put("same", new SameNotStoreFieldStrategy());
        parsers.put("same-store", new SameStoreFieldStrategy());
        parsers.put("store", new StoreOnlyFieldStrategy());
    }

    private FieldStrategyFactory() {

    }

    public static FieldParseStrategy getFieldParser(String fieldParseName) {
        return parsers.get(fieldParseName);
    }

}
