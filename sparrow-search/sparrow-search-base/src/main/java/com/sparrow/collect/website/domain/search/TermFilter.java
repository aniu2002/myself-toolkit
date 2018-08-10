package com.sparrow.collect.website.domain.search;

/**
 * Created by yaobo on 2014/12/15.
 */
public class TermFilter<T> {
    private String term;

    private T value;

    public TermFilter() {}

    public TermFilter(String term, T value) {
        this.term = term;
        this.value = value;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
