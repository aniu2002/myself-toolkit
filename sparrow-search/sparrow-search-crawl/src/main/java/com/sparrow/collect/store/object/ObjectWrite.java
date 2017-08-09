package com.sparrow.collect.store.object;

import com.sparrow.collect.store.serializer.Serializer;

/**
 * Created by Administrator on 2016/12/2.
 */
public interface ObjectWrite {
    void write(Object object);

    Serializer getSerializer();

    void destroy();
}
