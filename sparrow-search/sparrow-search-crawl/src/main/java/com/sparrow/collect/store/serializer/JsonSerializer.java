package com.sparrow.collect.store.serializer;

import com.sparrow.collect.utils.JsonMapper;

import java.io.IOException;

/**
 * Created by Administrator on 2016/12/2.
 */
public class JsonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        try {
            return JsonMapper.bytes(object);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
