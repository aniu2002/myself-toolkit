package com.sparrow.collect.store.object;

import com.sparrow.collect.store.io.DataWrite;
import com.sparrow.collect.store.serializer.Serializer;

/**
 * Created by Administrator on 2016/12/2.
 */
public class StandardObjectWrite extends AbstractObjectWrite {
    private final Serializer serializer;

    public StandardObjectWrite(DataWrite dataWrite, Serializer serializer) {
        super(dataWrite);
        this.serializer = serializer;
    }

    @Override
    public Serializer getSerializer() {
        return serializer;
    }
}
