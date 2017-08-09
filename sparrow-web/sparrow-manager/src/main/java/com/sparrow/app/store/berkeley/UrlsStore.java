package com.sparrow.app.store.berkeley;


import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 12-11-15 Time: 上午11:27 Berkeley DB
 * Java Edition使用说明
 */
public class UrlsStore {
	protected Environment environment;
	protected Database docIDsDB;
	protected boolean resumable = true;
	protected final Object mutex = new Object();
	protected int lastDocID;

	public UrlsStore(Environment environment, String dbName, boolean resumable) {
		this.environment = environment;
		this.resumable = resumable;
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
		dbConfig.setTransactional(resumable);
		dbConfig.setDeferredWrite(!resumable);
		this.docIDsDB = environment.openDatabase(null, dbName, dbConfig);
	}

	public void put(String key, byte[] value) {
		synchronized (mutex) {
			try {
				docIDsDB.put(null, new DatabaseEntry(key.getBytes()),
						new DatabaseEntry(value));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public byte[] get(String key) {
		synchronized (mutex) {
			if (docIDsDB == null) {
				return null;
			}
			OperationStatus result;
			DatabaseEntry value = new DatabaseEntry();
			try {
				DatabaseEntry keyP = new DatabaseEntry(key.getBytes());
				result = docIDsDB.get(null, keyP, value, null);

				if (result == OperationStatus.SUCCESS
						&& value.getData().length > 0) {
					return value.getData();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public int getNewDocID(String url) {
		synchronized (mutex) {
			try {
				int docid = getDocId(url);
				if (docid > 0) {
					return docid;
				}
				lastDocID++;
				docIDsDB.put(null, new DatabaseEntry(url.getBytes()),
						new DatabaseEntry(Util.int2ByteArray(lastDocID)));
				return lastDocID;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return -1;
		}
	}

	public int getDocId(String url) {
		synchronized (mutex) {
			if (docIDsDB == null) {
				return -1;
			}
			OperationStatus result;
			DatabaseEntry value = new DatabaseEntry();
			try {
				DatabaseEntry key = new DatabaseEntry(url.getBytes());
				result = docIDsDB.get(null, key, value, null);

				if (result == OperationStatus.SUCCESS
						&& value.getData().length > 0) {
					return Util.byteArray2Int(value.getData());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return -1;
		}
	}

	public boolean isSeenBefore(String url) {
		return getDocId(url) != -1;
	}

	public int getDocCount() {
		try {
			return (int) docIDsDB.count();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void sync() {
		if (this.resumable) {
			return;
		}
		if (docIDsDB == null) {
			return;
		}
		try {
			docIDsDB.sync();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			this.environment.cleanLog();
			this.docIDsDB.close();
			this.environment.close();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	public void scan(int max) throws DatabaseException {
		synchronized (mutex) {
			int matches = 0;

			Cursor cursor = null;
			OperationStatus result;
			DatabaseEntry key = new DatabaseEntry();
			DatabaseEntry value = new DatabaseEntry();
			Transaction txn;
			if (resumable) {
				txn = environment.beginTransaction(null, null);
			} else {
				txn = null;
			}
			try {
				cursor = docIDsDB.openCursor(txn, null);
				result = cursor.getFirst(key, value, null);

				while (matches < max && result == OperationStatus.SUCCESS) {
					if (key.getData().length > 0) {
						System.out.println(new String(key.getData()));
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
		}
	}
}
