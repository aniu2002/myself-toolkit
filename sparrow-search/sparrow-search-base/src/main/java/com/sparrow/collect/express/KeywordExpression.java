package com.sparrow.collect.express;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * 关键字表达式
 * [group1:[f1,f2],group2:[f3,f4]]
 * Created by yaobo on 2014/5/19.
 */
public class KeywordExpression {
    private List<GroupExpression> groupExpressions;

    private Integer weight;

    public KeywordExpression() {
        weight = KeywordConst.MAX_STRATEGY_WEIGHT;
        groupExpressions = new ArrayList<GroupExpression>();
    }

    public KeywordExpression(Integer weight) {
        if (weight == null) {
            weight = KeywordConst.DEFAULT_KEYWORD_WEIGHT;
        }
        this.weight = weight;
        groupExpressions = new ArrayList<GroupExpression>();
    }

    public List<GroupExpression> getGroupExpressions() {
        return groupExpressions;
    }

    public void setGroupExpressions(List<GroupExpression> groupExpressions) {
        this.groupExpressions = groupExpressions;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String toJsonExpression() {
        return JSON.toJSONString(this);
    }
}
