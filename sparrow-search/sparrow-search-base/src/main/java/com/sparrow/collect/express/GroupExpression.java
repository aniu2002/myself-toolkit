package com.sparrow.collect.express;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * 组表达式
 * Created by yaobo on 2014/5/19.
 */
public class GroupExpression {

    private String strategy;

    private List<FieldExpression> fieldExpressions;

    private Integer weight;


    public GroupExpression() {
        weight = KeywordConst.DEFAULT_KEYWORD_WEIGHT;
        fieldExpressions = new ArrayList<FieldExpression>();
    }

    public GroupExpression(Integer weight) {
        if (weight == null){
            weight = KeywordConst.DEFAULT_KEYWORD_WEIGHT;
        }
        this.weight = weight;
        fieldExpressions = new ArrayList<FieldExpression>();
    }

    public GroupExpression(String strategy, Integer weight) {
        this.strategy = strategy;
        if (weight == null){
            weight = KeywordConst.DEFAULT_KEYWORD_WEIGHT;
        }
        this.weight = weight;
        fieldExpressions = new ArrayList<FieldExpression>();
    }

    public List<FieldExpression> getFieldExpressions() {
        return fieldExpressions;
    }

    public void setFieldExpressions(List<FieldExpression> fieldExpressions) {
        this.fieldExpressions = fieldExpressions;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String toJsonExpression() {
        return JSON.toJSONString(this);
    }
}
