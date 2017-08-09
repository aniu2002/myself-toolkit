package com.sparrow.app.data;

import java.util.Map;

/**
 * Created by Administrator on 2016/3/15 0015.
 */
public abstract class DataImportHolder {
    private static ThreadLocal<Map<String, Object>> local = new ThreadLocal<Map<String, Object>>();

    public static void set(Map<String, Object> par) {
        if (par != null)
            local.set(par);
    }

    public static Map<String, Object> get() {
        return local.get();
    }

    public static void clear() {
        local.set(null);
        local.remove();
    }
}
