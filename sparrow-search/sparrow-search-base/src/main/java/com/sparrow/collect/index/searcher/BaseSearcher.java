package com.sparrow.collect.index.searcher;

import com.sparrow.collect.index.space.IndexSpacer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * author: Yzc
 * - Date: 2019/3/6 16:47
 */

public class BaseSearcher implements SearchService {
    private List<IndexSpacer> indexSpacers = new LinkedList<>();
    private IndexSearcher indexSearcher;

    public BaseSearcher(IndexSpacer indexSpacers) {
        this.indexSpacers.add(indexSpacers);
        this.indexSearcher = this.initializeSearcher(1);
    }

    public BaseSearcher(List<IndexSpacer> indexSpacers) {
        if (CollectionUtils.isNotEmpty(indexSpacers)) {
            indexSpacers.forEach(t -> this.indexSpacers.add(t));
        }
        this.indexSearcher = this.initializeSearcher(indexSpacers.size());
    }

    private IndexSearcher initializeSearcher(int size) {
        if (CollectionUtils.isEmpty(this.indexSpacers)) {
            return null;
        }
        List<IndexReader> readers = new ArrayList<>(size);
        this.indexSpacers.forEach(t -> readers.add(t.getReader()));
        MultiReader multiReader = new MultiReader(readers.toArray(new IndexReader[readers.size()]));
        return new IndexSearcher(multiReader);
    }

    private TopDocs search(IndexSearcher searcher, Query query, Filter filter, Sort sort, int max) throws IOException {
        TopDocs topDocs;
        if (filter != null && sort != null) {
            topDocs = searcher.search(query, filter, max, sort);
        } else if (filter != null) {
            topDocs = searcher.search(query, filter, max);
        } else if (sort != null) {
            topDocs = searcher.search(query, max, sort);
        } else {
            topDocs = searcher.search(query, max);
        }
        return topDocs;
    }

    private IndexSearcher getSearcher() {
        return this.indexSearcher;
    }

    @Override
    public void searchAndWrite(Query query, Filter filter, Sort sort, ResultWriter resultWriter, PageAble pageAble) throws IOException {
        PageResult result;
        int max = 200;
        int start = 0;
        int end = 200;
        if (pageAble == null) {
            result = new PageResult(1, 200);
        } else {
            int page = pageAble.getPage();
            int size = pageAble.getSize();
            if (page < 1) {
                page = 1;
            }
            if (size < 2) {
                size = 20;
            }
            end = page * size;
            start = (page - 1) * size;
            result = new PageResult(page, size);
            max = page * size;
        }
        IndexSearcher searcher = this.getSearcher();
        TopDocs topDocs = this.search(searcher, query, filter, sort, max);
        result.setTotal(topDocs.totalHits);
        resultWriter.writePageHeader(result);
        ScoreDoc[] scores = topDocs.scoreDocs;
        for (int i = start; i < end; i++) {
            resultWriter.writeRow(searcher.doc(scores[i].doc));
        }
        resultWriter.close();
    }

    @Override
    public void searchAndWrite(Query query, Filter filter, Sort sort) throws IOException {
        this.searchAndWrite(query, filter, sort, ResultWriter.DEFAULT_WRITER, null);
    }

    @Override
    public void searchAndWrite(Query query, Filter filter, Sort sort, ResultWriter resultWriter) throws IOException {
        this.searchAndWrite(query, filter, sort, resultWriter, null);
    }

    @Override
    public PageResult search(Query query, Filter filter, Sort sort) throws IOException {
        return this.search(query, filter, sort, null, null);
    }

    @Override
    public PageResult search(Query query, Filter filter, Sort sort, ResultExtractor resultExtractor) throws IOException {
        return this.search(query, filter, sort, resultExtractor, null);
    }

    @Override
    public PageResult search(Query query, Filter filter, Sort sort, ResultExtractor resultExtractor, PageAble pageAble) throws IOException {
        PageResult result;
        int max = 200;
        int start = 0;
        int end = 200;
        if (pageAble == null) {
            result = new PageResult(1, 200);
        } else {
            int page = pageAble.getPage();
            int size = pageAble.getSize();
            if (page < 1) page = 1;
            if (size < 2) size = 20;
            max = page * size;
            start = (page - 1) * size;
            end = page * size;
            result = new PageResult(page, size);
        }
        IndexSearcher searcher = this.getSearcher();
        TopDocs topDocs = this.search(searcher, query, filter, sort, max);
        result.setTotal(topDocs.totalHits);
        ScoreDoc[] scores = topDocs.scoreDocs;
        List<Object> rows = new LinkedList<>();
        for (int i = start; i < end; i++) {
            Document doc = searcher.doc(scores[i].doc);
            rows.add(extractDocument(doc, resultExtractor));
        }
        result.setRows(rows);
        return result;
    }

    private Object extractDocument(Document document, ResultExtractor resultExtractor) {
        ResultExtractor extractor = resultExtractor;
        if (extractor == null)
            extractor = ResultExtractor.MAP_EXTRACTOR;
        return extractor.extract(document);
    }
}
