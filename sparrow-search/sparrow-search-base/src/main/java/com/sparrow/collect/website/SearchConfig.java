package com.sparrow.collect.website;

import com.sparrow.core.config.SystemConfig;
import com.sparrow.core.utils.StringUtils;

import java.util.Properties;

/**
 * Created by Administrator on 2018/8/9.
 */
public class SearchConfig {
    private static final Properties P = SystemConfig.processYml("classpath:config/search-base.yml");

    private SearchConfig() {

    }

    public static String get(String key) {
        return P.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return P.getProperty(key, defaultValue);
    }

    public static int getInt(String key) {
        String str = get(key);
        return getIntValue(str, 0);
    }

    public static int getInt(String key, int defaultValue) {
        String str = get(key);
        return getIntValue(str, defaultValue);
    }

    public static boolean getBool(String key) {
        String str = get(key);
        return StringUtils.equalsIgnoreCase("true", str) || StringUtils.equalsIgnoreCase("1", str)
                || StringUtils.equalsIgnoreCase("yes", str) || StringUtils.equalsIgnoreCase("ok", str);
    }

    public static boolean has(String key) {
        return P.containsKey(key);
    }

    static int getIntValue(String value, int defaultValue) {
        if (StringUtils.isEmpty(value))
            return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

}
