package com.sparrow.collect.website.controller;

import com.sparrow.collect.strategy.StrategyArgInfoBuilder;
import com.sparrow.collect.strategy.StrategyManager;
import com.sparrow.collect.strategy.definition.StrategyDefinition;
import com.sparrow.collect.website.SearchIdDef;
import com.sparrow.collect.data.result.Result;
import com.sparrow.collect.data.result.SearchResult;
import com.sparrow.collect.data.search.SearchBean;
import com.sparrow.collect.data.search.sort.SortManager;
import com.sparrow.collect.website.filter.FilterManager;
import com.sparrow.collect.website.format.KeywordFormatManager;
import com.sparrow.collect.website.query.PageAble;
import com.sparrow.collect.search.NRTSearcher;
import com.sparrow.collect.search.NRTSearcherController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 商品搜索
 * <p/>
 * 1.如果有关键字, 先从关键字分析系统中分析关键字 2.如果有类目, 取出该类目的相关类目, 如果没有类目, 按关键字找到相关类目.
 * 3.根据类目+关键字查询商品 4.根据类目查询属性项
 * <p/>
 * Created by yaobo on 2014/6/10.
 */
public class GoodsSearcher implements Searcher {
    private Log log = LogFactory.getLog(GoodsSearcher.class);

    public static Integer QUERY_MIN_NUMBER = 500;

    private KeywordFormatManager keywordFormatManager;

    private FilterManager filterManager;

    private StrategyManager strategyManager;


    private SortManager sortManager = null;

//    private DLIndexSearcher searcher;

    public GoodsSearcher() throws Exception {
//        searcher = IndexSearchController.getController().getSearcher(SearchIdDef.GOODS_SEARCHER);
        keywordFormatManager = KeywordFormatManager.getInstance();
        filterManager = FilterManager.getInstance();
        strategyManager = StrategyManager.getInstance();
        sortManager = SortManager.getInstance();
    }

    public SearchResult search(SearchBean searchBean) throws SearchException {
        SearchResult searchResult = null;
        NRTSearcher nrt = null;
        try {
            log.debug("search begin");
            long begin = System.currentTimeMillis();
            nrt = NRTSearcherController.getSearcher(SearchIdDef.GOODS_SEARCHER);
            // 格式化
            String conStr = keywordFormatManager.keywordFormat(searchBean, SearchIdDef.GOODS_SEARCHER, searchBean.getSearchCondStr());
            log.debug("formatted  key = " + conStr);

            // 过滤
            Filter filter = filterManager.getFilter(searchBean);
            log.debug("filter = " + (filter == null ? null : filter.toString()));

            // 策略
            BooleanQuery query = new BooleanQuery();
            List<StrategyDefinition> strategyBeans = StrategyArgInfoBuilder.getInstance().build(null, conStr, SearchIdDef.GOODS_SEARCHER);
            strategyManager.strategy(strategyBeans, query);
            query = buildQuery(searchBean, query);
            log.debug("query = " + (query == null ? null : query.toString()));

            // 排序
            Sort sort = sortManager.getSort(searchBean, SearchIdDef.GOODS_SEARCHER);
            log.debug("sort = " + (sort == null ? null : sort.toString()));

            searchResult = doSearch(searchBean, query, filter, sort, nrt.get());

            //???
            List<String> conStrs = new ArrayList<String>();
            conStrs.add(conStr);
            Result result = new Result(conStrs, 1);
            searchResult.addResult("searchConStr", result);

            //name
            List<String> highlightFieldName = new ArrayList<String>();
            highlightFieldName.add("name");
            result = new Result(highlightFieldName, 1);
            searchResult.addResult("highlightFieldName", result);

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
            nrt = null;
        }
    }

    private BooleanQuery buildQuery(SearchBean searchBean, BooleanQuery booleanQuery) {
        BooleanQuery newQuery = new BooleanQuery();

        //加入原有查询
        if (!booleanQuery.clauses().isEmpty()) {
            newQuery.add(booleanQuery, BooleanClause.Occur.MUST);
        }

        //分类扩展为自己+所有子分类
//        if (searchBean.getCategory() != null) {
//            List<Category> allChildren = BeansFactory.getInstance().getCategoryCache().getAllChildren(searchBean.getCategory());
//            BooleanQuery cidQuery = new BooleanQuery();
//            cidQuery.add(new TermQuery(new Term("cid", searchBean.getCategory().toString())), BooleanClause.Occur.SHOULD);
//            if (allChildren != null) {
//                for (Category child : allChildren) {
//                    cidQuery.add(new TermQuery(new Term("cid", child.getId().toString())), BooleanClause.Occur.SHOULD);
//                }
//            }
//            if (!cidQuery.clauses().isEmpty()) {
//                newQuery.add(cidQuery, BooleanClause.Occur.MUST);
//            }
//        }

        //市场转为提货点, 如果没有对应的提货点, 设置为-1
        if (searchBean.getMarket() != null) {
            Set<Integer> deliveries = Collections.emptySet();//attributeCache.getDeliveryByMarket(searchBean.getMarket());
            BooleanQuery deliveryQuery = new BooleanQuery();
            if (deliveries != null && !deliveries.isEmpty()) {
                for (Integer delivery : deliveries) {
                    deliveryQuery.add(new TermQuery(new Term("deliveryAddrId", delivery.toString())), BooleanClause.Occur.SHOULD);
                }
            } else {
                deliveryQuery.add(new TermQuery(new Term("deliveryAddrId", "-1")), BooleanClause.Occur.SHOULD);
            }
            if (!deliveryQuery.clauses().isEmpty()) {
                newQuery.add(deliveryQuery, BooleanClause.Occur.MUST);
            }
        }

        if (newQuery.clauses().size() == 0) {
            MatchAllDocsQuery query = new MatchAllDocsQuery();
            newQuery.add(query, BooleanClause.Occur.MUST);
        }
        return newQuery;
    }

