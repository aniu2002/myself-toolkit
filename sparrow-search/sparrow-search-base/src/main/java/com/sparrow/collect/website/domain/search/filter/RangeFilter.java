package com.sparrow.collect.website.domain.search.filter;

/**
 * Created by yangtao on 2015/4/16.
 */
public abstract class RangeFilter<T> implements FieldFilter {
    //过滤字段
    private String field;
    //范围下限
    private T min;
    //范围上限
    private T max;
    //是否包含下限值
    private boolean minInclusive;
    //是否包含上限值
    private boolean maxInclusive;

    protected RangeFilter() {

    }

    public RangeFilter(String field, T min, T max) {
        this.field = field;
        this.min = min;
        this.max = max;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public T getMin() {
        return min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public boolean isMinInclusive() {
        return minInclusive;
    }

    public void setMinInclusive(boolean minInclusive) {
        this.minInclusive = minInclusive;
    }

    public boolean isMaxInclusive() {
        return maxInclusive;
    }

    public void setMaxInclusive(boolean maxInclusive) {
        this.maxInclusive = maxInclusive;
    }
}
