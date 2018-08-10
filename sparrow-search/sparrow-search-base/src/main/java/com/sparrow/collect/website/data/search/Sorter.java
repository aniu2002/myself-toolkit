package com.sparrow.collect.website.data.search;

/**
 * 排序字段
 * @author zhaoYuan
 * @version 1.0
 * @created 15-5月-2014 19:32:59
 */
public class Sorter {

	/**
	 * 排序字段
	 */
	private String sortFeild;
	/**
	 * 排序方式。
	 * 0：升序    1：降序
	 */
	private int sortWay;

    public Sorter() {
    }

    public Sorter(String sortFeild, int sortWay) {
        this.sortFeild = sortFeild;
        this.sortWay = sortWay;
    }

    public String getSortFeild() {
        return sortFeild;
    }

    public void setSortFeild(String sortFeild) {
        this.sortFeild = sortFeild;
    }

    public int getSortWay() {
        return sortWay;
    }

    public void setSortWay(int sortWay) {
        this.sortWay = sortWay;
    }
}