package com.sparrow.collect.express;

import com.alibaba.fastjson.JSON;

/**
 * 字段表达式
 * Created by yaobo on 2014/5/19.
 */
public class FieldExpression {
    private String keyword;

    private Integer weight;

    public FieldExpression() {
        weight = KeywordConst.DEFAULT_KEYWORD_WEIGHT;
    }

    public FieldExpression(String keyword, Integer weight) {
        this.keyword = keyword;
        if (weight == null) {
            weight = KeywordConst.DEFAULT_KEYWORD_WEIGHT;
        }
        this.weight = weight;
    }

    public String toJsonExpression() {
        return JSON.toJSONString(this);
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
