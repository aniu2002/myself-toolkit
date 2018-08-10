package com.sparrow.collect.website.query;

import com.sparrow.collect.website.SearchConfig;
import com.sparrow.collect.website.data.search.sort.SortBean;
import com.sparrow.collect.website.filter.FilterBean;

import java.util.List;
import java.util.Map;

/**
 * Created by yaobo on 2014/6/10.
 */
public class SearchBean<T> {

    /**
     * 分页
     */
    protected Pagination pagination;

    /**
     * filter
     */
    protected FilterBean filterBean;

    /**
     * 搜索业务id
     */
    protected String searchId;

    /**
     * 搜索条件字符串
     */
    protected String searchCondStr;

    /**
     * 排序beans
     */
    protected List<SortBean> sortBeans;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户签名
     */
    private String userSign;

    //================================================================================================
    //已选择的类目
    private Integer category;

    //根据关键字推断出的类目
    private Integer inferCategory;

    //本次商品搜索查询出的doc
    private int[] goodsDocs;

    //市场
    private Integer market;

    //属性项过滤
    private Map<String, String> filterProperties;

    private T paramsBean;


    /**
     * 已选中的类目, 不作为查询参数
     */
    private String selectedCategories;

    /**
     * 当前文本版本号
     */
    private Integer confVersionNo = 1;

    private SearchConfig searchConfiguration;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserSign() {
        return userSign;
    }

    public void setUserSign(String userSign) {
        this.userSign = userSign;
    }

    public SearchConfig getSearchConfiguration() {
        return searchConfiguration;
    }

    public void setSearchConfiguration(SearchConfig searchConfiguration) {
        this.searchConfiguration = searchConfiguration;
    }

    public Integer getConfVersionNo() {
        return confVersionNo;
    }

    public void setConfVersionNo(Integer confVersionNo) {
        this.confVersionNo = confVersionNo;
    }

    public String getSearchCondStr() {
        return searchCondStr;
    }

    public void setSearchCondStr(String searchCondStr) {
        this.searchCondStr = searchCondStr;
    }


    public FilterBean getFilterBean() {
        return filterBean;
    }

    public void setFilterBean(FilterBean filterBean) {
        this.filterBean = filterBean;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }


    public T getParamsBean() {
        return paramsBean;
    }

    public void setParamsBean(T paramsBean) {
        this.paramsBean = paramsBean;
    }

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Integer getInferCategory() {
        return inferCategory;
    }

    public void setInferCategory(Integer inferCategory) {
        this.inferCategory = inferCategory;
    }

    public Map<String, String> getFilterProperties() {
        return filterProperties;
    }

    public void setFilterProperties(Map<String, String> filterProperties) {
        this.filterProperties = filterProperties;
    }

    public List<SortBean> getSortBeans() {
        return sortBeans;
    }

    public void setSortBeans(List<SortBean> sortBeans) {
        this.sortBeans = sortBeans;
    }

    public Integer getMarket() {
        return market;
    }

    public void setMarket(Integer market) {
        this.market = market;
    }

    public int[] getGoodsDocs() {
        return goodsDocs;
    }

    public void setGoodsDocs(int[] goodsDocs) {
        this.goodsDocs = goodsDocs;
    }

    public String getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(String selectedCategories) {
        this.selectedCategories = selectedCategories;
    }
}
