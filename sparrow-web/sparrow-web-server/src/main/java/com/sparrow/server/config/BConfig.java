package com.sparrow.server.config;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class BConfig {
    private String name;
    private String clazz;
    private String parameter;
    private String init;
    private String destroyMethod;
    private List<PConfig> props;
    private List<PMapConfig> mapProps;
    private boolean lazy = true;
    private Class<?> clazzRef;


    public boolean isLazy() {
        return lazy;
    }

    public Class<?> getClazzRef() {
        return clazzRef;
    }

    public void setClazzRef(Class<?> clazzRef) {
        this.clazzRef = clazzRef;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    public String getDestroyMethod() {
        return destroyMethod;
    }

    public void setDestroyMethod(String destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    public List<PConfig> getProps() {
        return props;
    }

    public void setProps(List<PConfig> props) {
        this.props = props;
    }

    public void addPConfig(PConfig prop) {
        if (prop == null || StringUtils.isEmpty(prop.getName()))
            return;
        if (this.props == null)
            this.props = new ArrayList<PConfig>();
        this.props.add(prop);
    }

    public void addProperty(String name, Object valRef) {
        if (StringUtils.isEmpty(name) || valRef == null)
            return;
        PConfig setterCfg = new PConfig();
        setterCfg.setName(name);
        setterCfg.setRef(name);
        setterCfg.setRefValue(valRef);
        this.props.add(setterCfg);
    }

    public void addMapConfig(PMapConfig prop) {
        if (prop == null || StringUtils.isEmpty(prop.getName()))
            return;
        if (this.mapProps == null)
            this.mapProps = new ArrayList<PMapConfig>();
        this.mapProps.add(prop);
    }

    public List<PMapConfig> getMapProps() {
        return mapProps;
    }

    public void setMapProps(List<PMapConfig> mapProps) {
        this.mapProps = mapProps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getInit() {
        return init;
    }

    public void setInit(String init) {
        this.init = init;
    }
}
