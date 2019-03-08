package com.sparrow.collect.index.searcher.extractor;

import com.sparrow.collect.index.config.FieldSetting;
import com.sparrow.collect.index.searcher.ResultExtractor;
import com.sparrow.collect.index.utils.BeanUtils;
import com.sparrow.collect.index.utils.ConvertUtils;
import com.sparrow.collect.index.utils.PropertyUtils;
import org.apache.lucene.document.Document;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * bean的result的包装器
 * author YZC
 * version 1.0 (2013-12-23)
 * modify
 */
public class BeanResultExtractor<T> implements ResultExtractor<T> {
    private Class<T> mappedClass;
    private Map<String, PropertyDescriptor> mappedFields;
    private List<FieldSetting> fieldSettings;

    public BeanResultExtractor(Class<T> mappedClass, List<FieldSetting> fieldSettings) {
        this.fieldSettings = fieldSettings;
        initialize(mappedClass);
    }

    protected void initialize(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
        this.mappedFields = new HashMap<>();
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(mappedClass);
        for (PropertyDescriptor pd : pds) {
            if (pd.getWriteMethod() != null) {
                this.mappedFields.put(pd.getName(), pd);
            }
        }
    }

    private List<FieldSetting> getFieldSettings() {
        return fieldSettings;
    }

    @Override
    public T extract(Document document) {
        return mapRow(document);
    }

    private T mapRow(Document document) {
        T mappedObject = BeanUtils.instantiate(this.mappedClass);
        for (FieldSetting setting : this.getFieldSettings()) {
            PropertyDescriptor pd = this.mappedFields.get(setting.getName());
            if (pd != null) {
                Object value = convertToObject(document, setting);
                BeanUtils.setValue(pd, mappedObject, value);
            }
        }
        return mappedObject;
    }

    private Object convertToObject(Document document, FieldSetting field) {
        return ConvertUtils.convert(document.get(field.getName()), getFieldType(field));
    }

    private Class getFieldType(FieldSetting fieldSetting) {
        if (fieldSetting == null) {
            return String.class;
        }
        switch (fieldSetting.getType()) {
            case DOUBLE:
                return Double.class;
            case FLOAT:
                return Float.class;
            case INT:
                return Integer.class;
            case LONG:
                return Long.class;
            case TEXT:
            case KEYWORD:
            case STRING:
            case NONE:
            default:
                return String.class;
        }
    }
}
