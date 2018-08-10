package com.sparrow.collect.strategy;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.express.KeywordContextExpression;
import com.sparrow.collect.strategy.definition.StrategyDefinition;
import com.sparrow.collect.website.Configs;
import com.sparrow.collect.website.data.search.SearchBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.BooleanClause;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <B>Description</B>策略参数生成 <br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 *
 * @author zhanglin
 * @createTime 2014年6月20日 下午1:56:01
 */
public class StrategyArgInfoBuilder {


    private static StrategyArgInfoBuilder instance = new StrategyArgInfoBuilder();

    private Map<String, Float> fieldsWeight = new ConcurrentHashMap<>();

    private Map<String, Integer> fieldsSlop = new ConcurrentHashMap<>();

    private Map<String, IStrategy> strategyObjects = new ConcurrentHashMap<>(); // 策略对象

    private Map<String, IAnalyze> analyzerObjects = new ConcurrentHashMap<>(); // 分词对象<分词名,分词对象>

    private Map<String, IStrategy> searchStrategy = new ConcurrentHashMap<>();// <searchId+field,策略对象>

    private Map<String, IAnalyze> searchAnalyzer = new ConcurrentHashMap<>(); // <searchId+field,分词器>

    private Map<String, String[]> strategyFields = new ConcurrentHashMap<>();

    private Map<String, BooleanClause.Occur> innerOccurs = new ConcurrentHashMap<>();//field内关系

    private Map<String, BooleanClause.Occur> outerOccurs = new ConcurrentHashMap<>();//field间关系

    private Log log = LogFactory.getLog(StrategyArgInfoBuilder.class);

    public static StrategyArgInfoBuilder getInstance() {
        return instance;
    }

    // searchbaean,isthrowException,configname,
    public void init() throws Exception {

        try {
            handleStrategyFileds();
            // init strategyObjects
            Map<String, String> strategyObjectsClassName = getStrategyObjectsClassName();
            for (String strategyObjectClassNameKey : strategyObjectsClassName.keySet()) {
                String strategyObjectClassName = strategyObjectsClassName.get(strategyObjectClassNameKey);
                Class c = Class.forName(strategyObjectClassName);
                IStrategy strategy = (IStrategy) c.newInstance();
                strategyObjects.put(strategyObjectClassNameKey, strategy);
            }
            // init analyzerObjects
            Map<String, String> analyzerObjectsClassName = getAnalyzerObjectsClassName();
            for (String analyzerObjectClassNameKey : analyzerObjectsClassName.keySet()) {
                String analyzerObjectClassName = analyzerObjectsClassName.get(analyzerObjectClassNameKey);
                Class c = Class.forName(analyzerObjectClassName);
                IAnalyze analyzer = (IAnalyze) c.newInstance();
                analyzerObjects.put(analyzerObjectClassNameKey, analyzer);
            }
            // init searchStrategy
            Map<String, String> fieldsStrategyName = parseAndGetFieldsStrategy();
            for (String indentity : fieldsStrategyName.keySet()) {
                String strategyName = fieldsStrategyName.get(indentity);
                searchStrategy.put(indentity, strategyObjects.get(strategyName));
            }
            // init searchAnalyzer
            Map<String, String> fieldsAnalyzeName = parseAndGetFieldsAnalyze();
            for (String identity : fieldsAnalyzeName.keySet()) {
                String analyzeName = fieldsAnalyzeName.get(identity);
                searchAnalyzer.put(identity, analyzerObjects.get(analyzeName));
            }
            //init fieldsWeight
            parseAndGetFieldsWeight();

            //init occur
            initOccur();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            log.fatal(e);
            throw e;
        }
    }

    private Map<String, String> getStrategyObjectsClassName() {

        Map<String, String> strategyObjectsClassName = new HashMap<>();
        String[] strategyObjectsName = Configs.get("searcher.basesearch.search.strategy.list").split(",");
        for (String strategyObjectName : strategyObjectsName) {
            String strategyObjectClass = Configs.get(String.format("searcher.basesearch.search.strategy.%s.cla", strategyObjectName));
            strategyObjectsClassName.put(strategyObjectName, strategyObjectClass);
        }

        return strategyObjectsClassName;
    }

