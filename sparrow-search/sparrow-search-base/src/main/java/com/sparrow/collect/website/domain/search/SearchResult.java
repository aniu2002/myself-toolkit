package com.sparrow.collect.website.domain.search;

import com.sparrow.collect.website.query.PageAble;

import java.util.List;

/**
 * Created by yaobo on 2014/12/16.
 */
public class SearchResult<T> {
    private List<T> datas;
    private PageAble page;

    public SearchResult() {
    }

    public SearchResult(List<T> datas, PageAble page) {
        this.datas = datas;
        this.page = page;
    }

    public List<T> getDatas() {
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    public PageAble getPage() {
        return page;
    }

    public void setPage(PageAble page) {
        this.page = page;
    }
}
