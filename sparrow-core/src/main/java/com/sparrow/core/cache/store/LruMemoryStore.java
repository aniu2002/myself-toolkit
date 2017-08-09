package com.sparrow.core.cache.store;

import java.util.Map;

import com.sparrow.core.cache.Cache;
import com.sparrow.core.cache.Element;
import com.sparrow.core.cache.exceptions.CacheException;


/**
 * @author Yzc
 * @version 1.0
 */
public class LruMemoryStore implements Store {
	private Cache cache;
	protected final Store diskStore;
	protected Map<Object, Object> map;
	protected int maximumSize;

	public LruMemoryStore(Cache cache, Store diskStore, int max) {
		this.maximumSize = max;
		this.diskStore = diskStore;
		this.cache = cache;
		this.map = new SpoolLinkedHashMap();
	}

	public final synchronized void put(Element element) throws CacheException {
		if (element != null) {
			map.put(element.getObjectKey(), element);
		}
	}

	public final synchronized Element get(Object key) {
		return (Element) map.get(key);
	}

	public final synchronized Element remove(Object key) {
		// remove single item.
		Element element = (Element) map.remove(key);
		if (element != null) {
			return element;
		} else {
			return null;
		}
	}

	public final synchronized void removeAll() throws CacheException {
		clear();
	}

	protected final void clear() {
		map.clear();
	}

	public boolean bufferFull() {
		return false;
	}

	public final boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public final synchronized void dispose() {
		flush();
		// release reference to cache
		cache = null;
	}

	public final void flush() {
		if (cache.isOverflowToDisk()) {
			spoolAllToDisk();
		}
		// should be emptied if clearOnFlush is true
		if (cache.isClearOnFlush()) {
			clear();
		}
	}

	public final synchronized Object[] getKeyArray() {
		return map.keySet().toArray();
	}

	public final int getSize() {
		return map.size();
	}

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

	public final class SpoolLinkedHashMap extends
			java.util.LinkedHashMap<Object, Object> {
		private static final long serialVersionUID = 3607215904973190050L;
		private static final int INITIAL_CAPACITY = 100;
		private static final float GROWTH_FACTOR = .75F;

		public SpoolLinkedHashMap() {
			super(INITIAL_CAPACITY, GROWTH_FACTOR, true);
		}

		@Override
		protected final boolean removeEldestEntry(
				Map.Entry<Object, Object> eldest) {
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
