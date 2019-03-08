package com.sparrow.collect.index.searcher;

import com.sparrow.collect.index.searcher.write.EmptyResultWriter;
import org.apache.lucene.document.Document;

import java.io.Closeable;

/**
 * author: Yzc
 * - Date: 2019/3/6 17:33
 */

public interface ResultWriter extends Closeable {
    ResultWriter DEFAULT_WRITER = new EmptyResultWriter();

    void writeRow(Document document);

    void writePageHeader(PageResult page);
}
