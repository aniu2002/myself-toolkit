package com.sparrow.collect.website.domain.search;

/**
 * Created by yaobo on 2014/12/15.
 */
public class RangeFilter<T> {
    private String term;

    private T lower;

    private T upper;

    private boolean includeLower = true;

    private boolean includeUpper = true;

    public RangeFilter() {}

    public RangeFilter(String term, T lower, T upper) {
        this.term = term;
        this.lower = lower;
        this.upper = upper;
    }

    public RangeFilter(String term, T lower, T upper, boolean includeLower, boolean includeUpper) {
        this.term = term;
        this.lower = lower;
        this.upper = upper;
        this.includeLower = includeLower;
        this.includeUpper = includeUpper;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public T getLower() {
        return lower;
    }

    public void setLower(T lower) {
        this.lower = lower;
    }

    public T getUpper() {
        return upper;
    }

    public void setUpper(T upper) {
        this.upper = upper;
    }

    public boolean isIncludeLower() {
        return includeLower;
    }

    public void setIncludeLower(boolean includeLower) {
        this.includeLower = includeLower;
    }

    public boolean isIncludeUpper() {
        return includeUpper;
    }

    public void setIncludeUpper(boolean includeUpper) {
        this.includeUpper = includeUpper;
    }
}
