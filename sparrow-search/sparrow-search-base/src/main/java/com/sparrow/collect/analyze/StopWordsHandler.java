package com.dili.dd.searcher.basesearch.common.analyze;

import com.dili.dd.searcher.common.utils.PropertiesLoader;
import org.ansj.lucene4.AnsjAnalysis;
import org.ansj.lucene4.AnsjIndexAnalysis;
import org.ansj.util.FilterModifWord;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

/**
 * Created by yaobo on 2014/10/30.
 */
public class StopWordsHandler {

    private Log log = LogFactory.getLog(StopWordsHandler.class);

    public static String[] DEFAULT_STOP_WORDS = new String[]{" ", "\t"};

    private PropertiesLoader propertiesLoader = new PropertiesLoader("library.properties");

    public void handleStopWords(Analyzer analyzer){
        // 如果是ansj, 加载停用词
        if (analyzer instanceof AnsjAnalysis) {
            addStopWords((AnsjAnalysis) analyzer);
        }
        if (analyzer instanceof AnsjIndexAnalysis) {
            addStopWords((AnsjIndexAnalysis) analyzer);
        }
    }

    /**
     * ansj加载停用词, 默认的和停用词词典中的
     *
     * @param ansj
     */
    private void addStopWords(AnsjAnalysis ansj) {
        log.info(ansj.toString() + " load stopWords");
        if (ansj.filter == null) {
            ansj.filter = new HashSet<>();
        }
        // load from default
        for (String w : DEFAULT_STOP_WORDS) {
            ansj.filter.add(w);
            FilterModifWord.insertStopWord(w);
        }
        log.info("load default stopWords: " + DEFAULT_STOP_WORDS.length + "条");
        // load from library
        String stopLibrary = propertiesLoader.getProperty("stopLibrary");
        addStopWords(ansj, stopLibrary);
    }

    /**
     * ansj加载停用词, 默认的和停用词词典中的
     *
     * @param ansj
     */
    private void addStopWords(AnsjIndexAnalysis ansj) {
        log.info(ansj.toString() + " load stopWords");
        if (ansj.filter == null) {
            ansj.filter = new HashSet<>();
        }
        // load from default
        for (String w : DEFAULT_STOP_WORDS) {
            ansj.filter.add(w);
            FilterModifWord.insertStopWord(w);
        }
        log.info("load default stopWords: " + DEFAULT_STOP_WORDS.length + "条");
        // load from library
        String stopLibrary = propertiesLoader.getProperty("stopLibrary");
        addStopWords(ansj, stopLibrary);
    }

    private void addStopWords(AnsjIndexAnalysis ansj, String stopLibrary) {
        log.info("stopLibrary = " + stopLibrary);
        if (StringUtils.isBlank(stopLibrary)) {
            return;
        }
        if (ansj.filter == null) {
            ansj.filter = new HashSet<>();
        }
        BufferedReader br = null;
        int count = 0;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(stopLibrary), "UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                try {
                    ansj.filter.add(line);
                    FilterModifWord.insertStopWord(line);
                    count++;
                } catch (Exception ex) {

                }
            }
            log.info("加载停用词词典: " + count + "条");
        } catch (Exception e) {
            log.info("加载停用词词典出错", e);
        } finally {
            IOUtils.closeQuietly(br);
        }
    }

    private void addStopWords(AnsjAnalysis ansj, String stopLibrary) {
        log.info("stopLibrary = " + stopLibrary);
        if (StringUtils.isBlank(stopLibrary)) {
            return;
        }
        if (ansj.filter == null) {
            ansj.filter = new HashSet<>();
        }
        BufferedReader br = null;
        int count = 0;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(stopLibrary), "UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                try {
                    ansj.filter.add(line);
                    FilterModifWord.insertStopWord(line);
                    count++;
                } catch (Exception ex) {

                }
            }
            log.info("加载停用词词典: " + count + "条");
        } catch (Exception e) {
            log.info("加载停用词词典出错", e);
        } finally {
            IOUtils.closeQuietly(br);
        }
    }
}
