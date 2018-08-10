package com.sparrow.collect.express;

/**
 * 类目表达式.
 * 类目ID:权重
 * Created by yaobo on 2014/5/13.
 */
public class CategoryExpression {
    private Long[] categories;

    private Integer[] weights;

    public Long[] getCategories() {
        return categories;
    }

    public void setCategories(Long[] categories) {
        this.categories = categories;
    }

    public Integer[] getWeights() {
        return weights;
    }

    public void setWeights(Integer[] weights) {
        this.weights = weights;
    }
}
