package com.sparrow.collect.data.search.support;

import com.dili.dd.searcher.basesearch.search.beans.search.SearchBean;

/**
 * 提供默认的搜索条件
 * 历史原因造成SearchBean有太多冗余信息, 以后新的搜索条件都从SearchBean扩展
 * Created by yaobo on 2014/12/10.
 */
public class SimpleSearchBean extends SearchBean{

    private String keyword;

//    private Collection<RangeFilter> rangeFilters;
//
//    private Collection<TermFilter> termFilters;


    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
