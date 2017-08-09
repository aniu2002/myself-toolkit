package com.sparrow.collect.store.deserializer;

import com.sparrow.collect.utils.JsonMapper;

import java.io.IOException;

/**
 * Created by Administrator on 2016/12/2.
 */
public class JsonDeserializer implements Deserializer {
    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            return JsonMapper.bean(bytes, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] bytes, int offset, int len, Class<?> clazz) {
        try {
            return JsonMapper.bean(bytes, offset, len, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
