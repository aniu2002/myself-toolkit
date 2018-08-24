package com.sparrow.collect.strategy.definition;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.strategy.IStrategy;
import org.apache.lucene.search.BooleanClause;

/**
 * Created by Administrator on 2018/8/10.
 */
public class StrategyDefinition {
    private String searchId;

    private String fieldName;

    private Object fieldValue;

    private float weight = 0;

    private IAnalyze analyze = null;

    private int slop = 0;

    private IStrategy strategy = null;

    /**
     * field之间的occur
     */
    private BooleanClause.Occur innerOccur = BooleanClause.Occur.SHOULD;

    /**
     * 与其他field的occur
     */
    private BooleanClause.Occur outerOccur = BooleanClause.Occur.SHOULD;

    public IStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(IStrategy strategy) {
        this.strategy = strategy;
    }

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public IAnalyze getAnalyze() {
        return analyze;
    }

    public void setAnalyze(IAnalyze analyze) {
        this.analyze = analyze;
    }

    public int getSlop() {
        return slop;
    }

    public void setSlop(int slop) {
        this.slop = slop;
    }

    public BooleanClause.Occur getInnerOccur() {
        return innerOccur;
    }

    public void setInnerOccur(BooleanClause.Occur innerOccur) {
        this.innerOccur = innerOccur;
    }

    public BooleanClause.Occur getOuterOccur() {
        return outerOccur;
    }

    public void setOuterOccur(BooleanClause.Occur outerOccur) {
        this.outerOccur = outerOccur;
    }
}
