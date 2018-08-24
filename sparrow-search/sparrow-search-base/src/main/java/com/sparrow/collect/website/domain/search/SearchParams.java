package com.sparrow.collect.website.domain.search;

import com.sparrow.collect.data.search.Sorter;
import com.sparrow.collect.data.search.UserFactor;
import com.sparrow.collect.website.query.PageAble;

import java.util.Collection;

/**
 * 搜索参数, 各业务的参数从该类继承
 * Created by yaobo on 2014/12/15.
 */
public class SearchParams {

    private String searchId;

    /**
     * 个性化搜索时，提供的用户信息
     */
    private UserFactor userFactor;

    /**
     * 搜索关键词列表
     */
    private String keyword;

    /**
     * 范围条件
     */
    private Collection<RangeFilter> rangeFilters;

    /**
     * 单个条件
     */
    private Collection<TermFilter> termFilters;
    /**
     * 分页信息
     */
    private PageAble page;
    /**
     * 排序字段
     */
    private Collection<Sorter> sorters;

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

    public Collection<RangeFilter> getRangeFilters() {
        return rangeFilters;
    }

    public void setRangeFilters(Collection<RangeFilter> rangeFilters) {
        this.rangeFilters = rangeFilters;
    }

    public Collection<TermFilter> getTermFilters() {
        return termFilters;
    }

    public void setTermFilters(Collection<TermFilter> termFilters) {
        this.termFilters = termFilters;
    }

    public PageAble getPage() {
        return page;
    }

    public void setPage(PageAble page) {
        this.page = page;
    }

    public Collection<Sorter> getSorters() {
        return sorters;
    }

    public void setSorters(Collection<Sorter> sorters) {
        this.sorters = sorters;
    }

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }
}
