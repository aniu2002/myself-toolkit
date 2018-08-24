package com.sparrow.collect.website.controller;

import com.sparrow.collect.strategy.StrategyArgInfoBuilder;
import com.sparrow.collect.strategy.StrategyManager;
import com.sparrow.collect.strategy.definition.StrategyDefinition;
import com.sparrow.collect.website.Constant;
import com.sparrow.collect.data.result.Result;
import com.sparrow.collect.data.result.SearchResult;
import com.sparrow.collect.data.search.SearchBean;
import com.sparrow.collect.data.search.sort.SortManager;
import com.sparrow.collect.website.filter.FilterManager;
import com.sparrow.collect.website.format.KeywordFormatManager;
import com.sparrow.collect.website.query.Page;
import com.sparrow.collect.website.query.PageAble;
import com.sparrow.collect.search.NRTSearcher;
import com.sparrow.collect.search.NRTSearcherController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yaobo on 2014/12/15.
 */
public class SimpleSearcher implements Searcher {

    private Log log = LogFactory.getLog(SimpleSearcher.class);

    public static Integer QUERY_MIN_NUMBER = 500;

    @Override
    public SearchResult search(SearchBean searchBean) throws SearchException {
        SearchResult searchResult = null;
        NRTSearcher nrt = null;
        try {
            nrt = NRTSearcherController.getSearcher(searchBean.getSearchId());
            long begin = System.currentTimeMillis();

            // 格式化
            String conStr = KeywordFormatManager.getInstance().keywordFormat(searchBean, searchBean.getSearchId(), searchBean.getSearchCondStr());
            log.debug("formatted  key = " + conStr);

            // 过滤
            Filter filter = FilterManager.getInstance().getFilter(searchBean);
            log.debug("filter = " + (filter == null ? null : filter.toString()));

            // 策略
            BooleanQuery query = new BooleanQuery();
            List<StrategyDefinition> strategyBeans = StrategyArgInfoBuilder.getInstance().build(null, conStr, searchBean.getSearchId());
            StrategyManager.getInstance().strategy(strategyBeans, query);
            if (query.clauses().size() == 0) {
                MatchAllDocsQuery queryAll = new MatchAllDocsQuery();
                query.add(queryAll, BooleanClause.Occur.MUST);
            }
//            query = buildQuery(searchBean, query);
            log.debug("query = " + (query == null ? null : query.toString()));

            // 排序
            Sort sort = SortManager.getInstance().getSort(searchBean, searchBean.getSearchId());
            log.debug("sort = " + (sort == null ? null : sort.toString()));

            searchResult = doSearch(nrt.get(), searchBean, query, filter, sort);

            List<String> conStrs = new ArrayList<String>();
            conStrs.add(conStr);
            Result result = new Result(conStrs, 1);
            searchResult.addResult("searchConStr", result);

            //name
            List<String> highlightFieldName = new ArrayList<String>();
            highlightFieldName.add("name");
            result = new Result(highlightFieldName, 1);
            searchResult.addResult("highlightFieldName", result);
            //ResultHandlerManager.getInstance().handled(searchResult, searchBean, searchBean.getSearchId()); // goodslist
            long end = System.currentTimeMillis();
            long wasteTime = end - begin;
            searchResult.setWasteTime(wasteTime);
            log.debug("search end, waste time = " + wasteTime + "ms");
            return searchResult;
        } catch (Exception e) {
            if (searchResult == null) {
                searchResult = new SearchResult();
            }
            searchResult.setSuccess(false);
            throw new SearchException("搜索商品出错", e);
        } finally {
            NRTSearcherController.release(nrt);
        }
    }

    public SearchResult doSearch(IndexSearcher searcher, SearchBean searchBean, Query query, Filter filter, Sort sort) throws Exception {
        PageAble page = searchBean.getPage();
        int start = (page.getPageNo() - 1) * page.getPageSize();
        int end = start + page.getPageSize();
//        int maxNum = MAX_DOC_NUMBER * pagination.getPageNo();
        int maxNum = page.getPageSize() * page.getPageNo();

        //每次最小查询500个商品, 用于反推类目, 属性.
        maxNum = maxNum < QUERY_MIN_NUMBER ? QUERY_MIN_NUMBER : maxNum;

        long beginTime = System.currentTimeMillis();
        TopDocs topDocs = searcher.search(query, filter, maxNum, sort);
        int total = topDocs.totalHits;
        List<Document> datum = new ArrayList<>();
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        if (start < total) {
            end = end > total ? total : end;
            for (int i = start; i < end; i++) {
                datum.add(searcher.doc(scoreDocs[i].doc));
            }
        }

        maxNum = maxNum > total ? total : maxNum;
        int[] docs = new int[maxNum];
        for (int i = 0; i < maxNum; i++) {
            docs[i] = scoreDocs[i].doc;
        }

        long endTime = System.currentTimeMillis();
        log.debug("lucene search, waste time = " + (endTime - beginTime) + "ms");


        SearchResult searchResult = new SearchResult();
        Result result = new Result(datum, total);
        searchResult.addResult(Constant.DOCUMENTS_OF_QUERIED, result);

        List docList = new ArrayList();
        docList.add(docs);
        Result docResult = new Result(docList, docList.size());
        searchResult.addResult("docids", docResult);

        Page p = new Page();
        p.setPageSize(page.getPageSize());
        p.setTotal(total);
        p.setPageNo(page.getPageNo());

        result = new Result(Arrays.asList(p), 1);
        searchResult.addResult("pag", result);
        return searchResult;
    }
}
