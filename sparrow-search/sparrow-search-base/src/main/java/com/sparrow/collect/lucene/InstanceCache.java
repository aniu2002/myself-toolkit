package com.sparrow.collect.lucene;

import com.sparrow.collect.lucene.extractor.ResultExtractor;
import com.sparrow.collect.lucene.extractor.SearchResultExtractor;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InstanceCache {
    private static final Map<Class<?>, Object> cache = new ConcurrentHashMap<Class<?>, Object>();
    public static final String[] INDEX_DIRECTORIES;

    static {
        File file = new File("temp/indexer/database");
        File file1 = new File("temp/indexer/file");
        File file2 = new File("temp/indexer/web");
        if (!file.exists())
            file.mkdirs();
        if (!file1.exists())
            file1.mkdirs();
        if (!file2.exists())
            file2.mkdirs();
        INDEX_DIRECTORIES = new String[]{file.getAbsolutePath(),
                file1.getAbsolutePath(), file2.getAbsolutePath()};
        cache.put(org.apache.lucene.analysis.Analyzer.class,
                new IKAnalyzer(true));
        cache.put(ResultExtractor.class,
                new SearchResultExtractor());
    }

    public static <T> T getInstance(Class<T> claz) {
        if (claz == null)
            return null;
        return (T) cache.get(claz);
    }

    public static void registInstance(Object object) {
        if (object == null)
            return;
        cache.put(object.getClass(), object);
    }

    public static void registInstance(Class claz, Object object) {
        if (object == null)
            return;
        cache.put(claz, object);
    }
}
