package com.sparrow.collect.website.data.search;

import com.alibaba.fastjson.JSON;
import com.sparrow.collect.website.SearchIdDef;
import com.sparrow.collect.website.query.Pagination;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Created by yaobo on 2014/6/10.
 */
public class SearchBeanFactory {

    protected Log log = LogFactory.getLog(SearchBeanFactory.class);

    protected static int PAGE_SIZE = 500;

    public SearchBean createSearchBean(String searchId, Object params) {
        SearchBean<Object> searchBean = new SearchBean<>();
        searchBean.setSearchId(searchId);
        searchBean.setParamsBean(params);
        if (searchBean.getParamsBean() instanceof SearchFactor) {
            handleSearchFactor(searchBean);
        }
        log.debug("createSearchBean = " + JSON.toJSONString(searchBean));
        return searchBean;
    }

    @SuppressWarnings("unchecked")
    private void handleSearchFactor(SearchBean searchBean) {
        SearchFactor factor = (SearchFactor) searchBean.getParamsBean();
        searchBean.setSearchCondStr(factor.getKey());
        Pagination pagination = factor.getPagination();
        if (pagination != null) {
            Pagination page = new Pagination();
            page.setPageNo(pagination.getPageNo() < 1 ? 1 : pagination.getPageNo());
            page.setPageSize(pagination.getPageSize() > PAGE_SIZE ? PAGE_SIZE : pagination.getPageSize());
            searchBean.setPagination(page);
        } else {
            Pagination page = new Pagination();
            page.setPageNo(1);
            page.setPageSize(50);
            searchBean.setPagination(page);
        }

        String searchId = SearchIdDef.GOODS_SEARCHER;
        searchBean.setSearchId(searchId);

        Integer conVersion = 1;
        // --------------------------------------------filterbean
        searchBean.setFilterProperties(factor.getFilterProperties());
        searchBean.setCategory(factor.getCategory());
        searchBean.setMarket(factor.getMarketId());
        FilterBean filterBean = new FilterBean();
        filterBean.setSerachId(searchId);
        filterBean.setFieldsRangeInfo(handleFilterInf(searchBean, conVersion, searchId, factor.getFilterProperties(), factor.getRangeFilter()));
        searchBean.setFilterBean(filterBean);
        // ------------------------------------------------------
        // parse sortbean
        Sorter sorter = factor.getSorter();
        List<SortBean> sortBeans = builderSortBeans(sorter);
        searchBean.setSortBeans(sortBeans);

        UserFactor userFactor = factor.getUserFactor();
        if (userFactor != null) {
            searchBean.setUserId(userFactor.getUserId() == null ? null : userFactor.getUserId().toString());
            searchBean.setUserSign(userFactor.getUserSign() == null ? null : userFactor.getUserSign());
        }
    }

}
