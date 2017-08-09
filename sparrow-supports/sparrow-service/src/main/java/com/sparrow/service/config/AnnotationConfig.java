package com.sparrow.service.config;

import com.sparrow.core.config.SystemConfig;
import com.sparrow.core.utils.StringFormat;

import java.util.ArrayList;
import java.util.List;

public class AnnotationConfig {
    private String base;
    private String expression;
    private String transManager;
    private List<SetterConfig> setterConfig = new ArrayList<SetterConfig>();

    public AnnotationConfig() {

    }

    public AnnotationConfig(String base) {
        this.base = base;
    }

    public String getExpression() {
        return expression;
    }

    public String getTransManager() {
        return transManager;
    }

    public void setTransManager(String transManager) {
        this.transManager = transManager;
    }

    public void setExpression(String expression) {
        this.expression = StringFormat.format(expression);
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = StringFormat.format(base);
    }

    public void addSetterConfig(SetterConfig setterCfg) {
        this.setterConfig.add(setterCfg);
    }

    public List<SetterConfig> getSetterConfig() {
        return setterConfig;
    }

    public void setSetterConfig(List<SetterConfig> setterConfig) {
        this.setterConfig = setterConfig;
    }
}
