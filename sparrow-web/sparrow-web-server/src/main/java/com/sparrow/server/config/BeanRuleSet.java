package com.sparrow.server.config;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

import java.util.HashMap;
import java.util.Map;

public class BeanRuleSet extends RuleSetBase {

    public void addRuleInstances(Digester digester) {
        digester.addObjectCreate("beans/bean", BConfig.class);
        digester.addSetProperties("beans/bean");
        digester.addSetProperty("beans/bean/ref", "property", "rid");
        digester.addSetProperty("beans/bean/set", "property", "value");
        digester.addSetNext("beans/bean", "addBeanConfig",
                "com.sparrow.server.config.BConfig");

        digester.addObjectCreate("beans/bean/property", PConfig.class);
        digester.addSetProperties("beans/bean/property");
        digester.addSetNext("beans/bean/property", "addPConfig",
                "com.sparrow.server.config.PConfig");

        digester.addObjectCreate("beans/bean/map", PMapConfig.class);
        digester.addSetProperties("beans/bean/map");

        digester.addObjectCreate("beans/bean/map/entry", PConfig.class);
        digester.addSetProperties("beans/bean/map/entry");

        digester.addSetNext("beans/bean/map/entry", "addPConfig",
                "com.sparrow.server.config.PConfig");
        digester.addSetNext("beans/bean/map", "addMapConfig",
                "com.sparrow.server.config.PMapConfig");
    }
}
