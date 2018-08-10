package com.sparrow.collect.express;

import java.io.Serializable;

/**
 * 关键字上下表达式
 * 包含关键字表达式和类目表达式
 * Created by yaobo on 2014/5/13.
 */
public class KeywordContextExpression implements Serializable {
    private KeywordExpression keywordExpression;

    private CategoryExpression categoryExpression;

    public KeywordContextExpression() {

    }

    public KeywordContextExpression(KeywordExpression keywordExpression, CategoryExpression categoryExpression) {
        this.keywordExpression = keywordExpression;
        this.categoryExpression = categoryExpression;
    }

    public KeywordExpression getKeywordExpression() {
        return keywordExpression;
    }

    public void setKeywordExpression(KeywordExpression keywordExpression) {
        this.keywordExpression = keywordExpression;
    }

    public CategoryExpression getCategoryExpression() {
        return categoryExpression;
    }

    public void setCategoryExpression(CategoryExpression categoryExpression) {
        this.categoryExpression = categoryExpression;
    }
}
