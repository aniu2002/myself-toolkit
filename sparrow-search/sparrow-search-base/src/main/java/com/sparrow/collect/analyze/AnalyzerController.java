package com.dili.dd.searcher.basesearch.common.analyze;

import com.dili.dd.searcher.basesearch.common.config.ConfigIniter;
import com.dili.dd.searcher.basesearch.common.config.Contants;
import com.dili.dd.searcher.basesearch.common.exception.ConfigUpdateException;
import com.dili.dd.searcher.basesearch.common.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.FormatAnalyzerWapper;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <B>Description</B> TODO <br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 *
 * @author tanghongjun
 * @createTime 2014年5月22日 下午7:24:08
 */
public class AnalyzerController extends ConfigIniter {

    private Map<String, Analyzer> analyzeMap = new ConcurrentHashMap<String, Analyzer>();
    private Map<String, Analyzer> fieldAnalyzes = new ConcurrentHashMap<String, Analyzer>();
    private Log log = LogFactory.getLog(AnalyzerController.class);
    private static AnalyzerController aController = new AnalyzerController();

    {
        analyzeMap.put("whitespace", new WhitespaceAnalyzer(Version.LUCENE_46));
        analyzeMap.put("standard", new StandardAnalyzer(Version.LUCENE_46));
    }

    private AnalyzerController() {
        if (aController != null) {
            try {
                throw new Exception("duplicate instance create error!"
                        + AnalyzerController.class.getName());
            } catch (Exception e) {
                log.warn("duplicate instance create error!"
                        + AnalyzerController.class.getName());
            }
        }
    }

    @Override
    public void parserConf(Configuration config) throws IOException {
        String[] analyzers = config.getStrings(Contants.getStringByArray(new String[]{Contants.SEARCH_PREFIX, Contants.SEARCH_ANALYZER_LIST}));
        if (null == analyzers) {
            throw new ConfigUpdateException(new StringBuilder().append("analyzers: ").append(Contants.SEARCH_ANALYZER_LIST).append("not null....").toString());
        }
        Analyzer analyzer = null;
        log.info(Contants.getStringByArray(new String[]{Contants.SEARCH_PREFIX, Contants.SEARCH_ANALYZER_LIST}) + " values=" + Arrays.asList(analyzers));
        for (String analy : analyzers) {
            analyzer = config
                    .getInstances(Contants.getStringByArray(new String[]{Contants.SEARCH_PREFIX, Contants.SEARCH_ANALYZER_PREFIX, analy, Contants.CLASS_TAG}), Analyzer.class)
                    .get(0);
            analyzeMap.put(analy, analyzer);
            log.info(new StringBuffer().append(analy).append(
                    analyzer.getClass().getName()));
        }

        StopWordsHandler handler = new StopWordsHandler();
        for (Analyzer analy : analyzeMap.values()) {
            handler.handleStopWords(analy);
        }

        parserFieldAnalyzed(config);
    }

    protected void parserFieldAnalyzed(Configuration config) {
        String[] searchIDs = getSearchIDs(config);
        PerFieldAnalyzerWrapper pfaw = null;
        Map<String, Analyzer> anaMap;
        for (String seach : searchIDs) {
            anaMap = new HashMap<String, Analyzer>();
            String[] ifields = config.getStrings(new StringBuilder().append("searcher.basesearch.").append(seach).append(".ifield.list").toString());
            for (String ifield : ifields) {
                String ifieldAnalyName = config.get(new StringBuilder().append("searcher.basesearch.").append(seach).append(".ifield.").append(ifield).append(".analy.name").toString());
                if (!StringUtil.isNullOrEmpty(ifieldAnalyName)) {
                    //modify by yb: 包装为需要对数据进行格式化过滤
                    Analyzer analyzer = new FormatAnalyzerWapper(analyzeMap.get(ifieldAnalyName));
                    anaMap.put(ifield, analyzer);
                }
            }
            pfaw = new PerFieldAnalyzerWrapper(analyzeMap.get("default"), anaMap);
            fieldAnalyzes.put(seach, pfaw);
        }
    }

    public static AnalyzerController getController() {
        return aController;
    }

    public Analyzer getSearchAnalyzer(String searchID) {
        return fieldAnalyzes.get(searchID);
    }

    public Analyzer getAnalyzer(String analyzeName) {
        return analyzeMap.get(analyzeName);
    }
}
