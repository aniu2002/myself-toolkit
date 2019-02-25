package com.sparrow.collect.index.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/2/21 0021.
 */
public enum FieldType {
    NONE(null), STRING("string"), LONG("long"), INT("int"), FLOAT("float"),
    DOUBLE("double"), KEYWORD("keyword"), TEXT("text");
    final String value;

    static final Map<String, FieldType> maps = new HashMap(FieldType.values().length);

    static {
        for (FieldType fieldType : FieldType.values()) {
            maps.put(fieldType.value, fieldType);
        }
    }

    FieldType(String value) {
        this.value = value;
    }

    public static FieldType of(String value) {
        return maps.getOrDefault(value, NONE);
    }
}
