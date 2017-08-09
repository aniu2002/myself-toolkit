package com.sparrow.collect.crawler.conf.format;

import com.sparrow.collect.crawler.conf.AbstractConfigured;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/2.
 */
public class FormatConfig extends AbstractConfigured {
    private String classMap;
    /** formatter */
    private String formatter;
    /** indexes */
    private String [] paraNameIndexes;
    private List<FieldMap> fieldMaps;

    public String[] getParaNameIndexes() {
        return paraNameIndexes;
    }

    public void setParaNameIndexes(String[] paraNameIndexes) {
        this.paraNameIndexes = paraNameIndexes;
    }

    public String getClassMap() {
        return classMap;
    }

    public void setClassMap(String classMap) {
        this.classMap = classMap;
    }

    public String getFormatter() {
        return formatter;
    }

    public void setFormatter(String formatter) {
        this.formatter = formatter;
    }

    public List<FieldMap> getFieldMaps() {
        return fieldMaps;
    }

    public void setFieldMaps(List<FieldMap> fieldMaps) {
        this.fieldMaps = fieldMaps;
    }

    public void addFieldMap(FieldMap fieldMap) {
        if (fieldMap == null) return;
        if (this.fieldMaps == null)
            this.fieldMaps = new ArrayList<FieldMap>();
        this.fieldMaps.add(fieldMap);
    }

    public void addFieldMap(String name, String express) {
        this.addFieldMap(new FieldMap(name, express));
    }

    public void addFieldMap(String name, String express, int type) {
        this.addFieldMap(new FieldMap(name, express, type));
    }
}

