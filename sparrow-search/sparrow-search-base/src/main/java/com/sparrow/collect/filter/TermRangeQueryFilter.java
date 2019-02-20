package com.sparrow.collect.filter;

import org.apache.lucene.search.Filter;
import org.apache.lucene.util.BytesRef;

/**
 * Created by Administrator on 2019/2/20 0020.
 */
public class TermRangeQueryFilter implements SearchFilter {
    private final String fieldName;
    private String lower;
    private String upper;

    private boolean includeLower;
    private boolean includeUpper;

    public TermRangeQueryFilter(String fieldName) {
        this.fieldName = fieldName;
    }

    public TermRangeQueryFilter(String fieldName, String lower, String upper) {
        this.fieldName = fieldName;
        this.lower = lower;
        this.upper = upper;
    }

    public void setLower(String lower) {
        this.lower = lower;
    }

    public void setUpper(String upper) {
        this.upper = upper;
    }

    public void setIncludeLower(boolean includeLower) {
        this.includeLower = includeLower;
    }

    public void setIncludeUpper(boolean includeUpper) {
        this.includeUpper = includeUpper;
    }

    @Override
    public Filter filter() {
        return new org.apache.lucene.search.TermRangeFilter(this.fieldName,
                this.lower == null ? null : new BytesRef(this.lower),
                this.upper == null ? null : new BytesRef(this.upper),
                this.includeLower,
                this.includeUpper);
    }
}
