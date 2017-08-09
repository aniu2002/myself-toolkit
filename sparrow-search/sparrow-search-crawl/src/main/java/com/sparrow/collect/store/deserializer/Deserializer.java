package com.sparrow.collect.store.deserializer;

/**
 * Created by Administrator on 2016/12/2.
 */
public interface Deserializer {
    Object deserialize(byte[] bytes, Class<?> clazz);

    Object deserialize(byte[] bytes, int offset, int len, Class<?> clazz);
}
