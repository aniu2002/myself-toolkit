package com.sparrow.collect.filter;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.PrefixFilter;

/**
 * Created by Administrator on 2019/2/20 0020.
 */
public class TermQueryFilter implements SearchFilter {
    private final String fieldName;
    private String term;

    public TermQueryFilter(String fieldName) {
        this.fieldName = fieldName;
    }

    public TermQueryFilter(String fieldName, String term) {
        this.fieldName = fieldName;
        this.term = term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    @Override
    public Filter filter() {
        if (StringUtils.isEmpty(this.term)) {
            return null;
        }
        char c = this.term.charAt(this.term.length() - 1);
        if (c == '*') {
            return new PrefixFilter(new Term(this.fieldName, this.term));
        } else {
            return new TermFilter(new Term(this.fieldName, this.term));
        }
    }
}
