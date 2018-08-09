package com.sparrow.collect.website.filter.support;

import com.dili.dd.searcher.basesearch.search.beans.Ranger;
import com.dili.dd.searcher.basesearch.search.filter.SearchFilter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermsFilter;
import org.apache.lucene.search.Filter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by xiaozhiming on 2014/12/23.
 * 用于过滤 term = a || term = b的数据
 */
public class SearchTermsQueryFilter implements SearchFilter {

    @Override
    public List<Filter> getFilter(String field, List<Ranger> ranger, Map<String, String> extend) {
        List<Term> termList = new LinkedList<Term>();
        for (Ranger filterTerm : ranger) {
            Term term = new Term(field, filterTerm.getLowerValue().toString());
            termList.add(term);
        }
        if(termList.isEmpty()) {
            return null;
        }
        TermsFilter termsFilter = new TermsFilter(termList);
        List<Filter> filterList = new ArrayList<>(1);
        filterList.add(termsFilter);
        return filterList;
    }
}
