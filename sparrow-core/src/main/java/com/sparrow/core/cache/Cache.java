package com.sparrow.core.cache;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sparrow.core.cache.exceptions.CacheException;
import com.sparrow.core.cache.store.DiskStore;
import com.sparrow.core.cache.store.LruMemoryStore;
import com.sparrow.core.cache.store.Store;

public class Cache {
    private Store diskStore;
    private Store memoryStore;

    private String name;
    private String diskStorePath;

    private int maxElementsInMemory;
    private boolean overflowToDisk;
    private boolean clearOnFlush;
    private boolean active = false;
    private int diskSpoolBufferSize;

    public Cache(String name, int maxElementsInMemory, boolean overflowToDisk,
                 int diskSpoolBufferSize) {
        this(name, maxElementsInMemory, overflowToDisk, diskSpoolBufferSize,
                System.getProperty("app.home", System.getProperty("user.home")) + "/.cache2");
    }

    public Cache(String name, int maxElementsInMemory, boolean overflowToDisk,
                 int diskSpoolBufferSize, String diskStorePath) {
        this.name = name;
        this.maxElementsInMemory = maxElementsInMemory;
        this.overflowToDisk = overflowToDisk;
        this.clearOnFlush = true;
        this.diskSpoolBufferSize = diskSpoolBufferSize;
        this.diskStorePath = diskStorePath;
    }

    public void initialise() {
        this.diskStore = this.createDiskStore();
        this.memoryStore = new LruMemoryStore(this, diskStore,
                this.maxElementsInMemory);
        this.active = true;
    }

    protected Store createDiskStore() {
        if (this.overflowToDisk) {
            return new DiskStore(this, diskStorePath);
        } else {
            return null;
        }
    }

    public boolean isOverflowToDisk() {
        return this.overflowToDisk;
    }

    public boolean isClearOnFlush() {
        return this.clearOnFlush;
    }

    public String getName() {
        return this.name;
    }

    public long getMaxElementsOnDisk() {
        return this.maxElementsInMemory;
    }

    public int getDiskSpoolBufferSizeMB() {
        return this.diskSpoolBufferSize;
    }

    private void checkStatus() throws IllegalStateException {
        if (!active) {
            throw new IllegalStateException("The " + this.name
                    + " Cache is not alive.");
        }
    }

    public final Element get(Serializable key) {
        return get((Object) key);
    }

    public final Element get(Object key) {
        checkStatus();
        Element element;
        // long start = System.currentTimeMillis();

        element = searchInMemoryStore(key);
        if (element == null && this.overflowToDisk) {
            element = searchInDiskStore(key);
        }
        // todo is this expensive. Maybe ditch.
        // long end = System.currentTimeMillis();
        // long costs = start - end;
        // System.out.println("-Cost " + costs);
        return element;
    }

    private Element searchInMemoryStore(Object key) {
        Element element;
        element = memoryStore.get(key);
        if (element != null) {
            // 更新element 的使用频繁度
            // System.out.println("find the element " + element.getKey());
        }
        return element;
    }

    private Element searchInDiskStore(Object key) {
        if (!(key instanceof Serializable)) {
            return null;
        }
        Serializable serializableKey = (Serializable) key;
        Element element;
        element = diskStore.get(serializableKey);
        if (element != null) {
            // 放在内存中
            try {
                memoryStore.put(element);
            } catch (CacheException e) {
                e.printStackTrace();
            }
        }
        return element;
    }

    public final void put(Element element) {
        put(element, false);
    }

    /**
     */
    public final void put(Element element, boolean notify) {
        checkStatus();
        if (!active) {
            return;
        }
        if (element == null) {
            return;
        }

        if (element.getObjectKey() == null) {
            return;
        }

        // boolean elementExists;
        // Object key = element.getObjectKey();
        // elementExists = isElementInMemory(key) || isElementOnDisk(key);

        backOffIfDiskSpoolFull();
        try {
            memoryStore.put(element);
        } catch (CacheException e) {
            e.printStackTrace();
        }
    }

    private void backOffIfDiskSpoolFull() {

        if (diskStore != null && diskStore.bufferFull()) {
            // back off to avoid OutOfMemoryError
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // do not care if this happens
            }
        }
    }

    public final boolean isElementInMemory(Object key) {
        return memoryStore.containsKey(key);
    }

    public final boolean isElementOnDisk(Serializable key) {
        return isElementOnDisk((Object) key);
    }

    public final boolean isElementOnDisk(Object key) {
        if (!(key instanceof Serializable)) {
            return false;
        }
        Serializable serializableKey = (Serializable) key;
        if (this.overflowToDisk) {
            return diskStore != null && diskStore.containsKey(serializableKey);
        } else {
            return false;
        }
    }

    public final List<Object> getKeys() throws IllegalStateException,
            CacheException {
        checkStatus();
        List<Object> allKeyList = new ArrayList<Object>();
        List<Object> keyList = Arrays.asList(memoryStore.getKeyArray());
        allKeyList.addAll(keyList);
        if (this.overflowToDisk) {
            Set<Object> allKeys = new HashSet<Object>();
            // within the store keys will be unique
            allKeys.addAll(keyList);
            Object[] diskKeys = diskStore.getKeyArray();
            for (Object diskKey : diskKeys) {
                if (allKeys.add(diskKey)) {
                    // Unique, so add it to the list
                    allKeyList.add(diskKey);
                }
            }
        }
        return allKeyList;
    }

    public final boolean remove(Serializable key) throws IllegalStateException {
        return remove((Object) key);
    }

    public final boolean remove(Object key) throws IllegalStateException {
        return remove(key, false);
    }

    @SuppressWarnings("unused")
    private boolean remove(Object key, boolean expiry)
            throws IllegalStateException {
        checkStatus();
        Element elementFromDiskStore;
        Element elementFromMemoryStore = memoryStore.remove(key);
        elementFromMemoryStore = null;
        if (this.overflowToDisk) {
            if ((key instanceof Serializable)) {
                Serializable serializableKey = (Serializable) key;
                elementFromDiskStore = diskStore.remove(serializableKey);
                elementFromDiskStore = null;
            }

        }
        return true;
    }

    public final synchronized void flush() {
        checkStatus();
        try {
            memoryStore.flush();
            if (this.overflowToDisk) {
                diskStore.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