    private Map<String, String> getAnalyzerObjectsClassName() {

        Map<String, String> analyzerObjectsClassName = new HashMap<>();
        String[] analyzersName = Configs.get("searcher.basesearch.search.analyzer.list").split(",");
        for (String analyzerName : analyzersName) {
            String analyzerClassName = Configs.get(String.format("searcher.basesearch.search.analyzer.%s.cla", analyzerName));
            analyzerObjectsClassName.put(analyzerName, analyzerClassName);
        }

        return analyzerObjectsClassName;
    }

    private Map<String, String> parseAndGetFieldsStrategy() {

        Map<String, String> StrategyClassInfos = new HashMap<>();
        String[] searchIds = Configs.get("searcher.basesearch.search.strategy.searchId.list").split(",");

        if (null != searchIds && searchIds.length > 0) {

            for (String searchId : searchIds) {
                String keyStr = String.format("searcher.basesearch.search.strategy.%s.field.list", searchId);
                String[] fields = Configs.get(keyStr).split(",");
                if (null != fields && fields.length > 0) {
                    for (String field : fields) {
                        String strategynameKeyStr = String.format("searcher.basesearch.search.strategy.%s.field.%s", searchId, field);
                        String strategyname = Configs.get(strategynameKeyStr);
                        if (null == strategyname) {
                            strategynameKeyStr = String.format("searcher.basesearch.search.strategy.%s.field.default", searchId);
                            strategyname = Configs.get(strategynameKeyStr);
                        }
                        StrategyClassInfos.put(searchId + field, strategyname);
                    }
                }
            }
        }
        return StrategyClassInfos;
    }


    private void parseAndGetFieldsWeight() {

        String[] searchIds = Configs.get("searcher.basesearch.search.analyzer.searchId.list").split(",");
        if (null != searchIds && searchIds.length > 0) {
            for (String searchId : searchIds) {
                String keyStr = String.format("searcher.basesearch.search.analyzer.%s.field.list", searchId);
                String[] fields = Configs.get(keyStr).split(",");
                if (null != fields && fields.length > 0) {
                    for (String field : fields) {
                        String wightKeyStr = String.format("searcher.basesearch.search.weight.%s.field.%s", searchId, field);
                        String wightValueStr = Configs.get(wightKeyStr);
                        if (StringUtils.isNotBlank(wightValueStr)) {
                            fieldsWeight.put(searchId + field, Float.parseFloat(StringUtils.trim(wightValueStr)));
                        }
                    }
                }
            }
        }
    }


    private Map<String, String> parseAndGetFieldsAnalyze() {

        Map<String, String> AnalyzeNameInfos = new HashMap<>();
        String[] searchIds = Configs.get("searcher.basesearch.search.analyzer.searchId.list").split(",");
        if (null != searchIds && searchIds.length > 0) {

            for (String searchId : searchIds) {
                String keyStr = String.format("searcher.basesearch.search.analyzer.%s.field.list", searchId);
                String[] fields = Configs.get(keyStr).split(",");
                if (null != fields && fields.length > 0) {
                    for (String field : fields) {
                        String anlyzenameKeyStr = String.format("searcher.basesearch.search.analyzer.%s.field.%s.analy.name", searchId, field);
                        String anlyzename = Configs.get(anlyzenameKeyStr);
                        if (null == anlyzename) {
                            anlyzenameKeyStr = String.format("searcher.basesearch.search.analyzer.%s.field.default.analy.name", searchId);
                            anlyzename = Configs.get(anlyzenameKeyStr);
                        }
                        AnalyzeNameInfos.put(searchId + field, anlyzename);
                    }
                }
            }
        }
        return AnalyzeNameInfos;
    }

