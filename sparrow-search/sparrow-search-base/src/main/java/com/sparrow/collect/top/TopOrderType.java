package com.sparrow.collect.top;

/**
 * Created by yangtao on 2015/11/11.
 */
public enum TopOrderType {
    //分类置顶
    CATEGORY(1),
    //批发市场置顶
    MARKET(2),
    //产地直供置顶
    ORIGIN(3),
    //农户直供置顶
    FARMER(4),
    //同城零售置顶
    LOCAL(5);

    private int orderType;

    TopOrderType(int orderType) {
        this.orderType = orderType;
    }

    public int getOrderType() {
        return orderType;
    }

    public static TopOrderType get(int orderType) {
        TopOrderType[] topOrderTypes = TopOrderType.values();
        for(TopOrderType topOrderType : topOrderTypes) {
            if(orderType == topOrderType.getOrderType()) {
                return topOrderType;
            }
        }
        return null;
    }
}
