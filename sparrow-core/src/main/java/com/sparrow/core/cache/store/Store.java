package com.sparrow.core.cache.store;

import java.io.IOException;

import com.sparrow.core.cache.Element;
import com.sparrow.core.cache.exceptions.CacheException;


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
