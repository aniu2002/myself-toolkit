package org.apache.lucene.search;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对多个indexReader进行search, 同时过滤业务ID相同的doc, 以前一个为准. 需要先search ram, 在search disk.
 * Created by yaobo on 2014/7/11.
 */
@Deprecated
public class MultiIndexSearcher extends IndexSearcher {

    /**
     * ram和disk中有相同业务id的docid映射. key:docid; value:ram(前一次出现)中的docid
     */
    protected ConcurrentHashMap<Integer, Integer> sameDocIdMappingCache = new ConcurrentHashMap();

    public MultiIndexSearcher(IndexReader r) {
        super(r);
    }

    protected TopFieldDocs search(List<AtomicReaderContext> leaves, Weight weight, FieldDoc after, int nDocs,
                                  Sort sort, boolean fillFields, boolean doDocScores, boolean doMaxScore) throws IOException {
        // single thread
//        int limit = reader.maxDoc();
        int limit = getIndexReader().maxDoc();
        if (limit == 0) {
            limit = 1;
        }

        nDocs = Math.min(nDocs, limit);
        TopFieldCollector collector = TopFieldCollector.create(sort, nDocs, after, fillFields, doDocScores, doMaxScore, !weight.scoresDocsOutOfOrder());

        DuplicateTopDocsCollector duplicateCollector = new DuplicateTopDocsCollector(collector, this);

        search(leaves, weight, duplicateCollector);
        return (TopFieldDocs) duplicateCollector.topDocs();
    }

    protected void addSameDocIdMapping(Integer diskDocId, Integer ramDocId){
        sameDocIdMappingCache.put(diskDocId, ramDocId);
    }
}
