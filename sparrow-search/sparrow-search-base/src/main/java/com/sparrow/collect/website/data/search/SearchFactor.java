package com.sparrow.collect.website.data.search;

import com.sparrow.collect.website.query.Pagination;
import com.sparrow.collect.website.query.Ranger;

import java.util.Map;

/**
 * 搜索商品条件封装类
 *
 * @author zhaoYuan
 * @version 1.0
 * @created 08-5月-2014 15:46:33
 */
public class SearchFactor {

    /**
     * 个性化搜索时，提供的用户信息
     */
    private UserFactor userFactor = null;
    /**
     * 搜索关键词列表
     */
    private String key;
    /**
     * Key为属性id，value为选中的属性值id，如果有多个属性值，用逗号分隔
     */
    private Map<String, String> filterProperties;

    private Map<String,Ranger> rangeFilter;
    /**
     * 分页信息
     */
    private Pagination pagination = null;
    /**
     * 排序字段
     */
    private Sorter sorter;
    /**
     * 店铺ID
     */
    private Integer storeId = null;
    /**
     * 类目
     */
    private Integer category = null;
    /**
     * 商品ID
     */
    private Long goodsId = null;

    /**
     * 商品类型，自营还是第三方，或者全部.10-自营 20-代销 30-询价
     */
    private Integer saleType;

    /**
     * 起批量
     */
    private Integer minWholesale;

    /**
     * 起批量单位
     */
    private String minWholesaleUnit;

    /**
     * 市场id
     */
    private Integer marketId;

    /**
     * 销售类型, 多个用,分割.10表示在线交易，20表示询价交易
     */
    private String payType;

    /**
     * 商品状态
     */
    private int status[];

    /**
     * 卖家id
     */
    private Long sellerId;

    //店铺所在市场
    private String shopMarketId;
    //店铺来源
    private String shopSource;
    //产地
    private String productionId;
    //所在地
    private String localityId;
    //自定义分类
    private Integer customCategoryId;

    public SearchFactor() {

    }

    public Integer getMarketId() {
        return marketId;
    }

    public void setMarketId(Integer marketId) {
        this.marketId = marketId;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public UserFactor getUserFactor() {
        return userFactor;
    }

    public void setUserFactor(UserFactor userFactor) {
        this.userFactor = userFactor;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, String> getFilterProperties() {
        return filterProperties;
    }

    public void setFilterProperties(Map<String, String> filterProperties) {
        this.filterProperties = filterProperties;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }


    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Map<String, Ranger> getRangeFilter() {
        return rangeFilter;
    }

    public void setRangeFilter(Map<String, Ranger> rangeFilter) {
        this.rangeFilter = rangeFilter;
    }

    public Sorter getSorter() {
        return sorter;
    }

    public void setSorter(Sorter sorter) {
        this.sorter = sorter;
    }

    public Integer getSaleType() {
        return saleType;
    }

    public void setSaleType(Integer saleType) {
        this.saleType = saleType;
    }

    public Integer getMinWholesale() {
        return minWholesale;
    }

    public void setMinWholesale(Integer minWholesale) {
        this.minWholesale = minWholesale;
    }

    public String getMinWholesaleUnit() {
        return minWholesaleUnit;
    }

    public void setMinWholesaleUnit(String minWholesaleUnit) {
        this.minWholesaleUnit = minWholesaleUnit;
    }

    public int[] getStatus() {
        return status;
    }

    public void setStatus(int[] status) {
        this.status = status;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getShopMarketId() {
        return shopMarketId;
    }

    public void setShopMarketId(String shopMarketId) {
        this.shopMarketId = shopMarketId;
    }

    public String getShopSource() {
        return shopSource;
    }

    public void setShopSource(String shopSource) {
        this.shopSource = shopSource;
    }

    public String getProductionId() {
        return productionId;
    }

    public void setProductionId(String productionId) {
        this.productionId = productionId;
    }

    public String getLocalityId() {
        return localityId;
    }

    public void setLocalityId(String localityId) {
        this.localityId = localityId;
    }

    public Integer getCustomCategoryId() {
        return customCategoryId;
    }

    public void setCustomCategoryId(Integer customCategoryId) {
        this.customCategoryId = customCategoryId;
    }
}//end com.dili.dd.searcher.com.dili.dd.searcher.datainterface.domain.goods.SearchFactor