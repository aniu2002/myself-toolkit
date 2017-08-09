package com.sparrow.collect.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Project Name: sparrow-egg
 * Package Name: com.sparrow.collect.store
 * Author : YZC
 * Date: 2016/12/13
 * Time: 16:37
 */
public class SqlTemplateStore extends DatabaseStore {
    private List<Object> saveList = new ArrayList<Object>(100);
    private List<Object> saveListBak = new ArrayList<Object>(100);
    private List<Object[]> values = new ArrayList<Object[]>(100);
    private List<Object> updateList = new ArrayList<Object>(50);
    private final Lock lock = new ReentrantLock();
    private final String sql;

    public SqlTemplateStore(Map<String, String> storeSet, String sql) {
        super(storeSet);
        this.sql = sql;
    }

    @Override
    public void save(Object object) {
        if (object == null)
            return;
        boolean swap = false;
        lock.lock();
        this.saveList.add(object);
        if (this.saveList.size() >= 100) {
            List<Object> tmp = this.saveList;
            this.saveList = this.saveListBak;
            this.saveListBak = tmp;
            swap = true;
        }
        lock.unlock();
        if (swap) {
            this.copyTo(this.saveListBak, this.values);
            this.saveListBak.clear();
            this.batchSave(this.sql, this.values);
        }
    }

    List<Object[]> copy(List<Object> saveList) {
        List<Object[]> values = new ArrayList<Object[]>();
        for (Object object : saveList)
            values.add((Object[]) object);
        return values;
    }

    void copyTo(List<Object> saveList, List<Object[]> values) {
        values.clear();
        for (Object object : saveList)
            values.add((Object[]) object);
    }

    void batchSave(String sql, List<Object[]> values) {
        this.session.batchExecute(sql, values);
    }

    @Override
    public void update(Object object) {
        this.updateList.add(object);
        if (this.updateList.size() >= 50) {
            this.session.batchUpdate(this.updateList);
            this.updateList.clear();
        }
    }

    @Override
    public boolean exists(Object object) {
        return this.saveList.contains(object) || super.exists(object);
    }

    @Override
    public void close() {
        if (!this.saveList.isEmpty())
            this.batchSave(this.sql, this.copy(this.saveList));
        if (!this.updateList.isEmpty())
            this.session.batchUpdate(this.updateList);
        session.close();
    }
}