    public SearchResult doSearch(SearchBean searchBean, Query query, Filter filter, Sort sort, IndexSearcher searcher) throws Exception {
        PageAble pagination = searchBean.getPage();
        int start = (pagination.getPageNo() - 1) * pagination.getPageSize();
        int end = start + pagination.getPageSize();
//        int maxNum = MAX_DOC_NUMBER * pagination.getPageNo();
        int maxNum = pagination.getPageSize() * pagination.getPageNo();

        //每次最小查询500个商品, 用于反推类目, 属性.
        maxNum = maxNum < QUERY_MIN_NUMBER ? QUERY_MIN_NUMBER : maxNum;

        long beginTime = System.currentTimeMillis();
//        TopDocs topDocs = searcher.search(query, filter, maxNum, sort);
        TopDocs topDocs = searchGoods(searchBean, query, filter, maxNum, sort, searcher);
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


        // oldtext fieldname fencifangshi
        SearchResult searchResult = new SearchResult();
        Result result = new Result(datum, total);
        searchResult.addResult("searchDocs", result);

        List docList = new ArrayList();
        docList.add(docs);
        Result docResult = new Result(docList, docList.size());
        searchResult.addResult("docids", docResult);
        // highlight waste pageinfo
        return searchResult;
    }

    public TopDocs searchGoods(SearchBean searchBean, Query query, Filter filter, int maxNum, Sort sort, IndexSearcher searcher) throws IOException {
        //如果商品搜索使用的是默认搜索, 重写为人工干预排序&综合搜索
        if (sort.getSort().length == 1 && SortField.Type.SCORE.equals(sort.getSort()[0].getType())) {
            Query customQuery = null;
            if (searchBean.getCategory() != null) {
                //选中分类时加入人工排序
                customQuery = new GoodsTopQuery(query, searchBean.getUserId(), TopOrderType.CATEGORY, searchBean.getCategory());
                ((GoodsTopQuery) customQuery).setWeight(0.4f);
            } else {
                //未选中分类时采用综合排序
                customQuery = new NewGoodsCompositeQuery(query, searchBean.getUserId());
                ((NewGoodsCompositeQuery) customQuery).setWeight(0.4f);
            }
            return searcher.search(customQuery, filter, maxNum);
        } else {
            return searcher.search(query, filter, maxNum, sort);
        }
    }

//    public TopDocs searchGoods(SearchBean searchBean, Query query, Filter filter, int maxNum, Sort sort, IndexSearcher searcher) throws IOException {
//        //如果商品搜索使用的是默认搜索, 重写为人工干预排序&综合搜索
//        if (sort.getSort().length == 1 && SortField.Type.SCORE.equals(sort.getSort()[0].getType())) {
//            GoodsManualOrderCache goodsManualOrderCache = BeansFactory.getInstance().getGoodsManualOrderCache();
//            ManualOrderScoreComparatorSource source = new ManualOrderScoreComparatorSource("id", goodsManualOrderCache, new ManualOrderScoreComparatorSource.KeyTypeCast<Long>() {
//                @Override
//                public Long cast(String s) {
//                    if (NumberUtils.isNumber(s)) {
//                        return Long.valueOf(s);
//                    }else{
//                        return null;
//                    }
//                }
//            });
//            sort = new Sort(new SortField("", source, true));
//            ManualOrderTopDocsCollector collector = new ManualOrderTopDocsCollector(sort, maxNum);
//            source.setDocOrderScores(collector.getDocOrderMap());
//            log.debug("sort = SCORE " + ", rewrite query, use GoodsCompositeQuery");
//            log.debug("sort = SCORE " + ", rewrite sort, use ManualOrderTopDocsCollector");
//            log.debug("collector = " + collector.toString());
//            log.debug("sort = " + sort.toString());
//            GoodsCompositeQuery compositeQuery = new GoodsCompositeQuery(query, searchBean);
//            searcher.search(compositeQuery, filter, collector);
////            searcher.search(query, filter, collector);
//            return collector.topDocs();
//
////            return searcher.search(query, filter, maxNum);
//        } else {
//            return searcher.search(query, filter, maxNum, sort);
//        }
//    }
}
