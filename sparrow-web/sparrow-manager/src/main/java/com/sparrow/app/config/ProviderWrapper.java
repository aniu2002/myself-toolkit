package com.sparrow.app.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yuanzc on 2015/8/18.
 */
public class ProviderWrapper {

    private Map<String, SourceCfg> sources;

    private Map<String, ProviderCfg> providers;

    public void addSource(SourceCfg sourceCfg) {
        if (sourceCfg == null)
            return;
        if (this.sources == null) {
            this.sources = new ConcurrentHashMap<String, SourceCfg>();
        }
        this.sources.put(sourceCfg.getName(), sourceCfg);
    }

    public void addProvider(ProviderCfg providerCfg) {
        if (providerCfg == null)
            return;
        if (this.providers == null) {
            this.providers = new ConcurrentHashMap<String, ProviderCfg>();
        }
        this.providers.put(providerCfg.getName(), providerCfg);
    }

    public SourceCfg getSource(String name) {
        if (this.sources == null)
            return null;
        return this.sources.get(name);
    }

    public ProviderCfg getProviders(String name) {
        if (this.providers == null)
            return null;
        return providers.get(name);
    }
}