package com.sparrow.collect.store;

import com.sparrow.collect.orm.named.LazyBeanParameterSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2016/12/5.
 */
public class BatchDatabaseStore extends DatabaseStore {
    private List<Object> saveList = new ArrayList<Object>(70);
    private List<Object> updateList = new ArrayList<Object>(50);
    private final Lock lock = new ReentrantLock();

    public BatchDatabaseStore(Map<String, String> storeSet) {
        super(storeSet);
    }

    @Override
    public void save(Object object) {
        if (object == null)
            return;
        LazyBeanParameterSource[] sqlParameterSources = null;
        this.saveList.add(object);
        lock.lock();
        if (this.saveList.size() >= 50) {
            sqlParameterSources = this.session.translate(this.saveList);
            this.saveList.clear();
        }
        lock.unlock();
        if (sqlParameterSources != null)
            this.batchSave(this.session.getInsertSql(object.getClass()), sqlParameterSources);
    }

    void batchSave(String sql, LazyBeanParameterSource sqlParameterSource[]) {
        for (LazyBeanParameterSource parameterSource : sqlParameterSource)
            parameterSource.initialize();
        this.session.batchExecute(sql, sqlParameterSource);
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
            this.session.batchSave(this.saveList);
        if (!this.updateList.isEmpty())
            this.session.batchUpdate(this.updateList);
        session.close();
    }
}
