package com.sparrow.app.config;

import com.sparrow.core.config.SystemConfig;
import org.apache.commons.digester.Digester;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by yuanzc on 2015/12/30.
 */
public abstract class ProviderLoader {
    static final ProviderWrapper instance;

    static {
        instance = new ProviderWrapper();
        Digester configDigester = new Digester();
        configDigester.setNamespaceAware(false);
        configDigester.setValidating(false);
        configDigester.setUseContextClassLoader(true);
        configDigester.addRuleSet(new ProviderRuleSet());

        configDigester.push(instance);
        try {
            configDigester.parse(new FileInputStream(new File(SystemConfig.getSysProperty("provider.cfg.path"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final ProviderWrapper getProviderWrapper() {
        return instance;
    }

    public static void main(String args[]) {
        System.out.println(getProviderWrapper());
    }
}
