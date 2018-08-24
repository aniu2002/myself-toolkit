package com.sparrow.collect.data.search.sort;

import com.sparrow.collect.website.domain.search.soter.SearchSorter;
import org.apache.lucene.search.SortField;

/**
 * Created by yangtao on 2015/4/17.
 */
public class SortBuilder {
    private static SortBuilder instance;

    public static SortBuilder getInstance() {
        if(instance == null) {
            synchronized (SortBuilder.class) {
                if(instance == null) {
                    instance = new SortBuilder();
                }
            }
        }
        return instance;
    }

    public SortField build(SearchSorter sorter) {
        SortField.Type type = convert(sorter.getSortType());
        if(type == null) {
            return null;
        }
        SortField sortField = new SortField(sorter.getField(), type, sorter.isReverse());
        return sortField;
    }

    protected SortField.Type convert(SearchSorter.SortType sortType) {
        if(sortType == SearchSorter.SortType.LONG) {
            return SortField.Type.LONG;

        } else if(sortType == SearchSorter.SortType.INT) {
            return SortField.Type.INT;

        } else if(sortType == SearchSorter.SortType.STRING) {
            return SortField.Type.STRING;
        }
        return null;
    }
}