    private void initOccur() {
        BooleanClause.Occur defaultOccur = BooleanClause.Occur.SHOULD;
        Map<String, BooleanClause.Occur> occurs = new HashMap<>();
        String[] occurName = Configs.get("searcher.basesearch.search.occur.list").split(",");
        for (String name : occurName) {
            BooleanClause.Occur occur = BooleanClause.Occur.valueOf(name);
            occurs.put(name, occur);
        }

        //全局的默认occur
        String globalDefaultInner = Configs.get("searcher.basesearch.search.occur.inner.default");
        String globalDefaultOuter = Configs.get("searcher.basesearch.search.occur.outer.default");


        String[] searchIds = Configs.get("searcher.basesearch.search.strategy.searchId.list").split(",");
        if (null != searchIds && searchIds.length > 0) {
            for (String searchId : searchIds) {
                //每个searcherId的默认occur
                String defaultInner = Configs.get(String.format("searcher.basesearch.search.occur.inner.%s", searchId), globalDefaultInner);
                String defaultOuter = Configs.get(String.format("searcher.basesearch.search.occur.outer.%s", searchId), globalDefaultOuter);

                String keyStr = String.format("searcher.basesearch.search.strategy.%s.field.list", searchId);
                String[] fields = Configs.get(keyStr).split(",");
                if (fields != null && fields.length > 0) {
                    for (String field : fields) {
                        //每个域的occur
                        String innnerKey = String.format("searcher.basesearch.search.occur.inner.%s.field.%s", searchId, field);
                        String innerValue = Configs.get(innnerKey);
                        innerValue = innerValue == null ? defaultInner : innerValue;
                        BooleanClause.Occur innerOccur = occurs.get(innerValue);
                        innerOccur = innerOccur == null ? defaultOccur : innerOccur;
                        this.innerOccurs.put(searchId + field, innerOccur);

                        String outerKey = String.format("searcher.basesearch.search.occur.outer.%s.field.%s", searchId, field);
                        String outerValue = Configs.get(outerKey);
                        outerValue = outerValue == null ? defaultOuter : outerValue;
                        BooleanClause.Occur outerOccur = occurs.get(outerValue);
                        outerOccur = outerOccur == null ? defaultOccur : outerOccur;
                        this.outerOccurs.put(searchId + field, outerOccur);
                    }
                }
            }
        }

    }

    private List<StrategyDefinition> handleStrategyOnInputSearchStr(String searchId, String conStr) {

        List<StrategyDefinition> ret = new LinkedList<>();
        String[] fieldsName = strategyFields.get(searchId);
        if (null != fieldsName) {
            if (!searchId.equals("类目搜索")) {
                for (String fieldname : fieldsName) {
                    StrategyDefinition strategyBean = new StrategyDefinition();
                    strategyBean.setFieldname(fieldname);
                    strategyBean.setFieldvalue(conStr);
                    strategyBean.setSearchId(searchId);
                    String key = searchId + fieldname;
                    if (fieldsSlop.containsKey(key))
                        strategyBean.setSlop(fieldsSlop.get(key));
                    if (fieldsWeight.containsKey(key))
                        strategyBean.setWeight(fieldsWeight.get(key));
                    strategyBean.setStrategy(searchStrategy.get(key));
                    strategyBean.setAnlyze(searchAnalyzer.get(key));
                    strategyBean.setInnerOccur(innerOccurs.get(key));
                    strategyBean.setOuterOccur(outerOccurs.get(key));
                    ret.add(strategyBean);
                }
            } else {

                // TODO类目搜索

            }
            // todo 如果没有搜索关键字，则只有filter
        }

        if (ret.size() > 0)
            return ret;
        else
            return null;
    }

    private List<StrategyDefinition> handleStrategyOnKeywordExp(String searchId, KeywordContextExpression keywordContextExpression) {

        // TODO
        return null;
    }

    private void handleStrategyFileds() {

        String[] searchIds = Configs.get("searcher.basesearch.search.strategy.searchId.list").split(",");
        if (null != searchIds && searchIds.length > 0) {
            for (String searchId : searchIds) {
                String keyStr = String.format("searcher.basesearch.search.strategy.%s.field.list", searchId);
                if (Configs.get(keyStr) == null) {
                    System.out.println("===" + keyStr);
                }
                String[] fields = Configs.get(keyStr).split(",");
                strategyFields.put(searchId, fields);
            }
        }
    }

    /**
     * this method is TODO
     *
     * @param keywordContextExpression 关键字分析表达式
     * @param conStr                   搜索录入条件
     * @param searchId                 业务ID
     * @createTime 2014年6月20日 下午4:27:35
     * @author zhanglin
     */
    public List<StrategyDefinition> build(SearchBean searchBean, KeywordContextExpression keywordContextExpression, String conStr, String searchId) throws Exception {

        List<StrategyDefinition> strategyBeanList = new LinkedList();
        List<StrategyDefinition> strategyBeanListTmp = handleStrategyOnInputSearchStr(searchId, conStr);
        if (null != strategyBeanListTmp)
            strategyBeanList.addAll(strategyBeanListTmp);
        strategyBeanListTmp = handleStrategyOnKeywordExp(searchId, keywordContextExpression);
        if (null != strategyBeanListTmp)
            strategyBeanList.addAll(strategyBeanListTmp);
        return strategyBeanList;
    }


}
