package com.sparrow.collect.store.object;

import com.sparrow.collect.store.deserializer.Deserializer;
import com.sparrow.collect.store.io.AbstractDataRead;

/**
 * Created by Administrator on 2016/12/2.
 */
public class StandardObjectRead extends AbstractObjectRead {
    private final Deserializer deserializer;

    public StandardObjectRead(AbstractDataRead dataRead, Deserializer deserializer) {
        super(dataRead);
        this.deserializer = deserializer;
    }

    @Override
    public Deserializer getDeserializer() {
        return deserializer;
    }
}
