package com.sparrow.collect.space;

import com.sparrow.collect.website.SearchConfig;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;

import java.io.IOException;

/**
 * NRT索引空间.
 * 依赖于IndexWriter的线程安全,本身不做线程安全处理.
 * Created by yaobo on 2014/11/11.
 */
public interface IndexSpace {

    String getName();

    String getAlias();

    void commit() throws IOException;

    void merge() throws IOException;

    IndexSearcher getSearcher() throws IOException;

    IndexReader getReader() throws IOException;

    void release(IndexSearcher searcher) throws IOException;

    void add(Document... docs) throws IOException;

    void update(Document... docs) throws IOException;

    void delete(String... ids) throws IOException;

    void close() throws IOException;

    String getIndexPath();

    SearchConfig getConfig();
}
