package com.sparrow.collect.index.space;

import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.Map;

/**
 * @author: Yzc
 * - Date: 2019/3/6 15:34
 */

public interface IndexService {
    boolean addDocuments(Document... docs) throws IOException;

    boolean deleteDocuments(String... ids) throws IOException;

    boolean updateDocuments(Map<String, Document> idAndDocs) throws IOException;

    boolean updateDocument(String id, Document doc) throws IOException;

    int commit() throws IOException;

    void rollback() throws IOException;
}
