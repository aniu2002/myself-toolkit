package com.sparrow.collect.cache.bdb;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import java.io.File;

/**
 *
 */
public class DataTag {
    private final Environment environment;
    private final UrlsStore urlsStore;

    public DataTag(String dir) {
        File envHome = new File(dir, "bdb");
        if (!envHome.exists())
            envHome.mkdirs();
        boolean persist = true;
        EnvironmentConfig envConfig = new EnvironmentConfig();
        // envConfig.setDurability(true);
        envConfig.setAllowCreate(true);
        envConfig.setTransactional(persist);
        envConfig.setLocking(persist);
        if (!persist)
            IO.deleteFolderContents(envHome);
        this.environment = new Environment(envHome, envConfig);
        this.urlsStore = new UrlsStore(environment, "uuidStore", persist);
        System.out.println(String.format("BDB URL rows : %s", this.urlsStore.getDocCount()));
    }

    public void put(String key) {
        this.urlsStore.put(key);
    }

    public boolean exists(String key) {
        return this.urlsStore.exists(key);
    }

    public void close() {
        this.urlsStore.sync();
        this.urlsStore.close();
        this.environment.close();
    }
}
