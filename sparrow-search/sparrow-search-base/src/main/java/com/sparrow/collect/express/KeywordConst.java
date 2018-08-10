package com.sparrow.collect.express;

/**
 * 所有关键字分析常量定义在此处
 * Created by yaobo on 2014/5/14.
 */
public class KeywordConst {
    /**
     * 默认策略间的权重
     */
    public static Integer DEFAULT_STRATEGY_WEIGHT = Integer.valueOf(1);

    /**
     * 默认关键字权重
     */
    public static Integer DEFAULT_KEYWORD_WEIGHT = Integer.valueOf(1);

    /**
     * 默认类目权重
     */
    public static Integer DEFAULT_CATEGORY_WEIGHT = Integer.valueOf(1);

    /**
     * 默认策略间的权重
     */
    public static Integer MAX_STRATEGY_WEIGHT = Integer.valueOf(100);

    /**
     * 关键字最大权重
     */
    public static Integer MAX_KEYWORD_WEIGHT = Integer.valueOf(100);

    /**
     * 默认类目, 用于客户端未提交类目信息时使用
     */
    public static Long DEFAULT_CATEGORY_KEY = Long.valueOf(-1);

}
