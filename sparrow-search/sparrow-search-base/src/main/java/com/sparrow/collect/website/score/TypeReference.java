package com.sparrow.collect.website.score;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by yaobo on 2014/8/18.
 */
public class TypeReference<T> {
    private final Type type;

    protected TypeReference(){
        Type superClass = getClass().getGenericSuperclass();

        type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }
}
