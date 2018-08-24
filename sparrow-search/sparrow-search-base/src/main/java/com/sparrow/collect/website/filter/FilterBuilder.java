package com.sparrow.collect.website.filter;

import com.sparrow.collect.data.search.Translator;
import com.sparrow.collect.website.domain.search.filter.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermFilter;
import org.apache.lucene.queries.TermsFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.NumericRangeFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangtao on 2015/4/17.
 */
public class FilterBuilder {
    private static FilterBuilder instance = new FilterBuilder();

    public static FilterBuilder getInstance() {
        return instance;
    }

    public Filter build(String searchId, FieldFilter filter) {
        if (filter instanceof SingleValueFilter) {
            return build(searchId, (SingleValueFilter) filter);

        } else if (filter instanceof MultiValueFilter) {
            return build(searchId, (MultiValueFilter) filter);

        } else if (filter instanceof LongValueRangeFilter) {
            return build(searchId, (LongValueRangeFilter) filter);

        } else if (filter instanceof IntValueRangeFilter) {
            return build(searchId, (IntValueRangeFilter) filter);
        }
        return null;
    }

    protected Filter build(String searchId, SingleValueFilter filter) {
        if (filter.getField() == null || filter.getValue() == null) {
            return null;
        }
        String field = translate(searchId, filter.getField());
        Term term = new Term(field, filter.getValue());
        TermFilter termFilter = new TermFilter(term);
        return termFilter;
    }

    protected Filter build(String searchId, MultiValueFilter filter) {
        if (filter.getField() == null || filter.getValues() == null) {
            return null;
        }
        String field = translate(searchId, filter.getField());
        List<Term> termList = new ArrayList<>(filter.getValues().size());
        for (String value : filter.getValues()) {
            Term term = new Term(field, value);
            termList.add(term);
        }
        TermsFilter termsFilter = new TermsFilter(termList);
        return termsFilter;
    }

    protected Filter build(String searchId, LongValueRangeFilter filter) {
        if (filter.getField() == null || (filter.getMin() == null && filter.getMax() == null)) {
            return null;
        }
        String field = translate(searchId, filter.getField());
        Filter longRangeFilter = NumericRangeFilter.newLongRange(
                field,
                filter.getMin(),
                filter.getMax(),
                filter.isMinInclusive(),
                filter.isMaxInclusive());
        return longRangeFilter;
    }

    protected Filter build(String searchId, IntValueRangeFilter filter) {
        if (filter.getField() == null || (filter.getMin() == null && filter.getMax() == null)) {
            return null;
        }
        String field = translate(searchId, filter.getField());
        Filter intRangeFilter = NumericRangeFilter.newIntRange(
                field,
                filter.getMin(),
                filter.getMax(),
                filter.isMinInclusive(),
                filter.isMaxInclusive());
        return intRangeFilter;
    }

    public String translate(String searchId, String field) {
        String translated = Translator.getInstance().getFieldName(searchId, field);
        if (StringUtils.isNotBlank(translated)) {
            return translated;
        }
        return field;
    }
}
