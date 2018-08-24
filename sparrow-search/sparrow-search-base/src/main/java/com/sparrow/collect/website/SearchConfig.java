package com.sparrow.collect.website;

import com.sparrow.core.config.SystemConfig;
import com.sparrow.core.utils.ClassUtils;
import com.sparrow.core.utils.StringUtils;
import org.apache.lucene.utils.StringUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Administrator on 2018/8/9.
 */
public class SearchConfig {
    private final Properties properties;

    public SearchConfig(Properties properties) {
        this.properties = properties;
    }

    public SearchConfig(String file) {
        this.properties = SystemConfig.processYml(file);
    }

    public String get(String key) {
        return this.properties.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        return this.properties.getProperty(key, defaultValue);
    }

    public int getInt(String key) {
        String str = get(key);
        return getIntValue(str, 0);
    }

    public int getInt(String key, int defaultValue) {
        String str = get(key);
        return getIntValue(str, defaultValue);
    }

    public float getFloat(String key) {
        String str = get(key);
        return getFloatValue(str, 0);
    }

    public float getFloat(String key, float defaultValue) {
        String str = get(key);
        return getFloatValue(str, defaultValue);
    }

    public boolean getBool(String key, boolean defaultVal) {
        String str = get(key);
        if (StringUtils.isEmpty(key))
            return defaultVal;
        return StringUtils.equalsIgnoreCase("true", str) || StringUtils.equalsIgnoreCase("1", str)
                || StringUtils.equalsIgnoreCase("yes", str) || StringUtils.equalsIgnoreCase("ok", str);
    }


    public boolean getBool(String key) {
        return getBool(key, false);
    }

    public <T> T getInstance(String key, Class<T> c) {
        return ClassUtils.instance(this.get(key), c);
    }

    public <T> List<T> getInstances(String key, Class<T> c) {
        String v = this.get(key);
        if (StringUtils.isEmpty(v))
            return Collections.emptyList();
        String vs[] = StringUtils.split(v, ',');
        List<T> list = new LinkedList();
        for (String s : vs) {
            list.add(ClassUtils.instance(s, c));
        }
        return list;
    }

    public boolean has(String key) {
        return this.properties.containsKey(key);
    }

    int getIntValue(String value, int defaultValue) {
        if (StringUtils.isEmpty(value))
            return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    float getFloatValue(String value, float defaultValue) {
        if (StringUtils.isEmpty(value))
            return defaultValue;
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
