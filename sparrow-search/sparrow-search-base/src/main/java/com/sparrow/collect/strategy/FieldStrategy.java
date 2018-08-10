package com.sparrow.collect.strategy;

import org.apache.lucene.search.BooleanClause;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yangtao on 2015/12/22.
 */
public class FieldStrategy {
    //域名
    private String fieldName;
    //域值
    private String fieldValue;
    //域默认查询权重因子
    private float fieldBoost = 1.0f;
    //域之间的逻辑查询
    private BooleanClause.Occur occur;
    //域搜索策略
    List<StrategyBean> strategies;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public float getFieldBoost() {
        return fieldBoost;
    }

    public void setFieldBoost(float fieldBoost) {
        this.fieldBoost = fieldBoost;
    }

    public BooleanClause.Occur getOccur() {
        return occur;
    }

    public void setOccur(BooleanClause.Occur occur) {
        this.occur = occur;
    }

    public List<StrategyBean> getStrategies() {
        return strategies;
    }

    public void setStrategies(List<StrategyBean> strategies) {
        this.strategies = strategies;
    }

    public void addStrategy(StrategyBean strategyBean) {
        if(strategies == null) {
            strategies = new LinkedList();
        }
        strategies.add(strategyBean);
    }
}
