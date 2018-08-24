package com.sparrow.collect.strategy;

import com.sparrow.collect.strategy.support.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangtao on 2015/12/22.
 * query生成策略管理
 */
public class QueryStrategyManager {
    Map<String, PcStrategy> strategies;

    private static QueryStrategyManager instance = new QueryStrategyManager();

    private QueryStrategyManager(){}

    public void init() {
        if(strategies == null) {
            strategies = new HashMap();
        }
        strategies.put("FuzzyMatch1Strategy", new FuzzyMatch1Strategy());
        strategies.put("FuzzyMatchStrategy", new FuzzyMatchStrategy());
        strategies.put("MatchAllStrategy", new MatchAllStrategy());
        strategies.put("MatchAllWithSynonymsStrategy", new MatchAllWithSynonymsStrategy());
        strategies.put("MatchAnyStrategy", new MatchAnyStrategy());
        strategies.put("MatchAnyWithSynonymsStrategy", new MatchAnyWithSynonymsStrategy());
        strategies.put("MatchNotAllStrategy", new MatchNotAllStrategy());
        strategies.put("MatchNotAllWithSynonymsStrategy", new MatchNotAllWithSynonymsStrategy());
        strategies.put("MatchOriginStrategy", new MatchOriginStrategy());
        strategies.put("PrefixMatchStrategy", new PrefixMatchStrategy());
        //特殊域
        strategies.put("LengthFuzzyMatch1Strategy", new LengthFuzzyMatch1Strategy());
        strategies.put("MatchAnyWithSelfStrategy", new MatchAnyWithSelfStrategy());
    }

    public static QueryStrategyManager getInstance() {
        return instance;
    }

    public PcStrategy getStrategy(String key) {
        return strategies.get(key);
    }

    public String toString() {
        if(strategies == null || strategies.isEmpty()) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        for(String name : strategies.keySet()) {
            buffer.append(name).append(", ");
        }
        return buffer.substring(0, buffer.length()-2);
    }
}
