package com.sparrow.collect.website.domain.search;

import com.sparrow.collect.website.query.Pagination;

import java.util.List;

/**
 * Created by yaobo on 2014/12/16.
 */
public class SearchResult<T> {
    private List<T> datas;
    private Pagination pagination;

    public SearchResult() {
    }

    public SearchResult(List<T> datas, Pagination pagination) {
        this.datas = datas;
        this.pagination = pagination;
    }

    public List<T> getDatas() {
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
