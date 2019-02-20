package com.sparrow.collect.filter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermsFilter;
import org.apache.lucene.search.Filter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2019/2/20 0020.
 */
public class TermsQueryFilter implements SearchFilter {
    private String fieldName;
    private List<String> terms;

    public TermsQueryFilter(String fieldName) {
        this.fieldName = fieldName;
    }

    public void addTerm(String term) {
        if (StringUtils.isEmpty(term)) {
            return;
        }
        if (this.terms == null) {
            this.terms = new LinkedList();
        }
        this.terms.add(term);
    }

    @Override
    public Filter filter() {
        if (CollectionUtils.isEmpty(this.terms)) {
            return null;
        }
        List<Term> termList = new LinkedList<Term>();
        for (String filterTerm : this.terms) {
            Term term = new Term(this.fieldName, filterTerm);
            termList.add(term);
        }
        if (termList.isEmpty()) {
            return null;
        }
        return new TermsFilter(termList);
    }
}
