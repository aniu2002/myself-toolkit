package com.sparrow.collect.website.query;

/**
 * Created by yaobo on 2014/6/11.
 */
public class Page {
    private Integer pageNo;

    private Integer pageSize;

    private Integer total;

    public Integer getTotal() {
        return total;
    }

    public Page setTotal(Integer total) {
        this.total = total;
        return this;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
