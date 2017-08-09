package com.sparrow.collect.store.object;

import com.sparrow.collect.store.deserializer.Deserializer;

/**
 * Created by Administrator on 2016/12/2.
 */
public interface ObjectRead {
    Object read();

    Deserializer getDeserializer();

    boolean hasNext();

    void destroy();
}
