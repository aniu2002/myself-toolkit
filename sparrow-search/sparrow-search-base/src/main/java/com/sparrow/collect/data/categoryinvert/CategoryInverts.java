package com.sparrow.collect.data.categoryinvert;

/**
 * Created by yaobo on 2014/6/23.
 */
public class CategoryInverts {
    private Integer categoryId;

    private String goods;

    private String store;

    private Integer order;

    public CategoryInverts(Integer categoryId, String goods, String store, Integer order) {
        this.categoryId = categoryId;
        this.goods = goods;
        this.store = store;
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getGoods() {
        return goods;
    }

    public void setGoods(String goods) {
        this.goods = goods;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    @Override
    public String toString() {
        return "CategoryInverts{" +
                "categoryId=" + categoryId +
                ", goods='" + goods + '\'' +
                ", store='" + store + '\'' +
                ", order=" + order +
                '}';
    }
}