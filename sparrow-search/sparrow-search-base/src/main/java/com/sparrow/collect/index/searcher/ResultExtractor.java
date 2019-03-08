package com.sparrow.collect.index.searcher;

import com.sparrow.collect.index.searcher.extractor.MapResultExtractor;
import org.apache.lucene.document.Document;

/**
 * author: Yzc
 * - Date: 2019/3/6 17:33
 */

public interface ResultExtractor<T> {
    ResultExtractor MAP_EXTRACTOR = new MapResultExtractor();

    T extract(Document document);
}
