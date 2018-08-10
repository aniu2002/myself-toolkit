package com.sparrow.collect.website.domain.search.soter;

/**
 * Created by yangtao on 2015/4/16.
 */
public class SearchSorter {
    //排序字段类型定义
    public static enum SortType {
        //int类型排序
        INT,
        //long类型排序
        LONG,
        //string类型排序
        STRING
    }

    //排序字段
    private String field;
    //排序字段类型
    private SortType sortType;
    //升序/降序  false/true
    private boolean reverse;

    protected SearchSorter() {}

    public SearchSorter(String field, SortType sortType) {
        this(field, sortType, false);
    }

    public SearchSorter(String field, SortType sortType, boolean reverse) {
        this.field = field;
        this.sortType = sortType;
        this.reverse = reverse;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }
}
