package com.sparrow.collect.data.attribute;

import java.util.HashSet;
import java.util.Set;

/**
 * 属性值
 * Created by yaobo on 2014/6/14.
 */
public class AttrValue {
    private Set<Integer> values = new HashSet<>();

    public boolean addValue(Integer value){
        return this.values.add(value);
    }

    public boolean addAttrValue(AttrValue value){
        return this.values.addAll(value.getValues());
    }

    public Set<Integer> getValues() {
        return values;
    }

    public void setValues(Set<Integer> values) {
        this.values = values;
    }

    public boolean contains(Integer value){
        return values.contains(value);
    }

}
