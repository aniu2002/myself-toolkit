package com.sparrow.collect.website.filter.support;

import com.dili.dd.searcher.basesearch.search.beans.Ranger;
import com.dili.dd.searcher.basesearch.search.filter.SearchFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.NumericRangeFilter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <B>Description</B> <br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 * 
 * @createTime 2014年6月11日 下午4:21
 * @author zhanglin
 */
public class SearchNumberRangeFilter implements SearchFilter {

    @Override
    public List<Filter> getFilter(String field, List<Ranger> rangers,
            Map<String, String> extend) {

        List<Filter> retFilters = new LinkedList<Filter>();
        for (Ranger ranger : rangers) {
            if (ranger.getLowerValue() instanceof Integer || ranger.getUpperValue() instanceof Integer) {
                Filter filter = NumericRangeFilter.newIntRange(field,
                        ranger.getLowerValue() == null ? null : Integer.parseInt(ranger.getLowerValue().toString()),
                        ranger.getUpperValue() == null ? null : Integer.parseInt(ranger.getUpperValue().toString()), ranger.isIncludeLower(),
                        ranger.isIncludeUpper());
                retFilters.add(filter);
            }else if (ranger.getLowerValue() instanceof Long || ranger.getUpperValue() instanceof Long) {
                Filter filter = NumericRangeFilter.newLongRange(field,
                        ranger.getLowerValue() == null ? null : Long.parseLong(ranger.getLowerValue().toString()),
                        ranger.getUpperValue() == null ? null : Long.parseLong(ranger.getUpperValue().toString()), ranger.isIncludeLower(),
                        ranger.isIncludeUpper());
                retFilters.add(filter);
            }
        }
        return retFilters;
    }

}
