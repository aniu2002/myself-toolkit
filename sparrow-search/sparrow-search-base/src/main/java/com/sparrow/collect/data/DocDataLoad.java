package com.sparrow.collect.data;

/**
 * Created by Administrator on 2018/8/24.
 */
public interface DocDataLoad {
    String index();

    String getString(String key);

    int getInt(String key);

    Float getFloat(String key);

    Double getDouble(String key);

    Long getLong(String key);
}
