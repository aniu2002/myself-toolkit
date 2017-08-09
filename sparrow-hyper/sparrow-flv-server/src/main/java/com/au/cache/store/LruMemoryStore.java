package com.au.cache.store;

import java.util.Map;

import com.au.cache.Cache;
import com.au.cache.Element;
import com.au.cache.exceptions.CacheException;

/**
 * 
 * <p>
 * Title: LruMemoryStore
 * </p>
 * <p>
 * Description: com.au.cache.store
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: HR
 * </p>
 * 
 * @author Yzc
 * @version 1.0
 * @date 2009-11-20下午09:48:47
 */
public class LruMemoryStore implements Store {
	private Cache cache;
	protected final Store diskStore;
	protected Map map;
	protected int maximumSize;

	public LruMemoryStore(Cache cache, Store diskStore,int max) {
		this.maximumSize = max;
		this.diskStore = diskStore;
		this.cache = cache;
		this.map = new SpoolLinkedHashMap();
	}

	/**
	 * put to cache map (non-Javadoc)
	 * 
	 * @see com.eweb.cache.store.Store#put(com.eweb.cache.Element)
	 */
	public final synchronized void put(Element element) throws CacheException {
		if (element != null) {
			map.put(element.getObjectKey(), element);
		}
	}

	/**
	 * get element from key (non-Javadoc)
	 * 
	 * @see com.eweb.cache.store.Store#get(java.lang.Object)
	 */
	public final synchronized Element get(Object key) {
		return (Element) map.get(key);
	}

	/**
	 * remove element from cache (non-Javadoc)
	 * 
	 * @see com.eweb.cache.store.Store#remove(java.lang.Object)
	 */
	public final synchronized Element remove(Object key) {
		// remove single item.
		Element element = (Element) map.remove(key);
		if (element != null) {
			return element;
		} else {
			return null;
		}
	}

	/**
	 * Remove all of the elements from the store.
	 */
	public final synchronized void removeAll() throws CacheException {
		clear();
	}

	/**
	 * Clears any data structures and places it back to its state when it was
	 * first created.
	 */
	protected final void clear() {
		map.clear();
	}

	public boolean bufferFull() {
		return false;
	}

	public final boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	/**
	 * Prepares for shutdown.
	 */
	public final synchronized void dispose() {
		flush();
		// release reference to cache
		cache = null;
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.au.cache.store.Store#flush()
	 */
	public final void flush() {
		if (cache.isOverflowToDisk()) {
			spoolAllToDisk();
		}

		// should be emptied if clearOnFlush is true
		if (cache.isClearOnFlush()) {
			clear();
		}
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.au.cache.store.Store#getKeyArray()
	 */
	public final synchronized Object[] getKeyArray() {
		return map.keySet().toArray();
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.au.cache.store.Store#getSize()
	 */
	public final int getSize() {
		return map.size();
	}

	/**
	 * 
	 * <p>
	 * Title: spoolAllToDisk
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author Yzc
	 */
	protected final void spoolAllToDisk() {
		boolean clearOnFlush = cache.isClearOnFlush();
		Object[] keys = getKeyArray();
		for (Object key : keys) {
			Element element = (Element) map.get(key);
			if (element != null) {
				if (element.isSerializable()) {
					spoolToDisk(element);
					if (clearOnFlush) {
						remove(key);
					}
				}
			}
		}
	}

	/**
	 * <p>
	 * Title: isFull
	 * </p>
	 * <p>
	 * Description: memory cache is full?
	 * </p>
	 * 
	 * @return
	 * @author Yzc
	 */
	protected final boolean isFull() {
		return map.size() > maximumSize;
	}

	protected final void evict(Element element) throws CacheException {
		boolean spooled = false;
		if (cache.isOverflowToDisk()) {
			if (element.isSerializable()) {
				spoolToDisk(element);
				spooled = true;
			}
		}

		if (!spooled) {
			System.out.println(" Spooled to disk ... ");
		}
	}

	protected void spoolToDisk(Element element) {
		try {
			diskStore.put(element);
		} catch (CacheException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * <p>
	 * Title: SpoolingLinkedHashMap
	 * </p>
	 * <p>
	 * Description: com.eweb.cache.store
	 * </p>
	 * <p>
	 * Copyright: Copyright (c) 2009
	 * </p>
	 * <p>
	 * Company: HR
	 * </p>
	 * 
	 * @author Yzc
	 * @version 1.0
	 * @date 2009-11-20下午09:36:36
	 */
	public final class SpoolLinkedHashMap extends java.util.LinkedHashMap {
		private static final int INITIAL_CAPACITY = 100;
		private static final float GROWTH_FACTOR = .75F;

		public SpoolLinkedHashMap() {
			super(INITIAL_CAPACITY, GROWTH_FACTOR, true);
		}

		@Override
		protected final boolean removeEldestEntry(Map.Entry eldest) {
			Element element = (Element) eldest.getValue();
			try {
				return removeLeastRecentlyUsedElement(element);
			} catch (CacheException e) {
				e.printStackTrace();
			}
			return false;
		}

		private boolean removeLeastRecentlyUsedElement(Element element)
				throws CacheException {
			if (isFull()) {
				evict(element);
				System.out.println("Memory full, over flow to disk ! ");
				return true;
			} else {
				return false;
			}
		}
	}

}
