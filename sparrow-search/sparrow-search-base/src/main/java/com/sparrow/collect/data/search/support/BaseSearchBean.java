package com.sparrow.collect.data.search.support;

import com.sparrow.collect.data.search.SearchBean;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

/**
 * Created by yangtao on 2015/4/17.
 */
public class BaseSearchBean extends SearchBean {
    //lucene查询对象
    protected Query query;
    //lucene排序对象
    protected Sort sort;
    //lucene过滤对象
    protected Filter filter;

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}
