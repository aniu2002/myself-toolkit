package com.sparrow.collect.index.searcher;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import java.io.IOException;

/**
 * author: Yzc
 * - Date: 2019/3/6 16:41
 */

public interface SearchService {

    PageResult search(Query query, Filter filter, Sort sort, ResultExtractor resultExtractor, PageAble pageAble) throws IOException;

    PageResult search(Query query, Filter filter, Sort sort) throws IOException;

    PageResult search(Query query, Filter filter, Sort sort, ResultExtractor resultExtractor) throws IOException;

    void searchAndWrite(Query query, Filter filter, Sort sort, ResultWriter resultWriter, PageAble pageAble) throws IOException;

    void searchAndWrite(Query query, Filter filter, Sort sort) throws IOException;

    void searchAndWrite(Query query, Filter filter, Sort sort, ResultWriter resultWriter) throws IOException;

}
