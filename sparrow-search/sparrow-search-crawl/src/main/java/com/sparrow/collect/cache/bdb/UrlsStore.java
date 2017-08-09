package com.sparrow.collect.cache.bdb;


import com.sleepycat.bind.tuple.ByteBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.*;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 12-11-15 Time: 上午11:27 Berkeley DB
 * Java Edition使用说明
 */
public class UrlsStore {
    protected Environment environment;
    protected Database urlBDB;
    protected boolean resume = true;
    protected DatabaseEntry DEFAULT_TAG = new DatabaseEntry();
    protected TupleBinding<Byte> binding = new ByteBinding();

    public UrlsStore(Environment environment, String dbName, boolean resume) {
        this.environment = environment;
        this.resume = resume;
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setTransactional(resume);
        dbConfig.setDeferredWrite(!resume);
        //允许存储多个值
        //dbConfig.setSortedDuplicates(true);
        this.urlBDB = environment.openDatabase(null, dbName, dbConfig);
        this.binding.objectToEntry((byte) 1, DEFAULT_TAG);
    }

    public void put(String key) {
        try {
            urlBDB.put(null, new DatabaseEntry(key.getBytes()), DEFAULT_TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean exists(String key) {
        if (urlBDB == null)
            return false;
        OperationStatus result;
        DatabaseEntry value = new DatabaseEntry();
        try {
            DatabaseEntry keyP = new DatabaseEntry(key.getBytes());
            result = urlBDB.get(null, keyP, value, LockMode.DEFAULT);
            if (result == OperationStatus.SUCCESS)
                return true;
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Byte get(String key) {
        if (urlBDB == null)
            return null;
        OperationStatus result;
        DatabaseEntry value = new DatabaseEntry();
        try {
            DatabaseEntry keyP = new DatabaseEntry(key.getBytes());
            result = urlBDB.get(null, keyP, value, null);
            if (result == OperationStatus.SUCCESS && value.getData().length > 0)
                return binding.entryToObject(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getDocCount() {
        try {
            return (int) urlBDB.count();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void sync() {
        if (this.resume) {
            return;
        }
        if (urlBDB == null) {
            return;
        }
        try {
            urlBDB.sync();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            this.urlBDB.close();
            //this.environment.cleanLog();
            this.environment.close();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }
}
