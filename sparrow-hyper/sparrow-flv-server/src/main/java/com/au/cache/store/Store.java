package com.au.cache.store;

import java.io.IOException;

import com.au.cache.Element;
import com.au.cache.exceptions.CacheException;

public interface Store {

    void put(Element element) throws CacheException;

    Element get(Object key);
    
    Object[] getKeyArray();
    
    Element remove(Object key);
    
    void removeAll() throws CacheException;

    void dispose();
    
    int getSize();
    
    boolean containsKey(Object key);
    
    void flush() throws IOException;
    
    boolean bufferFull();
}
