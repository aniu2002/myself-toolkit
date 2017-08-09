package com.sparrow.app.store;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.*;
import com.sparrow.app.store.berkeley.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-4-9 Time: 下午7:48 To change this
 * template use File | Settings | File Templates.
 */
public class DataStore<T> {
    protected Database taskDB = null;
    protected Environment env;
    protected boolean transEnable;
    protected TupleBinding<T> binding;

    protected final Object mutex = new Object();

    DataStore(Environment env, String dbName, boolean transEnable, TupleBinding<T> binding)
            throws DatabaseException {
        this.env = env;
        this.transEnable = transEnable;
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setTransactional(transEnable);
        dbConfig.setReadOnly(false);
        // 缓冲写库
        dbConfig.setDeferredWrite(true);
        this.taskDB = env.openDatabase(null, dbName, dbConfig);
        this.binding = binding;
    }

    public void scan(RecordScan<T> scan) throws DatabaseException {
        synchronized (mutex) {
            Cursor cursor = null;
            OperationStatus result;
            DatabaseEntry key = new DatabaseEntry();
            DatabaseEntry value = new DatabaseEntry();
            Transaction txn;
            if (transEnable) {
                txn = env.beginTransaction(null, null);
            } else {
                txn = null;
            }
            try {
                cursor = taskDB.openCursor(txn, null);
                result = cursor.getFirst(key, value, null);
                while (result == OperationStatus.SUCCESS) {
                    if (value.getData().length > 0) {
                        scan.scan(binding.entryToObject(value));
                    }
                    result = cursor.getNext(key, value, null);
                }
            } catch (DatabaseException e) {
                if (txn != null) {
                    txn.abort();
                    txn = null;
                }
                throw e;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (txn != null) {
                    txn.commit();
                }
            }
        }
    }

    public List<T> list() throws DatabaseException {
        synchronized (mutex) {
            Cursor cursor = null;
            OperationStatus result;
            DatabaseEntry key = new DatabaseEntry();
            DatabaseEntry value = new DatabaseEntry();
            Transaction txn;
            if (transEnable) {
                txn = env.beginTransaction(null, null);
            } else {
                txn = null;
            }
            try {
                cursor = taskDB.openCursor(txn, null);
                result = cursor.getFirst(key, value, null);
                List<T> list = new ArrayList<T>();
                while (result == OperationStatus.SUCCESS) {
                    if (value.getData().length > 0) {
                        list.add(binding.entryToObject(value));
                    }
                    result = cursor.getNext(key, value, null);
                }
                return list;
            } catch (DatabaseException e) {
                if (txn != null) {
                    txn.abort();
                    txn = null;
                }
                throw e;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (txn != null) {
                    txn.commit();
                }
            }
        }
    }


    public T get(Long id) throws DatabaseException {
        DatabaseEntry theKey = new DatabaseEntry(Util.long2ByteArray(id));
        DatabaseEntry value = new DatabaseEntry();
        OperationStatus st = taskDB.get(null, theKey, value, LockMode.DEFAULT);
        if (st == OperationStatus.SUCCESS) {
            T task = binding.entryToObject(value);
            return task;
        } else
            return null;
    }

    public List<T> get(int max) throws DatabaseException {
        synchronized (mutex) {
            int matches = 0;
            List<T> results = new ArrayList<T>(max);

            Cursor cursor = null;
            OperationStatus result;
            DatabaseEntry key = new DatabaseEntry();
            DatabaseEntry value = new DatabaseEntry();
            Transaction txn;
            if (transEnable) {
                txn = env.beginTransaction(null, null);
            } else {
                txn = null;
            }
            try {
                cursor = taskDB.openCursor(txn, null);
                result = cursor.getFirst(key, value, null);

                while (matches < max && result == OperationStatus.SUCCESS) {
                    if (value.getData().length > 0) {
                        results.add(binding.entryToObject(value));
                        matches++;
                    }
                    result = cursor.getNext(key, value, null);
                }
            } catch (DatabaseException e) {
                if (txn != null) {
                    txn.abort();
                    txn = null;
                }
                throw e;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (txn != null) {
                    txn.commit();
                }
            }
            return results;
        }
    }

    public boolean remove(Long id) {
        synchronized (mutex) {
            try {
                DatabaseEntry key = new DatabaseEntry(Util.long2ByteArray(id));
                //  TransactionConfig txConfig = new TransactionConfig();
                // txConfig.setSerializableIsolation(true);

                try {
                    OperationStatus res = taskDB.delete(null, key);
                    //txn.commit();
                    if (res == OperationStatus.SUCCESS)
                        return true;
                    else if (res == OperationStatus.KEYEMPTY) {
                        System.out.println("没有从数据库" + this.taskDB + "中找到:" + key + "。无法删除");
                        return false;
                    } else {
                        System.out.println("删除操作失败，由于" + res.toString());
                    }
                } catch (DatabaseException e) {

                    throw e;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void delete(int count) throws DatabaseException {
        synchronized (mutex) {
            int matches = 0;
            Cursor cursor = null;
            OperationStatus result;
            DatabaseEntry key = new DatabaseEntry();
            DatabaseEntry value = new DatabaseEntry();
            Transaction txn;
            if (transEnable) {
                txn = env.beginTransaction(null, null);
            } else {
                txn = null;
            }
            try {
                cursor = taskDB.openCursor(txn, null);
                result = cursor.getFirst(key, value, null);

                while (matches < count && result == OperationStatus.SUCCESS) {
                    cursor.delete();
                    matches++;
                    result = cursor.getNext(key, value, null);
                }
            } catch (DatabaseException e) {
                if (txn != null) {
                    txn.abort();
                    txn = null;
                }
                throw e;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (txn != null) {
                    txn.commit();
                }
            }
        }
    }

    public void put(Long id, T data) throws DatabaseException {
        byte[] keyData = Util.long2ByteArray(id);
        DatabaseEntry value = new DatabaseEntry();
        binding.objectToEntry(data, value);
        Transaction txn;
        if (transEnable) {
            txn = env.beginTransaction(null, null);
        } else {
            txn = null;
        }
        taskDB.put(txn, new DatabaseEntry(keyData), value);
        if (transEnable) {
            if (txn != null) {
                txn.commit();
            }
        }
    }

    public long getLength() {
        try {
            return taskDB.count();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void sync() {
        if (transEnable) {
            return;
        }
        if (taskDB == null) {
            return;
        }
        try {
            taskDB.sync();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            taskDB.close();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }
}