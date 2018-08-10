package com.sparrow.collect.website;

import com.sparrow.core.config.SystemConfig;

/**
 * Created by Administrator on 2018/8/9.
 */
public class Configs {
    private static final SearchConfig P = new SearchConfig(SystemConfig.processYml("classpath:config/search-base.yml"));

    private Configs() {

    }

    public static String get(String key) {
        return P.get(key);
    }

    public static String get(String key, String defaultValue) {
        return P.get(key, defaultValue);
    }

    public static int getInt(String key) {
        return P.getInt(key);
    }

    public static int getInt(String key, int defaultValue) {
        return P.getInt(key, defaultValue);
    }

    public static boolean getBool(String key) {
        return P.getBool(key);
    }

    public static boolean has(String key) {
        return P.has(key);
    }

    public static SearchConfig getConfig(String file) {
        return new SearchConfig(SystemConfig.processYml(file));
    }

    public static SearchConfig getConfig() {
        return P;
    }
}
