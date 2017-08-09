package com.sparrow.collect.store;

/**
 * Created by Administrator on 2016/12/5.
 */
public interface DataStore {
    int checkAndSave(Object object);
    void save(Object object);
    void update(Object object);
    boolean exists(Object object);
    void close();
}
