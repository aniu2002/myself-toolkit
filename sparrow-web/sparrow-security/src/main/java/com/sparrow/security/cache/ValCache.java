package com.sparrow.security.cache;

import com.sparrow.core.cache.Cache;
import com.sparrow.core.cache.Element;
import com.sparrow.security.authz.AuthorizationInfo;

public class ValCache {
    private final Cache cache;

    {
        cache = new Cache("permission", 300, true, 600,
                System.getProperty("app.home", System.getProperty("user.home")) + "/.cache");
        cache.initialise();
    }

    public AuthorizationInfo get(Object key) {
        Element ele = cache.get(key);
        if (ele != null)
            return (AuthorizationInfo) ele.getValue();
        return null;
    }

    public void put(Object key, AuthorizationInfo info) {
        Element ele = new Element(key, info);
        cache.put(ele);
    }

    public void remove(Object key) {
        cache.remove(key);
    }
}
