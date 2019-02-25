package com.sparrow.collect.index.filter;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.NumericRangeFilter;

/**
 * Created by Administrator on 2019/2/20 0020.
 */
public class NumberRangeQueryFilter implements SearchFilter {
    private final String fieldName;
    private Number lower;
    private Number upper;

    private boolean includeLower;
    private boolean includeUpper;

    public NumberRangeQueryFilter(String fieldName) {
        this.fieldName = fieldName;
    }

    public NumberRangeQueryFilter(String fieldName, Number lower, Number upper) {
        this.fieldName = fieldName;
        this.lower = lower;
        this.upper = upper;
    }

    public void setLower(Number lower) {
        this.lower = lower;
    }

    public void setUpper(Number upper) {
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
        if (this.lower == null && this.upper == null) {
            return null;
        }
        if (this.isInt()) {
            return NumericRangeFilter.newIntRange(this.fieldName,
                    this.getInt(this.lower),
                    this.getInt(this.upper),
                    this.includeLower,
                    this.includeUpper);
        } else if (this.isLong()) {
            return NumericRangeFilter.newLongRange(this.fieldName,
                    this.getLong(this.lower),
                    this.getLong(this.upper),
                    this.includeLower,
                    this.includeUpper);
        } else if (this.isFloat()) {
            return NumericRangeFilter.newFloatRange(this.fieldName,
                    this.getFloat(this.lower),
                    this.getFloat(this.upper),
                    this.includeLower,
                    this.includeUpper);
        } else if (this.isDouble()) {
            return NumericRangeFilter.newDoubleRange(this.fieldName,
                    this.getDouble(this.lower),
                    this.getDouble(this.upper),
                    this.includeLower,
                    this.includeUpper);
        } else {
            return null;
        }
    }

    private Integer getInt(Number number) {
        if (number == null)
            return null;
        else
            return (Integer) number;
    }

    private Long getLong(Number number) {
        if (number == null)
            return null;
        else
            return (Long) number;
    }

    private Float getFloat(Number number) {
        if (number == null)
            return null;
        else
            return (Float) number;
    }

    private Double getDouble(Number number) {
        if (number == null)
            return null;
        else
            return (Double) number;
    }

    private boolean isInt() {
        return this.lower == null ? this.upper instanceof Integer : this.lower instanceof Integer;
    }

    private boolean isLong() {
        return this.lower == null ? this.upper instanceof Long : this.lower instanceof Long;
    }

    private boolean isFloat() {
        return this.lower == null ? this.upper instanceof Float : this.lower instanceof Float;
    }

    private boolean isDouble() {
        return this.lower == null ? this.upper instanceof Double : this.lower instanceof Double;
    }
}
