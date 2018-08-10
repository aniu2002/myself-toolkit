package com.sparrow.collect.website.domain.search.filter;

/**
 * Created by yangtao on 2015/4/16.
 * 范围过滤
 * int类型范围过滤
 */
public class IntValueRangeFilter extends RangeFilter<Integer> {

    private IntValueRangeFilter() {}

    public IntValueRangeFilter(String field, Integer min, Integer max) {
        super(field, min, max);
    }

    public IntValueRangeFilter(String field) {
        this(field, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
}
