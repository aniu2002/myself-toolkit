package com.sparrow.collect.top;

/**
 * User: zhaoYuan
 * Date: 14-6-10
 * Time: 下午3:05
 */
public class SKU {
    private String sku;
    private Integer stockNum;
    private Integer minNum;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getStockNum() {
        return stockNum;
    }

    public void setStockNum(Integer stockNum) {
        this.stockNum = stockNum;
    }

    public Integer getMinNum() {
        return minNum;
    }

    public void setMinNum(Integer minNum) {
        this.minNum = minNum;
    }
}
