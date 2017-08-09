package com.sparrow.app.config;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

public class ProviderRuleSet extends RuleSetBase {

    public void addRuleInstances(Digester digester) {
        digester.addObjectCreate("data/sources/source", SourceCfg.class);
        digester.addSetProperties("data/sources/source");
        digester.addBeanPropertySetter("data/sources/source", "props");
        digester.addSetNext("data/sources/source", "addSource",
                "com.sparrow.manager.config.SourceCfg");

        digester.addObjectCreate("data/providers/provider",
                ProviderCfg.class);
        digester.addSetProperties("data/providers/provider");
        digester.addBeanPropertySetter("data/providers/provider", "script");
        digester.addSetNext("data/providers/provider", "addProvider",
                "com.sparrow.manager.config.ProviderCfg");
    }
}
