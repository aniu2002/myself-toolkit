package com.sparrow.collect.website.data.attribute;

/**
 * 属性key = category+attr+value
 * Created by yaobo on 2014/6/13.
 */
public class AttrKey {
    private Integer category;
    private Integer attribute;
    private Integer value;

    public AttrKey() {
    }

    public AttrKey(Integer category, Integer attribute, Integer value) {
        this.category = category;
        this.attribute = attribute;
        this.value = value;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Integer getAttribute() {
        return attribute;
    }

    public void setAttribute(Integer attribute) {
        this.attribute = attribute;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String str = "\u0001";
//        String str = "-";
        sb.append(this.category).append(str).append(this.getAttribute()).append(str).append(this.getValue());
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttrKey attrKey = (AttrKey) o;

        if (!attribute.equals(attrKey.attribute)) return false;
        if (!category.equals(attrKey.category)) return false;
        if (!value.equals(attrKey.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = category.hashCode();
        result = 31 * result + attribute.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
