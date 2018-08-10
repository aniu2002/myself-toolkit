package com.sparrow.collect.strategy.definition;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.strategy.IStrategy;
import org.apache.lucene.search.BooleanClause;

/**
 * Created by Administrator on 2018/8/10.
 */
public class StrategyDefinition {
    private String searchId;

    private String fieldname;

    private Object fieldvalue;

    private float weight = 0;

    private IAnalyze anlyze = null;

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

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public Object getFieldvalue() {
        return fieldvalue;
    }

    public void setFieldvalue(Object fieldvalue) {
        this.fieldvalue = fieldvalue;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public IAnalyze getAnlyze() {
        return anlyze;
    }

    public void setAnlyze(IAnalyze anlyze) {
        this.anlyze = anlyze;
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
