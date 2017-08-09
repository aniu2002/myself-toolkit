package com.sparrow.app.store.berkeley;

import java.util.ArrayList;
import java.util.List;


import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-4-9
 * Time: 下午7:48
 * To change this template use File | Settings | File Templates.
 */
public class FinalPageStore {
    protected Database urlsDB = null;
    protected Environment env;
    protected boolean resumable;
    protected TupleBinding binding;

    protected final Object mutex = new Object();

    public FinalPageStore(Environment env, String dbName, boolean resumable) throws DatabaseException {
        this.env = env;
        this.resumable = resumable;
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setTransactional(resumable);
        dbConfig.setDeferredWrite(!resumable);
        urlsDB = env.openDatabase(null, dbName, dbConfig);
        binding = new TupleBinding();
    }

    public void scan(PageRecordScan scan) throws DatabaseException {
        synchronized (mutex) {
            Cursor cursor = null;
            OperationStatus result;
            DatabaseEntry key = new DatabaseEntry();
            DatabaseEntry value = new DatabaseEntry();
            Transaction txn;
            if (resumable) {
                txn = env.beginTransaction(null, null);
            } else {
                txn = null;
            }
            try {
                cursor = urlsDB.openCursor(txn, null);
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

    public List<UrlData> get(int max) throws DatabaseException {
        synchronized (mutex) {
            int matches = 0;
            List<UrlData> results = new ArrayList<UrlData>(max);

            Cursor cursor = null;
            OperationStatus result;
            DatabaseEntry key = new DatabaseEntry();
            DatabaseEntry value = new DatabaseEntry();
            Transaction txn;
            if (resumable) {
                txn = env.beginTransaction(null, null);
            } else {
                txn = null;
            }
            try {
                cursor = urlsDB.openCursor(txn, null);
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

    public boolean removeURL(UrlData webUrl) {
        synchronized (mutex) {
            try {
                DatabaseEntry key = new DatabaseEntry(Util.int2ByteArray(webUrl.getDocId()));
                Transaction txn = env.beginTransaction(null, null);
                try {
                    urlsDB.delete(txn, key);
                } catch (DatabaseException e) {
                    if (txn != null) {
                        txn.abort();
                        txn = null;
                    }
                    throw e;
                } finally {
                    if (txn != null) {
                        txn.commit();
                    }
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
            if (resumable) {
                txn = env.beginTransaction(null, null);
            } else {
                txn = null;
            }
            try {
                cursor = urlsDB.openCursor(txn, null);
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


    public void put(UrlData url) throws DatabaseException {
        byte[] keyData = Util.int2ByteArray(url.getDocId());
        DatabaseEntry value = new DatabaseEntry();
        binding.objectToEntry(url, value);
        Transaction txn;
        if (resumable) {
            txn = env.beginTransaction(null, null);
        } else {
            txn = null;
        }
        urlsDB.put(txn, new DatabaseEntry(keyData), value);
        if (resumable) {
            if (txn != null) {
                txn.commit();
            }
        }
    }

    public long getLength() {
        try {
            return urlsDB.count();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void sync() {
        if (resumable) {
            return;
        }
        if (urlsDB == null) {
            return;
        }
        try {
            urlsDB.sync();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            urlsDB.close();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }
}