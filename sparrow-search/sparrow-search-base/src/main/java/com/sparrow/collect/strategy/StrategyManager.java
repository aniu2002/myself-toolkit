package com.sparrow.collect.strategy;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.strategy.definition.StrategyDefinition;
import com.sparrow.collect.website.Configs;
import com.sparrow.collect.website.SearchConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;

import java.util.*;

/**
 * Created by yangtao on 2015/12/22.
 */
public class StrategyManager {
    private Log log = LogFactory.getLog(StrategyManager.class);
    //各index对应搜索策略
    private Map<String, List<FieldStrategy>> fieldStrategies;
    //配置信息
    private SearchConfig config;

    private static StrategyManager instance = new StrategyManager();

    private StrategyManager() {
        this.config = Configs.getConfig();
        this.fieldStrategies = new HashMap();
    }

    public static StrategyManager getInstance() {
        return instance;
    }

    public void strategy(List<StrategyDefinition> strategyBeans, BooleanQuery query) throws Exception {
        if (null == strategyBeans || strategyBeans.size() < 1)
            return;
        if (query == null)
            query = new BooleanQuery();
        for (StrategyDefinition strategyBean : strategyBeans) {
            IStrategy strategy = strategyBean.getStrategy();
            strategy.parse(strategyBean, query);
        }
    }

    public void init() {
        String[] searchTypes = getSearchTypes();
        if (searchTypes == null || searchTypes.length == 0) {
            log.warn("###############无索引搜索策略配置##############");
            return;
        }
        for (String searchType : searchTypes) {
            this.fieldStrategies.put(searchType, buildFieldStrategies(searchType));
        }
    }

    /**
     * 从配置文件中读取索引搜索类别
     *
     * @return
     */
    private String[] getSearchTypes() {
        String messageTypes = config.get("searcher.basesearch.search2.searchType.list");
        if (StringUtils.isBlank(messageTypes)) {
            return null;
        }
        messageTypes = removeBlank(messageTypes);
        return messageTypes.split(",", -1);
    }

    /**
     * 从配置文件中读取索引搜索域
     *
     * @param messageType
     * @return
     */
    private String[] getSearchField(String messageType) {
        String name = String.format("searcher.basesearch.search2.%s.field.list", messageType);
        String fields = config.get(name);
        if (StringUtils.isBlank(fields)) {
            return null;
        }
        fields = removeBlank(fields);
        return fields.split(",", -1);
    }

    /**
     * 从配置中读取field策略
     *
     * @param searchType
     * @param field
     * @return
     */
    private String[] getFieldStrategies(String searchType, String field) {
        String name = String.format("searcher.basesearch.search2.%s.%s.strategy.list", searchType, field);
        String strategies = config.get(name);
        if (StringUtils.isBlank(strategies)) {
            return null;
        }
        strategies = removeBlank(strategies);
        return strategies.split(";", -1);
    }

    /**
     * 从配置中读取fieldOccur值
     *
     * @param searchType
     * @param field
     * @return
     */
    private String getFieldOccur(String searchType, String field) {
        String name = String.format("searcher.basesearch.search2.%s.%s.occur", searchType, field);
        String occur = config.get(name);
        if (StringUtils.isBlank(occur)) {
            name = String.format("searcher.basesearch.search2.%s.field.occur.default", searchType, field);
            occur = config.get(name);
        }
        return occur;
    }

    /**
     * 从配置中读取fieldBoost值
     *
     * @param searchType
     * @param field
     * @return
     */
    private float getFieldBoost(String searchType, String field) {
        String name = String.format("searcher.basesearch.search2.%s.%s.boost", searchType, field);
        float boost = config.getFloat(name, 1.0f);
        return boost;
    }

    private List<FieldStrategy> buildFieldStrategies(String searchType) {
        String[] fields = getSearchField(searchType);
        if (fields == null || fields.length == 0) {
            throw new RuntimeException(String.format("###############[%s]无搜索域##############", searchType));
        }
        List<FieldStrategy> fieldStrategies = new ArrayList();
        for (String field : fields) {
            FieldStrategy fieldStrategy = new FieldStrategy();
            fieldStrategy.setFieldName(field);
            fieldStrategy.setStrategies(buildStrategies(searchType, field));
            fieldStrategy.setOccur(BooleanClause.Occur.valueOf(getFieldOccur(searchType, field)));
            fieldStrategy.setFieldBoost(getFieldBoost(searchType, field));
            fieldStrategies.add(fieldStrategy);
        }
        return fieldStrategies;
    }

    private List<StrategyBean> buildStrategies(String searchType, String field) {
        String[] strategies = getFieldStrategies(searchType, field);
        if (strategies == null || strategies.length == 0) {
            throw new RuntimeException(String.format("###############[%s:%s]无搜策略##############", searchType, field));
        }
        List<StrategyBean> strategyBeans = new LinkedList();
        for (String strategy : strategies) {
            StrategyBean strategyBean = parse(strategy);
            strategyBeans.add(strategyBean);
        }
        return strategyBeans;
    }

    /**
     * 格式:
     * [strategy,analyzer,occur,boost(?),?...]
     *
     * @param expr
     * @return
     */
    private StrategyBean parse(String expr) {
        int length = expr.length();
        assert length > 2;
        expr = expr.substring(1, length - 1);
        String[] params = expr.split(",", 5);
        assert params.length >= 3;
        PcStrategy strategy = QueryStrategyManager.getInstance().getStrategy(params[0]);
        if (strategy == null) {
            throw new RuntimeException("参数错误, MUST BE " + QueryStrategyManager.getInstance().toString());
        }
        IAnalyze analyze = AnalyzerManager.getInstance().getAnalyzer(params[1]);
        if (analyze == null) {
            throw new RuntimeException("参数错误, MUST BE " + AnalyzerManager.getInstance().toString());
        }
        BooleanClause.Occur occur = BooleanClause.Occur.valueOf(params[2]);

        StrategyBean strategyBean = new StrategyBean();
        strategyBean.setStrategy(strategy);
        strategyBean.setAnalyze(analyze);
        strategyBean.setOccur(occur);
        if (params.length >= 4) {
            strategyBean.setBoost(Float.valueOf(params[3]));
        }
        return strategyBean;
    }

    private String removeBlank(String value) {
        return value.replaceAll("\\s+", "");
    }

    public List<FieldStrategy> getFieldStrategy(String searchType) {
        return this.fieldStrategies.get(searchType);
    }

}
