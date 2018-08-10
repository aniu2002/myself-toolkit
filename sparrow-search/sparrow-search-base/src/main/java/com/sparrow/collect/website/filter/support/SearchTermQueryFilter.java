package com.sparrow.collect.website.filter.support;

import com.sparrow.collect.website.filter.SearchFilter;
import com.sparrow.collect.website.query.Ranger;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermFilter;
import org.apache.lucene.search.Filter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <B>Description</B> TODO <br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 *
 * @author zhanglin
 * @createTime 2014年6月13日 上午10:48:42
 */
public class SearchTermQueryFilter implements SearchFilter {

    @Override
    public List<Filter> getFilter(String field, List<Ranger> rangers,
                                  Map<String, String> extend) {

        List<Filter> retFilters = new LinkedList<Filter>();
        for (Ranger ranger : rangers) {
//            Term term = new Term(field, new BytesRef(ranger.getLowerValue().toString()));
//            TermQuery termQuery = new TermQuery(term);
//            Filter qf = new QueryWrapperFilter(termQuery);
//            retFilters.add(qf);


            Term term = new Term(field, ranger.getLowerValue().toString());
            TermFilter termFilter = new TermFilter(term);
            retFilters.add(termFilter);

//            Filter filter = NumericRangeFilter.newLongRange(field,
//                    Long.valueOf(ranger.getLowerValue().toString()),
//                    Long.valueOf(ranger.getLowerValue().toString()),
//                    ranger.isIncludeLower(),
//                    ranger.isIncludeUpper());
//            retFilters.add(filter);
        }
        return retFilters;
    }

}
