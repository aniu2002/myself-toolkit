package com.sparrow.collect.website.filter.support;

import com.dili.dd.searcher.basesearch.search.beans.Ranger;
import com.dili.dd.searcher.basesearch.search.filter.SearchFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.TermRangeFilter;
import org.apache.lucene.util.BytesRef;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <B>Description</B> <br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 *
 * @author zhanglin
 * @createTime 2014年6月11日 下午4:21
 */
public class SearchTermRangeFilter implements SearchFilter {

    public List<Filter> getFilter(String field, List<Ranger> ranges, Map<String, String> extend) {

        List<Filter> retFilters = new LinkedList<Filter>();
        for (Ranger range : ranges) {
            Filter filter = new TermRangeFilter(field,
                    range.getLowerValue() == null ? null : new BytesRef(range.getLowerValue().toString()),
                    range.getUpperValue() == null ? null : new BytesRef(range.getUpperValue().toString()),
                    range.isIncludeLower(),
                    range.isIncludeUpper());
            retFilters.add(filter);
        }
        return retFilters;

    }

}
