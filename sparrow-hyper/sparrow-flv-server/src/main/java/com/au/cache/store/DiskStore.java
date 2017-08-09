package com.au.cache.store;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.au.cache.Cache;
import com.au.cache.Element;
import com.au.cache.exceptions.CacheException;
import com.au.cache.tools.ByteStreamTools;
import com.au.cache.tools.FreeCounter;

public class DiskStore implements Store {
	// 1 M b
	private static final int ONE_MEGABYTE = 1048576;
	private static final int SPOOL_THREAD_INTERVAL = 500;
	private static final int PAYLOAD_SIZE = 512;
	private static final int QUARTER_OF_A_SECOND = 250;

	private Cache cache;
	private RandomAccessFile randomAccessFile;
	private File dataFile;
	private File indexFile;

	// indicates to the spoolThread that it needs to write the index on next
	// flush to disk.
	private final AtomicBoolean writeIndexFlag;
	private final Object writeIndexFlagLock;

	// if has cache elements , the thread will write index file and data file
	private Thread spoolThread;

	private ConcurrentHashMap diskElements = new ConcurrentHashMap();
	private List freeSpace = Collections.synchronizedList(new ArrayList());
	private volatile ConcurrentHashMap spool = new ConcurrentHashMap();

	private final String name;
	private final String diskPath;
	private final long maxElementsOnDisk;
	private final boolean persistent;

	private long totalSize;
	private int diskSpoolBufferSizeBytes;
	private int lastElementSize;
	private boolean active;

	private volatile boolean spoolThreadActive;

	public DiskStore(Cache cache, String diskPath) {
		this.cache = cache;
		this.name = cache.getName();
		this.diskPath = diskPath;

		persistent = cache.isOverflowToDisk();
		maxElementsOnDisk = cache.getMaxElementsOnDisk();
		diskSpoolBufferSizeBytes = cache.getDiskSpoolBufferSizeMB()
				* ONE_MEGABYTE;
		writeIndexFlag = new AtomicBoolean(false);
		writeIndexFlagLock = new Object();

		try {
			initialiseFiles();
			active = true;
			// Always start up the spool thread
			spoolThread = new SpoolThread();
			spoolThread.start();
		} catch (final Exception e) {
			e.printStackTrace();
			dispose();
		}
	}

	/**
	 * 
	 * <p>
	 * Title: initialiseFiles
	 * </p>
	 * <p>
	 * Description: initialize file
	 * </p>
	 * 
	 * @throws Exception
	 * @author Yzc
	 */
	private void initialiseFiles() throws Exception {
		// Make sure the cache directory exists
		final File diskDir = new File(diskPath);
		if (diskDir.exists() && !diskDir.isDirectory()) {
			throw new Exception("Store directory \""
					+ diskDir.getCanonicalPath()
					+ "\" exists and is not a directory.");
		}
		if (!diskDir.exists() && !diskDir.mkdirs()) {
			throw new Exception("Could not create cache directory \""
					+ diskDir.getCanonicalPath() + "\".");
		}
		dataFile = new File(diskDir, getDataFileName());
		indexFile = new File(diskDir, getIndexFileName());
		deleteIndexIfNoData();
		if (persistent) {
			if (!readIndex()) {
				dataFile.delete();
			}
		} else {
			dataFile.delete();
			indexFile = null;
		}

		// Open the data file as random access. The dataFile is created if
		// necessary.
		randomAccessFile = new RandomAccessFile(dataFile, "rw");
	}

	/**
	 * 
	 * <p>
	 * Title: deleteIndexIfNoData
	 * </p>
	 * <p>
	 * Description: delete index file
	 * </p>
	 * 
	 * @author Yzc
	 */
	private void deleteIndexIfNoData() {
		boolean dataFileExists = dataFile.exists();
		boolean indexFileExists = indexFile.exists();
		if (!dataFileExists && indexFileExists) {
			indexFile.delete();
		}
	}

