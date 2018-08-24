package com.sparrow.collect.data.search;

import com.alibaba.fastjson.JSON;
import com.sparrow.collect.website.Constant;
import com.sparrow.collect.website.SearchIdDef;
import com.sparrow.collect.data.search.sort.SortBean;
import com.sparrow.collect.website.filter.FilterBean;
import com.sparrow.collect.website.query.PageAble;
import com.sparrow.collect.website.query.Ranger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Created by yaobo on 2014/6/10.
 */
public class SearchBeanFactory {

    protected Log log = LogFactory.getLog(SearchBeanFactory.class);

    protected static int PAGE_SIZE = 500;

    public SearchBean createSearchBean(String searchId, Object params) {
        SearchBean<Object> searchBean = new SearchBean();
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
        PageAble pagination = factor.getPage();
        if (pagination != null) {
            PageAble page = new PageAble();
            page.setPageNo(pagination.getPageNo() < 1 ? 1 : pagination.getPageNo());
            page.setPageSize(pagination.getPageSize() > PAGE_SIZE ? PAGE_SIZE : pagination.getPageSize());
            searchBean.setPage(page);
        } else {
            PageAble page = new PageAble();
            page.setPageNo(1);
            page.setPageSize(50);
            searchBean.setPage(page);
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

    private List<SortBean> builderSortBeans(Sorter... args) {
        List<SortBean> ret = new ArrayList<SortBean>();
        for (int i = 0; i < args.length; i++) {
            Sorter sorter = args[i];
            if (sorter == null || sorter.getSortFeild() == null) {
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

    private Map<String, List<Ranger>> handleFilterInf(SearchBean searchBean, Integer conVersion, String searchId, Map<String, String> propertyFilter, Map<String, Ranger> rangefileter) {
        Map<String, List<Ranger>> filterRangInf = new HashMap();
        if (null != rangefileter && rangefileter.size() > 0) {
            for (String tbField : rangefileter.keySet()) {
                Ranger ranger = rangefileter.get(tbField);
                String fieldName = Translator.getInstance().getFieldName(searchId, tbField);
                if (StringUtils.isBlank(fieldName)) {
                    fieldName = tbField;
                }
                List<Ranger> rangers = new LinkedList();
                rangers.add(ranger);
                filterRangInf.put(fieldName, rangers);
            }
        }

        String filterField;
        List<Ranger> rangerList = null;
        // 处理filter, 如果是产地和所在地的id, 转成对应的filter, 其他的属性转成searchAttValueId
        if (null != propertyFilter && propertyFilter.size() > 0) {
            for (Map.Entry<String, String> entry : propertyFilter.entrySet()) {
                String field = entry.getKey();
                String values = entry.getValue();
                rangerList = buildRangers(values);
                if (rangerList == null || rangerList.isEmpty()) {
                    continue;
                }
                if ((!searchId.equalsIgnoreCase(SearchIdDef.STORE_SEARCHER)) && (!searchId.equalsIgnoreCase(SearchIdDef.CMS_STORE_SEARCHER))) {
                    if (field.equals(Constant.ATTR_PRODUCTION_ID.toString())) {
                        //产地对应过滤条件
                        filterField = Constant.ATTR_PRODUCTION_FIELD;
                    } else if (field.equals(Constant.ATTR_LOCALITY_ID.toString())) {
                        //所在地对应过滤条件
                        filterField = Constant.ATTR_LOCALITY_FIELD;
                    } else {
                        //可搜索属性对应过滤条件
                        filterField = Constant.ATTR_SEARCH_FIELD;
                    }
                } else {
                    filterField = Translator.getInstance().getFieldName(searchId, field);
                    if (StringUtils.isBlank(filterField)) {
                        filterField = field;
                    }
                }
                filterRangInf.put(filterField, rangerList);
            }
        }

        if (searchBean.getParamsBean() instanceof SearchFactor) {
            SearchFactor factor = (SearchFactor) searchBean.getParamsBean();
            if (factor.getGoodsId() != null) {
                filterRangInf.put("id", create(factor.getGoodsId()));
            }
            if (factor.getSaleType() != null) {
                filterRangInf.put("saleType", create(factor.getSaleType()));
            }
            if (factor.getStoreId() != null) {
                filterRangInf.put("storeId", create(factor.getStoreId()));
            }
            if (factor.getMinWholesale() != null) {
                filterRangInf.put("minNum", create(0, factor.getMinWholesale()));
            }
            if (factor.getCategory() != null) {
                filterRangInf.put("cid", create(factor.getCategory()));
            }
            if (factor.getStatus() != null && factor.getStatus().length > 0) {
                List<Ranger> rangers = new LinkedList();
                for (int status : factor.getStatus()) {
                    rangers.add(createRanger(status, status));
                }
                filterRangInf.put("status", rangers);
            } else {
                filterRangInf.put("status", create(Constant.GOODS_STATUS_SALE));
            }
            if (factor.getSellerId() != null) {
                filterRangInf.put("sellerId", create(factor.getSellerId()));
            }
            if (factor.getCustomCategoryId() != null) {
                filterRangInf.put("customCategoryId", create(factor.getCustomCategoryId()));
            }
        }

        return filterRangInf;
    }

    /**
     * 构造过滤属性条件
     *
     * @param values
     * @return
     */
    public List<Ranger> buildRangers(String values) {
        if (values == null) {
            return null;
        }
        String[] filterValues = values.split(",");
        List<Ranger> rangerList = new ArrayList<>(filterValues.length);
        Ranger<String> ranger;
        for (String filterValue : filterValues) {
            ranger = new Ranger();
            ranger.setLowerValue(filterValue);
            ranger.setUpperValue(filterValue);
            rangerList.add(ranger);
        }
        return rangerList;
    }

    protected List<Ranger> create(Object value) {
        return create(value, value);
    }

    protected List<Ranger> create(Object lowerValue, Object UpperValue) {
        List<Ranger> rangers = new LinkedList();
        rangers.add(createRanger(lowerValue, UpperValue));
        return rangers;
    }

    protected Ranger createRanger(Object lowerValue, Object UpperValue) {
        Ranger ranger = new Ranger();
        ranger.setLowerValue(lowerValue);
        ranger.setUpperValue(UpperValue);
        return ranger;
    }
}
