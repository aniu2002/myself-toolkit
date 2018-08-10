package com.sparrow.collect.website.domain.search.filter;

/**
 * Created by yangtao on 2015/4/16.
 * 单个值过滤查询
 * eg:name="a"的过滤
 */
public class SingleValueFilter implements FieldFilter {
    //过滤字段
    private String field;
    //过滤字段
    private String value;

    private SingleValueFilter() {

    }

    public SingleValueFilter(String field, String value) {
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
