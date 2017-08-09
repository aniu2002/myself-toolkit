package com.sparrow.weixin.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanzc on 2015/6/3.
 */
public class EventConfigs {
    private Map<String, EntryConfig> entryConfigMap;

    public void put(String key, EntryConfig entryConfig) {
        if (entryConfig == null || key == null)
            return;
        if (this.entryConfigMap == null)
            this.entryConfigMap = new HashMap<String, EntryConfig>();
        this.entryConfigMap.put(key, entryConfig);
    }

    public EntryConfig get(String key) {
        if (this.entryConfigMap == null)
            return null;
        return this.entryConfigMap.get(key);
    }

    public Collection<EntryConfig> getEntryConfigs() {
        if (this.entryConfigMap != null && !this.entryConfigMap.isEmpty()) {
            return this.entryConfigMap.values();
        }
        return null;
    }

    public MsgConfig getMessageConfig(String key) {
        if (this.entryConfigMap == null)
            return null;
        EntryConfig config = this.entryConfigMap.get(key);
        if (config == null)
            return null;
        return config.getMsgConfig(key);
    }

    public boolean isEmpty() {
        return this.entryConfigMap == null || this.entryConfigMap.isEmpty();
    }

    public void clear() {
        if (this.entryConfigMap != null) {
            this.entryConfigMap.clear();
            this.entryConfigMap = null;
        }
    }
}
