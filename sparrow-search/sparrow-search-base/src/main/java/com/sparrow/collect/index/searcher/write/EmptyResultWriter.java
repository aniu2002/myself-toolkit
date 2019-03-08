package com.sparrow.collect.index.searcher.write;

import com.sparrow.collect.index.searcher.PageResult;
import com.sparrow.collect.index.searcher.ResultWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;

import java.io.IOException;

/**
 * author: Yzc
 * - Date: 2019/3/6 19:51
 */
@Slf4j
public class EmptyResultWriter implements ResultWriter {
    @Override
    public void writeRow(Document document) {

    }

    @Override
    public void writePageHeader(PageResult page) {

    }

    @Override
    public void close() throws IOException {

    }
}
