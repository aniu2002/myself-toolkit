package com.sparrow.weixin.common;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public abstract class JsonMapper {
    public static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.configure(
                DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static <T> T readJson(String json, Class<T> clz) {
        try {
            return mapper.readValue(json, clz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T readJson(byte[] bytes, Class<T> clz) throws IOException {
        return mapper.readValue(bytes, clz);
    }

    public static String toJsonString(Object object) throws IOException {
        return mapper.writeValueAsString(object);
    }
}
