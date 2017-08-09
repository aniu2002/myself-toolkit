package com.sparrow.server.config;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PMapConfig {
    private String name;
    private List<PConfig> props;
    private boolean fieldSet;

    public boolean isFieldSet() {
        return fieldSet;
    }

    public void setFieldSet(boolean fieldSet) {
        this.fieldSet = fieldSet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
