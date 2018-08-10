package com.sparrow.collect.website.domain.search.filter;

/**
 * Created by yangtao on 2015/4/16.
 * 范围过滤
 * long类型范围过滤
 */
public class LongValueRangeFilter extends RangeFilter<Long> {

    private LongValueRangeFilter() {}

    public LongValueRangeFilter(String field) {
        this(field, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public LongValueRangeFilter(String field, Long min, Long max) {
        super(field, min, max);
    }
}
