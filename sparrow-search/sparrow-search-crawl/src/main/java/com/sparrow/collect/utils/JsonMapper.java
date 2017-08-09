package com.sparrow.collect.utils;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;

/**
 * Created by Administrator on 2016/12/1.
 */
public abstract class JsonMapper {
    static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.configure(
                DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public final static <T> T bean(String json, Class<T> clz) {
        try {
            return mapper.readValue(json, clz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public final static <T> T bean(byte[] bytes, Class<T> clz) throws IOException {
        return mapper.readValue(bytes, clz);
    }

    public final static <T> T bean(String text, TypeReference<T> typeReference) throws IOException {
        return mapper.readValue(text, typeReference);
    }

    public final static <T> T bean(byte[] bytes, int offset, int len, Class<T> clz) throws IOException {
        return mapper.readValue(bytes, offset, len, clz);
    }

    public final static String string(Object object) throws IOException {
        return mapper.writeValueAsString(object);
    }

    public final static byte[] bytes(Object object) throws IOException {
        return mapper.writeValueAsBytes(object);
    }
}