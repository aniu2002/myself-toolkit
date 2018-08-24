package com.sparrow.collect.strategy;

import com.sparrow.collect.strategy.definition.StrategyDefinition;
import org.apache.lucene.search.BooleanQuery;


public interface IStrategy {
    void parse(StrategyDefinition definition, BooleanQuery bq) throws Exception;
}
