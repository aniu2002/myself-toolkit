package com.sparrow.collect.store;

/**
 * Created by Administrator on 2016/12/5.
 */
public class DefaultStore implements DataStore {
    @Override
    public int checkAndSave(Object object) {
        return 0;
    }

    @Override
    public void save(Object object) {

    }

    @Override
    public void update(Object object) {

    }

    @Override
    public boolean exists(Object object) {
        return false;
    }

    @Override
    public void close() {

    }
}
