package com.sparrow.collect.orm.mapping;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

public class MapConfigRuleSet extends RuleSetBase {

    public void addRuleInstances(Digester digester) {
        digester.addObjectCreate("maps/map", MapRow.class);
        digester.addSetProperties("maps/map");
        digester.addBeanPropertySetter("maps/map/key");
        digester.addBeanPropertySetter("maps/map/select");
        digester.addBeanPropertySetter("maps/map/insert");
        digester.addBeanPropertySetter("maps/map/update");
        digester.addBeanPropertySetter("maps/map/delete");
        digester.addBeanPropertySetter("maps/map/query");
        digester.addSetNext("maps/map", "addMapItem",
                "com.sparrow.collect.orm.mapping.MapRow");
    }
}
