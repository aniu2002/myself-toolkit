package com.sparrow.collect.website.data.search.support;

import com.dili.dd.searcher.basesearch.search.beans.Pagination;
import com.dili.dd.searcher.basesearch.search.beans.Ranger;
import com.dili.dd.searcher.basesearch.search.beans.search.SearchBean;
import com.dili.dd.searcher.basesearch.search.beans.search.SearchBeanFactory;
import com.dili.dd.searcher.basesearch.search.beans.search.Translator;
import com.dili.dd.searcher.basesearch.search.filter.FilterBean;
import com.dili.dd.searcher.basesearch.search.sort.SortBean;
import com.dili.dd.searcher.datainterface.domain.Sorter;
import com.dili.dd.searcher.datainterface.domain.UserFactor;
import com.dili.dd.searcher.datainterface.domain.search.RangeFilter;
import com.dili.dd.searcher.datainterface.domain.search.SearchParams;
import com.dili.dd.searcher.datainterface.domain.search.TermFilter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by yaobo on 2014/12/15.
 */
public class SimpleSearchBeanFactory extends SearchBeanFactory {

    public SearchBean createSearchBean(String searchId, Object params) {
        SearchParams searchParams = (SearchParams) params;

        SimpleSearchBean searchBean = new SimpleSearchBean();
        setCommon(searchBean, searchParams);
        setFilter(searchBean, searchParams);
        setSorter(searchBean, searchParams);
        setPagination(searchBean, searchParams);

        return searchBean;
    }

    private SearchBean setCommon(SimpleSearchBean bean, SearchParams params) {
        bean.setSearchId(params.getSearchId());
        bean.setSearchCondStr(params.getKeyword());
        bean.setKeyword(params.getKeyword());
        UserFactor userFactor = params.getUserFactor();
        if (userFactor != null) {
            bean.setUserId(userFactor.getUserId() == null ? null : userFactor.getUserId().toString());
            bean.setUserSign(userFactor.getUserSign() == null ? null : userFactor.getUserSign());
        }
        return bean;
    }

    private SearchBean setPagination(SimpleSearchBean bean, SearchParams params) {
        com.dili.dd.searcher.datainterface.domain.Pagination pagination = params.getPagination();
        if (pagination != null) {
            Pagination page = new Pagination();
            page.setPageNo(pagination.getPageNumber() < 1 ? 1 : pagination.getPageNumber());
            page.setPageSize(pagination.getPageSize() > PAGE_SIZE ? PAGE_SIZE : pagination.getPageSize());
            bean.setPagination(page);
        } else {
            Pagination page = new Pagination();
            page.setPageNo(1);
            page.setPageSize(50);
            bean.setPagination(page);
        }
        return bean;
    }

    private SearchBean setFilter(SimpleSearchBean bean, SearchParams params) {
        Map<String, List<Ranger>> filterRangInf = new HashMap<String, List<Ranger>>();

        Collection<TermFilter> termFilters = params.getTermFilters();
        if (CollectionUtils.isNotEmpty(termFilters)){
            for (TermFilter filter : termFilters) {
                com.dili.dd.searcher.basesearch.search.beans.Ranger ranger = new com.dili.dd.searcher.basesearch.search.beans.Ranger<Integer>();
                ranger.setLowerValue(filter.getValue());
                ranger.setUpperValue(filter.getValue());
                String fieldName = Translator.getInstance().getFieldName(bean.getSearchId(), filter.getTerm());
                if (StringUtils.isBlank(fieldName)) {
                    fieldName = filter.getTerm();
                }
//                List<com.dili.dd.searcher.basesearch.search.beans.Ranger> rangers = new LinkedList<com.dili.dd.searcher.basesearch.search.beans.Ranger>();
//                rangers.add(ranger);
                List<com.dili.dd.searcher.basesearch.search.beans.Ranger> rangers = filterRangInf.get(fieldName);
                if(rangers == null) {
                    rangers = new LinkedList<com.dili.dd.searcher.basesearch.search.beans.Ranger>();
                }
                rangers.add(ranger);
                filterRangInf.put(fieldName, rangers);
            }
        }

        Collection<RangeFilter> rangeFilters = params.getRangeFilters();
        if (CollectionUtils.isNotEmpty(rangeFilters)) {
            for (RangeFilter filter : rangeFilters) {
                com.dili.dd.searcher.basesearch.search.beans.Ranger ranger = new com.dili.dd.searcher.basesearch.search.beans.Ranger<Integer>();
                ranger.setLowerValue(filter.getLower());
                ranger.setUpperValue(filter.getUpper());
                ranger.setIncludeLower(filter.isIncludeLower());
                ranger.setIncludeUpper(filter.isIncludeUpper());
                String fieldName = Translator.getInstance().getFieldName(bean.getSearchId(), filter.getTerm());
                if (StringUtils.isBlank(fieldName)) {
                    fieldName = filter.getTerm();
                }
                List<com.dili.dd.searcher.basesearch.search.beans.Ranger> rangers = new LinkedList<com.dili.dd.searcher.basesearch.search.beans.Ranger>();
                rangers.add(ranger);
                filterRangInf.put(fieldName, rangers);
            }
        }

        FilterBean filterBean = new FilterBean();
        filterBean.setSerachId(bean.getSearchId());
        filterBean.setFieldsRangeInfo(filterRangInf);
        bean.setFilterBean(filterBean);
        return bean;
    }

    private SearchBean setSorter(SimpleSearchBean bean, SearchParams params) {
        Collection<Sorter> sorter = params.getSorters();
        if (sorter != null) {
            bean.setSortBeans(builderSortBeans(sorter.toArray(new Sorter[]{})));
        }
        return bean;
    }

    private List<SortBean> builderSortBeans(Sorter... args) {
        List<SortBean> ret = new ArrayList<SortBean>();
        for (int i = 0; i < args.length; i++) {
            Sorter sorter = args[i];
            if (sorter == null || sorter.getSortFeild() == null) {
                log.warn("sorter.getSortFeild() == null");
                continue;
            }
            String sortField = sorter.getSortFeild();
            int sortWay = sorter.getSortWay();
            SortBean sortBean = new SortBean();
            sortBean.setSortField(sortField);
            sortBean.setSortReverse(sortWay == 0 ? false : true);
            ret.add(sortBean);
        }
        return ret;
    }
}
