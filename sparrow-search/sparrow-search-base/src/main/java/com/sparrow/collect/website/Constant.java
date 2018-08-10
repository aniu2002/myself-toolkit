package com.sparrow.collect.website;

/**
 * Created by yaobo on 2014/6/30.
 */
public class Constant {
    public static Integer ATTR_PRODUCTION_ID = -1;
    public static Integer ATTR_DELIVERY_ID = -3;

    public static String ATTR_PRODUCTION_FIELD = "productionAreas";
    public static String ATTR_DELIVERY_FIELD = "deliveryAddrId";

    public static String GOODS_STATUS_DELETE = "-1";

    public static String GOODS_STATUS_SALE = "3";

    public static String CATEGORY_STATUS_DELETE = "-1";

    public static String CATEGORY_STATUS_NORMAL = "1";

    public static String CATEGORY_SHOW_STATUS_ACTIVE = "1";

    public static String CATEGORY_LEVEL_THREE = "3";

    public static String CATEGORY_LEVEL_ONE  = "1";

    //商品所在地属性ID
    public static Integer ATTR_LOCALITY_ID = -5;
    //商品所在地属性索引字段
    public static String ATTR_LOCALITY_FIELD = "localities";
    //商品可搜索属性索引字段
    public static String ATTR_SEARCH_FIELD = "searchAttValueId";

    //表示从索引中查询到的document
    public static final String DOCUMENTS_OF_QUERIED = "searchDocs";
    //分页查询对象
    public static final String PAGINATION_OF_QUERIED = "pagination";
}
