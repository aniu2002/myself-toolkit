package com.sparrow.weixin.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanzc on 2015/6/3.
 */
public class DispatchConfig {
    static final MsgConfig NOT_HANDLE_CONFIG = new MsgConfig("no");
    private Map<String, EntryConfig> entryConfigMap;
    private Map<String, MsgConfig> msgConfigMap;
    private DelegateConfig delegateConfig;

    public DelegateConfig getDelegateConfig() {
        return delegateConfig;
    }

    public void putMsg(String key, MsgConfig msgConfig) {
        if (msgConfig == null || key == null)
            return;
        if (this.msgConfigMap == null)
            this.msgConfigMap = new HashMap<String, MsgConfig>();
        this.msgConfigMap.put(key, msgConfig);
    }

    public MsgConfig getMsg(String key) {
        if ("!".equals(key))
            return NOT_HANDLE_CONFIG;
        if (this.msgConfigMap == null)
            return null;
        return this.msgConfigMap.get(key);
    }

    public void putEntry(String key, EntryConfig entryConfig) {
        if (entryConfig == null || key == null)
            return;
        if (this.entryConfigMap == null)
            this.entryConfigMap = new HashMap<String, EntryConfig>();
        this.entryConfigMap.put(key, entryConfig);
    }

    public EntryConfig getEntry(String key) {
        if ("event".equals(key)) {
            if (this.delegateConfig != null)
                return this.delegateConfig.get(key);
            return null;
        }
        if (this.entryConfigMap == null)
            return null;
        return this.entryConfigMap.get(key);
    }

    public void putDelegateEntry(String key, EntryConfig entryConfig) {
        if (entryConfig == null || key == null)
            return;
        if (this.delegateConfig == null)
            this.delegateConfig = new DelegateConfig();
        this.delegateConfig.put(key, entryConfig);
    }

    public EntryConfig getDelegateEntry(String key) {
        if (this.delegateConfig == null)
            return null;
        return this.delegateConfig.get(key);
    }

    public void clear() {
        if (this.msgConfigMap != null) {
            this.msgConfigMap.clear();
            this.msgConfigMap = null;
        }
        if (this.entryConfigMap != null) {
            this.entryConfigMap.clear();
            this.entryConfigMap = null;
        }
        if (this.delegateConfig != null) {
            this.delegateConfig.clear();
            this.delegateConfig = null;
        }
    }
}
