package com.sparrow.collect.website.domain.search.filter;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by yangtao on 2015/4/16.
 * 多值过滤
 * eg:name="a" || name="b"的过滤
 */
public class MultiValueFilter implements FieldFilter {
    //过滤字段
    private String field;
    //过滤字段的值(集合)
    private Set<String> values;

    private MultiValueFilter() {

    }

    public MultiValueFilter(String field) {
        this(field, new HashSet<String>());
    }

    public MultiValueFilter(String field, Set<String> values) {
        this.field = field;
        this.values = values;
    }

    public void addValue(String value) {
        this.values.add(value);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Set<String> getValues() {
        return values;
    }

    public void setValues(Set<String> values) {
        this.values = values;
    }
}
