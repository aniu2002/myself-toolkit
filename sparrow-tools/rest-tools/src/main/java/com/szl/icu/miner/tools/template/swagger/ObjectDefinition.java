package com.szl.icu.miner.tools.template.swagger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/2.
 */
public class ObjectDefinition {
    private String clazz;
    private String name;
    private String type;
    private List<ObjectPropDefinition> props;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ObjectPropDefinition> getProps() {
        return props;
    }

    public void setProps(List<ObjectPropDefinition> props) {
        this.props = props;
    }

    public void addProp(ObjectPropDefinition objectPropDefine) {
        if (objectPropDefine == null)
            return;
        if (this.props == null)
            this.props = new ArrayList<ObjectPropDefinition>();
        this.props.add(objectPropDefine);
    }
}
