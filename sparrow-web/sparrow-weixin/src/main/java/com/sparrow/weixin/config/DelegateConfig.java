package com.sparrow.weixin.config;

/**
 * Created by yuanzc on 2015/6/3.
 */
public class DelegateConfig {
    private String key;
    private EventConfigs eventConfig;

    public void put(String key, EntryConfig entryConfig) {
        if (entryConfig == null || key == null)
            return;
        if (this.eventConfig == null)
            this.eventConfig = new EventConfigs();
        this.eventConfig.put(key, entryConfig);
    }

    public EntryConfig get(String key) {
        if (this.eventConfig == null)
            return null;
        return this.eventConfig.get(key);
    }

    public boolean isEmpty() {
        return this.eventConfig == null || this.eventConfig.isEmpty();
    }

    public void clear() {
        if (this.eventConfig != null) {
            this.eventConfig.clear();
            this.eventConfig = null;
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public EventConfigs getEventConfig() {
        return eventConfig;
    }
}
