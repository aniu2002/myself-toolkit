package com.sparrow.common.source;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yuanzc on 2015/8/18.
 */
public abstract class SourceManager {
    static final Map<String, SourceHandler> map = new ConcurrentHashMap<String, SourceHandler>();

    public static void regSourceHandler(String key, SourceHandler sourceHandler) {
        map.put(key, sourceHandler);
    }

    public static SourceHandler getSourceHandler(String key) {
        return map.get(key);
    }
}
