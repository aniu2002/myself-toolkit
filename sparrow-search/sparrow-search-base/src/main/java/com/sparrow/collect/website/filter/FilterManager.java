package com.sparrow.collect.website.filter;

import com.dili.dd.searcher.basesearch.search.beans.Ranger;
import com.dili.dd.searcher.basesearch.search.beans.search.SearchBean;
import org.apache.lucene.queries.ChainedFilter;
import org.apache.lucene.search.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <B>Description</B>过滤管理<br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 *
 * @author zhanglin
 * @createTime 2014年6月11日 下午4:21
 */
public class FilterManager {

    private static FilterManager instance = new FilterManager();

    public static FilterManager getInstance() {
        return instance;
    }

    private static String BUSINESS_ID = "id";

    /**
     * this method is 获取filter
     *
     * @param searchBean
     * @return
     * @createTime 2014年6月12日 下午5:51:17
     * @author zhanglin
     */
    public Filter getFilter(SearchBean searchBean) throws Exception {

        ChainedFilter chainFilter = null;
        FilterBean filterBean = searchBean.getFilterBean();
        Map<String, List<Ranger>> fieldsRangInfo = null;
        if (null != filterBean) {
            fieldsRangInfo = filterBean.getFieldsRangeInfo();
        }
        if (fieldsRangInfo != null) {
            ArrayList<Filter> filterArryList = new ArrayList<Filter>();
            for (String field : fieldsRangInfo.keySet()) {
                List<Filter> filter = FilterFactory.getInstance().getFilter(field, searchBean);
                if (filter != null) {
                    filterArryList.addAll(filter);
                }
            }

            //add by yb: 增加排重filter
//            filterArryList.add(new DuplicateFilter(BUSINESS_ID));
//            filterArryList.add(new RamDiskDuplicateFilter(BUSINESS_ID));

            if (filterArryList.size() > 0) {
                Filter[] filterArray = new Filter[filterArryList.size()];
                chainFilter = new ChainedFilter(filterArryList.toArray(filterArray), ChainedFilter.AND);
            }
        }

        //add by yb: 增加排重filter
//        if (chainFilter == null){
//            chainFilter = new ChainedFilter(new Filter[]{new DuplicateFilter(BUSINESS_ID)});
//            chainFilter = new ChainedFilter(new Filter[]{new RamDiskDuplicateFilter(BUSINESS_ID)});
//        }

        //-----------------------
        return chainFilter;
    }

}
