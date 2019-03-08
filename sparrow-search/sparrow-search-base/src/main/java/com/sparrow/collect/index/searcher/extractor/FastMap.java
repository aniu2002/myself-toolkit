package com.sparrow.collect.index.searcher.extractor;

import java.util.*;
import java.util.function.Consumer;

/**
 *  author: Yzc
 * - Date: 2019/3/6 22:19
 */

public class FastMap implements Map {
    private Object[] objects;
    private int size;
    private int length;
    private int index = 0;

    public FastMap(int size) {
        if (size < 1) {
            this.objects = new Object[0];
            this.size = this.length = 0;
        } else {
            this.length = size * 2;
            this.size = size;
            this.objects = new Object[this.length];
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.findKey(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (int i = 1; i < this.length; i += 2) {
            if (Objects.equals(this.objects[i], value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object get(Object key) {
        return this.findKey(key);
    }

    private Object findKey(Object key) {
        for (int i = 0; i < this.length; i += 2) {
            if (Objects.equals(this.objects[i], key)) {
                return this.objects[i + 1];
            }
        }
        return null;
    }

    private void forEachKey(Consumer consumer){
        for (int i = 0; i < this.length; i += 2) {
             consumer.accept(this.objects[i]);
        }
    }

    @Override
    public Object put(Object key, Object value) {
        this.objects[this.index++] = key;
        this.objects[this.index++] = value;
        return value;
    }

    @Override
    public Object remove(Object key) {
        Object object = null;
        for (int i = 0; i < this.length; i += 2) {
            if (Objects.equals(this.objects[i], key)) {
                object = this.objects[i + 1];
                this.objects[i + 1] = null;
            }
        }
        return object;
    }

    @Override
    public void putAll(Map m) {
        m.forEach((k, v) -> this.put(k, v));
    }

    @Override
    public void clear() {
        this.index = 0;
    }

    @Override
    public Set keySet() {
        return null;
    }

    @Override
    public Collection values() {
        return null;
    }

    @Override
    public Set<Entry> entrySet() {
        return null;
    }
}
