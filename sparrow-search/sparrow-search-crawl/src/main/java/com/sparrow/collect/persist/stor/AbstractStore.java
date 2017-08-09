package com.sparrow.collect.persist.stor;

import com.sparrow.collect.persist.PersistConfig;

/**
 * Created by Administrator on 2017/7/29 0029.
 */
public abstract class AbstractStore<D> implements Store<D> {
    private final PersistConfig config;

    public AbstractStore(PersistConfig config) {
        this.config = config;
    }

    protected PersistConfig getConfig() {
        return this.config;
    }

    protected abstract void doSave(Object[] r);

    public void save(D d) {
        Object[] r = this.getFormat().format(d);
        this.doSave(r);
    }

    public void initialize() {

    }

    public void close() {

    }
}