	/**
	 * 
	 * <p>
	 * Title: readIndex
	 * </p>
	 * <p>
	 * Description: read index file ,get element key and index detail
	 * </p>
	 * 
	 * @return
	 * @throws IOException
	 * @author Yzc
	 */
	private synchronized boolean readIndex() throws IOException {
		ObjectInputStream objectInputStream = null;
		FileInputStream fin = null;
		boolean success = false;
		if (indexFile.exists()) {
			try {
				fin = new FileInputStream(indexFile);
				objectInputStream = new ObjectInputStream(fin);

				Map diskElementsMap = (Map) objectInputStream.readObject();
				if (diskElementsMap instanceof ConcurrentHashMap) {
					diskElements = (ConcurrentHashMap) diskElementsMap;
				} else {
					diskElements = new ConcurrentHashMap(diskElementsMap);
				}
				freeSpace = (List) objectInputStream.readObject();
				success = true;
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (objectInputStream != null) {
						objectInputStream.close();
					}
					if (fin != null) {
						fin.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (!success) {
					createNewIndexFile();
				}
			}
		} else {
			createNewIndexFile();
		}
		// Return the success flag
		return success;

	}

	/**
	 * 
	 * <p>
	 * Title: createNewIndexFile
	 * </p>
	 * <p>
	 * Description: create new index file
	 * </p>
	 * 
	 * @throws IOException
	 * @author Yzc
	 */
	private void createNewIndexFile() throws IOException {
		if (indexFile.exists()) {
			if (!indexFile.delete()) {
				throw new IOException("Index file " + indexFile
						+ " could not deleted.");
			}
		}
		if (!indexFile.createNewFile()) {
			throw new IOException("Index file " + indexFile
					+ " could not created.");
		}
	}

	public final String getDataFileName() {
		String safeName = name.replace('/', '_');
		return safeName + ".data";
	}

	public final String getIndexFileName() {
		return name + ".index";
	}

	public final String getDataFilePath() {
		return diskPath;
	}

	public boolean bufferFull() {
		long estimatedSpoolSize = spool.size() * lastElementSize;
		boolean backedUp = estimatedSpoolSize > diskSpoolBufferSizeBytes;
		return backedUp;

	}

	public final boolean containsKey(Object key) {
		return diskElements.containsKey(key) || spool.containsKey(key);
	}

	public final void dispose() {

		if (!active) {
			return;
		}

		// Close the cache
		try {

			// set the write index flag. Ignored if not persistent
			flush();

			// tell the spool thread to spool down. It will loop one last time
			// if flush was caled.
			spoolThreadActive = false;

			// interrupt the spoolAndExpiryThread if it is waiting to run again
			// to get it to run now
			// Then wait for it to write
			spoolThread.interrupt();
			if (spoolThread != null) {
				spoolThread.join();
			}

			// Clear in-memory data structures
			spool.clear();
			diskElements.clear();
			freeSpace.clear();
			synchronized (randomAccessFile) {
				if (randomAccessFile != null) {
					randomAccessFile.close();
				}
			}
			deleteFiles();
			if (!persistent) {
				dataFile.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			active = false;
			randomAccessFile = null;

			// release reference to cache
			cache = null;
		}
	}

	/**
	 * 
	 * <p>
	 * Title: deleteFiles
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author Yzc
	 */
	protected void deleteFiles() {
		if (dataFile != null && dataFile.exists()) {
			dataFile.delete();
		}
		if (indexFile != null && indexFile.exists()) {
			indexFile.delete();
		}
		File dataDirectory = new File(diskPath);
		if (dataDirectory != null && dataDirectory.exists()) {
			if (dataDirectory.delete()) {
			}
		}

	}

	/**
	 * notify thread to flush to disk (non-Javadoc)
	 * 
	 * @see com.au.cache.store.Store#flush()
	 */
	public final void flush() {
		if (persistent) {
			synchronized (writeIndexFlagLock) {
				writeIndexFlag.set(true);
			}
		}
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.au.cache.store.Store#get(java.lang.Object)
	 */
	public final synchronized Element get(final Object key) {
		try {
			checkActive();

			// Check in the spool. Remove if present
			Element element;
			element = (Element) spool.remove(key);
			if (element != null) {
				return element;
			}

			// Check if the element is on disk
			final DiskElement diskElement = (DiskElement) diskElements.get(key);
			if (diskElement == null) {
				// Not on disk
				return null;
			}

			element = loadElementFromDiskElement(diskElement);
			return element;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * <p>
	 * Title: loadElementFromDiskElement
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param diskElement
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @author Yzc
	 */
	private Element loadElementFromDiskElement(DiskElement diskElement)
			throws IOException, ClassNotFoundException {
		Element element;
		final byte[] buffer;
		synchronized (randomAccessFile) {
			// Load the element
			randomAccessFile.seek(diskElement.position);
			buffer = new byte[diskElement.payloadSize];
			randomAccessFile.readFully(buffer);
		}
		final ByteArrayInputStream instr = new ByteArrayInputStream(buffer);

		final ObjectInputStream objstr = new ObjectInputStream(instr) {
			/**
			 * Overridden because of: Bug 1324221 ehcache DiskStore has issues
			 * when used in Tomcat
			 */
			@Override
			protected Class resolveClass(ObjectStreamClass clazz)
					throws ClassNotFoundException, IOException {
				try {
					ClassLoader classLoader = Thread.currentThread()
							.getContextClassLoader();
					return Class.forName(clazz.getName(), false, classLoader);
				} catch (ClassNotFoundException e) {
					// Use the default as a fallback because of
					// bug 1517565 - DiskStore loadElementFromDiskElement
					return super.resolveClass(clazz);
				}
			}
		};
		element = (Element) objstr.readObject();
		objstr.close();
		return element;
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.au.cache.store.Store#getKeyArray()
	 */
	public final synchronized Object[] getKeyArray() {
		Set elementKeySet;
		elementKeySet = diskElements.keySet();
		Set spoolKeySet;
		spoolKeySet = spool.keySet();
		Set allKeysSet = new HashSet(elementKeySet.size() + spoolKeySet.size());
		allKeysSet.addAll(elementKeySet);
		allKeysSet.addAll(spoolKeySet);
		return allKeysSet.toArray();
	}

	/**
	 * 
	 * (non-Javadoc) get element size
	 * 
	 * @see com.au.cache.store.Store#getSize()
	 */
	public final synchronized int getSize() {
		try {
			checkActive();
			int spoolSize = spool.size();
			int diskSize = diskElements.size();
			return spoolSize + diskSize;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.au.cache.store.Store#put(com.au.cache.Element)
	 */
	public final void put(final Element element) {
		try {
			checkActive();
			// Spool the element
			if (spoolThread.isAlive()) {
				System.out.println(" Disk store put element : "+element.getObjectKey());
				spool.put(element.getObjectKey(), element);
			} else {
				spool.clear();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.au.cache.store.Store#remove(java.lang.Object)
	 */
	public final synchronized Element remove(final Object key) {
		Element element;
		try {
			checkActive();

			// Remove the entry from the spool
			element = (Element) spool.remove(key);

			// Remove the entry from the file. Could be in both places.
			final DiskElement diskElement = (DiskElement) diskElements
					.remove(key);
			if (diskElement != null) {
				element = loadElementFromDiskElement(diskElement);
				freeBlock(diskElement);
			}
		} catch (Exception exception) {
			String message = name
					+ "Cache: Could not remove disk store entry for key " + key
					+ ". Error was " + exception.getMessage();
			System.out.println(message);
			return null;
		}
		return element;
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.au.cache.store.Store#removeAll()
	 */
	public final synchronized void removeAll() {
		try {
			checkActive();

			// Ditch all the elements, and truncate the file
			spool = new ConcurrentHashMap();
			diskElements = new ConcurrentHashMap();
			freeSpace = Collections.synchronizedList(new ArrayList());
			totalSize = 0;
			synchronized (randomAccessFile) {
				randomAccessFile.setLength(0);
			}
			if (persistent) {
				indexFile.delete();
				indexFile.createNewFile();
			}
		} catch (Exception e) {
			// Clean up
			e.printStackTrace();
			dispose();
		}
	}

	/**
	 * <p>
	 * Title: checkActive
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @throws CacheException
	 * @author Yzc
	 */
	private void checkActive() throws CacheException {
		if (!active) {
			throw new CacheException(name
					+ " Cache: The Disk store is not active.");
		}
	}

	/**
	 * 
	 * <p>
	 * Title: spoolAndExpiryThreadMain
	 * </p>
	 * <p>
	 * Description: run thread to check
	 * </p>
	 * 
	 * @author Yzc
	 */
	private void spoolThreadMain() {
		while (spoolThreadActive || writeIndexFlag.get()) {
			try {
				// don't wait when we want to flush
				if (!writeIndexFlag.get()) {
					Thread.sleep(SPOOL_THREAD_INTERVAL);
				}
			} catch (InterruptedException e) {
				// expected on shutdown
			}
			safeFlushSpool();
			if (!spoolThreadActive) {
				return;
			}
		}
	}

	/**
	 * 
	 * <p>
	 * Title: safeFlushSpool
	 * </p>
	 * <p>
	 * Description: 检测是否需要持久化到文件
	 * </p>
	 * 
	 * @author Yzc
	 */
	private void safeFlushSpool() {
		synchronized (writeIndexFlagLock) {
			if (spool != null && (spool.size() != 0 || writeIndexFlag.get())) {
				// Write elements to disk
				System.out.println(" write element");
				try {
					flushSpool();
					if (persistent && writeIndexFlag.get()) {
						try {
							writeIndex();
						} finally {
							writeIndexFlag.set(false);
						}
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 
	 * <p>
	 * Title: swapSpoolReference
	 * </p>
	 * <p>
	 * Description: copy spool
	 * </p>
	 * 
	 * @return
	 * @author Yzc
	 */
	private Map swapSpoolReference() {
		Map copyOfSpool = null;
		copyOfSpool = spool;
		spool = new ConcurrentHashMap();
		return copyOfSpool;
	}

	private synchronized void flushSpool() throws IOException {
		if (spool.size() == 0) {
			return;
		}

		Map copyOfSpool = swapSpoolReference();

		// does not guarantee insertion order
		Iterator valuesIterator = copyOfSpool.values().iterator();
		while (valuesIterator.hasNext()) {
			writeOrReplaceEntry(valuesIterator.next());
			valuesIterator.remove();
		}
	}

	/**
	 * 
	 * <p>
	 * Title: writeOrReplaceEntry
	 * </p>
	 * <p>
	 * Description: 写如或者替换释放的节点
	 * </p>
	 * 
	 * @param object
	 * @throws IOException
	 * @author Yzc
	 */
	private void writeOrReplaceEntry(Object object) throws IOException {
		Element element = (Element) object;
		if (element == null) {
			return;
		}
		final Serializable key = (Serializable) element.getObjectKey();
		removeOldEntryIfAny(key);
		// 当存储的element大于了指定的最大数，则清除  使用频率最小的，释放空间
		if (maxElementsOnDisk > 0 && diskElements.size() >= maxElementsOnDisk) {
			evictLfuDiskElement();
		}
		writeElement(element, key);
	}

	/**
	 * 
	 * <p>
	 * Title: removeOldEntryIfAny
	 * </p>
	 * <p>
	 * Description: delete exists elements in index
	 * </p>
	 * 
	 * @param key
	 * @author Yzc
	 */
	private void removeOldEntryIfAny(Serializable key) {
		final DiskElement oldBlock;
		oldBlock = (DiskElement) diskElements.remove(key);
		if (oldBlock != null) {
			freeBlock(oldBlock);
		}
	}

	/**
	 * 
	 * <p>
	 * Title: freeBlock
	 * </p>
	 * <p>
	 * Description: free space of
	 * </p>
	 * 
	 * @param diskElement
	 * @author Yzc
	 */
	private void freeBlock(final DiskElement diskElement) {
		totalSize -= diskElement.payloadSize;
		diskElement.payloadSize = 0;
		// reset Element meta data
		diskElement.key = null;
		freeSpace.add(diskElement);
	}

	private void writeElement(Element element, Serializable key)
			throws IOException {
		try {
			// 序列化对象字节流到内存
			ByteArrayOutputStream buffer = serializeElement(element);
			if (buffer == null) {
				return;
			}

			int bufferLength = buffer.size();
			try {
				DiskElement diskElement = checkForFreeBlock(bufferLength);

				// Write the record
				synchronized (randomAccessFile) {
					randomAccessFile.seek(diskElement.position);
					randomAccessFile.write(buffer.toByteArray(), 0,
							bufferLength);
				}
				buffer = null;

				// Add to index, update stats
				diskElement.payloadSize = bufferLength;
				diskElement.key = key;
				totalSize += bufferLength;
				lastElementSize = bufferLength;
				diskElements.put(key, diskElement);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			// Catch any exception that occurs during serialization
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * <p>
	 * Title: checkForFreeBlock
	 * </p>
	 * <p>
	 * Description: 检测空闲的资源块
	 * </p>
	 * 
	 * @param bufferLength
	 * @return
	 * @throws IOException
	 * @author Yzc
	 */
	private DiskElement checkForFreeBlock(int bufferLength) throws IOException {
		// 从空闲块中寻找已经释放的 element
		DiskElement diskElement = findFreeBlock(bufferLength);
		if (diskElement == null) {
			// 未找到则新建立一个,磁盘存储块
			diskElement = new DiskElement();
			synchronized (randomAccessFile) {
				diskElement.position = randomAccessFile.length();
			}
			diskElement.blockSize = bufferLength;
		}
		return diskElement;
	}

	/**
	 * 
	 * <p>
	 * Title: findFreeBlock
	 * </p>
	 * <p>
	 * Description: 从free的space里查询与 长度匹配的element
	 * </p>
	 * 
	 * @param length
	 * @return
	 * @author Yzc
	 */
	private DiskElement findFreeBlock(final int length) {
		for (int i = 0; i < freeSpace.size(); i++) {
			final DiskElement element = (DiskElement) freeSpace.get(i);
			if (element.blockSize >= length) {
				freeSpace.remove(i);
				return element;
			}
		}
		return null;
	}

	/**
	 * 
	 * <p>
	 * Title: serializeElement
	 * </p>
	 * <p>
	 * Description: 序列化对象得到一个 byte stream
	 * </p>
	 * 
	 * @param element
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @author Yzc
	 */
	private ByteArrayOutputStream serializeElement(Element element)
			throws IOException, InterruptedException {
		for (int retryCount = 0; retryCount < 2; retryCount++) {
			try {
				return ByteStreamTools.serialize(element,
						estimatedPayloadSize());
			} catch (ConcurrentModificationException e) {
				// wait for the other thread(s) to finish
				Thread.sleep(QUARTER_OF_A_SECOND);
			}
		}
		return null;
	}

	/**
	 * 
	 * <p>
	 * Title: estimatedPayloadSize
	 * </p>
	 * <p>
	 * Description: 给出一个估计值
	 * </p>
	 * 
	 * @return
	 * @author Yzc
	 */
	private int estimatedPayloadSize() {
		int size = 0;
		try {
			// 取一个平均值
			size = (int) (totalSize / diskElements.size());
		} catch (Exception e) {
			//
		}
		if (size <= 0) {
			size = PAYLOAD_SIZE;
		}
		return size;
	}

	/**
	 * 
	 * <p>
	 * Title: evictLfuDiskElement
	 * </p>
	 * <p>
	 * Description: 使用频繁度最少的
	 * </p>
	 * 
	 * @author Yzc
	 */
	private void evictLfuDiskElement() {
		synchronized (diskElements) {
			DiskElement diskElement = findRelativelyUnused();
			diskElements.remove(diskElement.key);
			freeBlock(diskElement);
		}
	}

	/**
	 * 
	 * <p>
	 * Title: findRelativelyUnused
	 * </p>
	 * <p>
	 * Description: 寻找一个相对未使用的
	 * </p>
	 * 
	 * @return
	 * @author Yzc
	 */
	private DiskElement findRelativelyUnused() {
		// 生成随机的 选择索引
		DiskElement[] elements = sampleElements(diskElements);
		return leastHit(elements, null);
	}

	/**
	 * 
	 * <p>
	 * Title: leastHit
	 * </p>
	 * <p>
	 * Description: 使用频繁度最少的
	 * </p>
	 * 
	 * @param sampledElements
	 * @param justAdded
	 * @return
	 * @author Yzc
	 */
	public static DiskElement leastHit(DiskElement[] sampledElements,
			DiskElement justAdded) {
		// edge condition when Memory Store configured to size 0
		if (sampledElements.length == 1 && justAdded != null) {
			return justAdded;
		}
		DiskElement lowestElement = null;
		for (DiskElement diskElement : sampledElements) {
			if (lowestElement == null) {
				if (!diskElement.equals(justAdded)) {
					lowestElement = diskElement;
				}
			} else {
				if (diskElement.getHitCount() < lowestElement.getHitCount()
						&& !diskElement.equals(justAdded)) {
					lowestElement = diskElement;
				}
			}
		}
		return lowestElement;
	}

	private DiskElement[] sampleElements(Map map) {
		int[] offsets = FreeCounter.generateRandomSample(map.size());
		DiskElement[] elements = new DiskElement[offsets.length];
		Iterator iterator = map.values().iterator();
		for (int i = 0; i < offsets.length; i++) {
			for (int j = 0; j < offsets[i]; j++) {
				iterator.next();
			}
			elements[i] = (DiskElement) iterator.next();
		}
		return elements;
	}
    // 写索引
	private synchronized void writeIndex() throws IOException {
		FileOutputStream fout = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			fout = new FileOutputStream(indexFile);
			objectOutputStream = new ObjectOutputStream(fout);
			objectOutputStream.writeObject(diskElements);
			objectOutputStream.writeObject(freeSpace);
		} finally {
			if (objectOutputStream != null) {
				objectOutputStream.close();
			}
			if (fout != null) {
				fout.close();
			}
		}
	}

	/**
	 * 
	 * @version 1.0
	 * @date 2009-11-20下午10:49:01
	 */
	private static final class DiskElement implements Serializable {
		private static final long serialVersionUID = -717310932566592289L;
		private long position;
		private int payloadSize;
		private int blockSize;
		private int hitCount;
		private Object key;

		public Object getObjectKey() {
			return key;
		}

		public int getHitCount() {
			return hitCount;
		}
	}

	/**
	 * @author Yzc
	 * @version 1.0
	 * @date 2009-11-20下午11:07:38
	 */
	private final class SpoolThread extends Thread {

		public SpoolThread() {
			super("Store " + name + " Spool Thread");
			setDaemon(true);
			setPriority(Thread.NORM_PRIORITY);
			spoolThreadActive = true;
		}

		/**
		 * RemoteDebugger thread method.
		 */
		@Override
		public final void run() {
			spoolThreadMain();
		}
	}
}
