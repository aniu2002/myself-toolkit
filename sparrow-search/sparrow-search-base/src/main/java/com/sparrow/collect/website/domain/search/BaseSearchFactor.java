package com.sparrow.collect.website.domain.search;

import com.sparrow.collect.website.data.search.UserFactor;
import com.sparrow.collect.website.domain.search.filter.FieldFilter;
import com.sparrow.collect.website.domain.search.soter.SearchSorter;
import com.sparrow.collect.website.query.Pagination;

import java.util.List;

/**
 * Created by yangtao on 2015/4/16.
 */
public class BaseSearchFactor {
    //用户信息
    private UserFactor userFactor;
    //搜索关键词
    private String keyword;
    //过滤条件
    private List<FieldFilter> filters;
    //排序
    private List<SearchSorter> sorters;
    //分页
    private Pagination pagination;

    public BaseSearchFactor() {}

    public UserFactor getUserFactor() {
        return userFactor;
    }

    public void setUserFactor(UserFactor userFactor) {
        this.userFactor = userFactor;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<FieldFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<FieldFilter> filters) {
        this.filters = filters;
    }

    public List<SearchSorter> getSorters() {
        return sorters;
    }

    public void setSorters(List<SearchSorter> sorters) {
        this.sorters = sorters;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
