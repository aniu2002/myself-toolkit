package org.apache.lucene.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.DocumentStoredFieldVisitor;
import org.apache.lucene.index.AtomicReaderContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 对TopFieldCollector进行包装. 每次collect之前, 先判断document是否已存在, 对已存在的不再收集
 * document是否存在, 通过indexSearcher.doc(docid)取得document, 再取出业务ID进行判断. 业务ID field必须为id
 * Created by yaobo on 2014/7/11.
 */

@Deprecated
public class DuplicateTopDocsCollector extends TopDocsCollector<FieldValueHitQueue.Entry> {

    private TopFieldCollector topFieldCollector;

    private MultiIndexSearcher indexSearcher;

    private static String ID = "id";

    private static Integer EMPTY_DOCID = Integer.MAX_VALUE;

    /**
     * 已收集的docids. key:业务id, value:docid
     */
    private Map<String, Integer> collectedDocIds = new HashMap<String, Integer>();

    protected DuplicateTopDocsCollector(TopFieldCollector topFieldCollector, MultiIndexSearcher indexSearcher) {
        super(null);
        this.topFieldCollector = topFieldCollector;
        this.indexSearcher = indexSearcher;
    }

    @Override
    public void setScorer(Scorer scorer) throws IOException {
        topFieldCollector.setScorer(scorer);
    }

    @Override
    public void collect(int doc) throws IOException {
        boolean duplicate = checkDuplicateDoc(topFieldCollector.docBase + doc);
        if (!duplicate) {
            topFieldCollector.collect(doc);
        }
    }

    @Override
    public void setNextReader(AtomicReaderContext context) throws IOException {
        topFieldCollector.setNextReader(context);
    }

    @Override
    public boolean acceptsDocsOutOfOrder() {
        return topFieldCollector.acceptsDocsOutOfOrder();
    }

    private boolean checkDuplicateDoc(int doc) throws IOException {
        //已处理过该doc
        if (this.indexSearcher.sameDocIdMappingCache.containsKey(doc)){
            //doc=EMPTY_DOCID, 表示没有重复的
            if (EMPTY_DOCID.equals(this.indexSearcher.sameDocIdMappingCache.get(doc))) {
                return false;
            }else{
                return true;
            }
        } else {
            //没有处理过doc, 取出bid进行判断
            //TODO:已经收集满doc, 不再进行判断. 但会造成最大条数比排重完的条数要多. 最后几页可能会受到影响; 需要对0-maxdoc来进行判断, 约往后越慢. 如果只对start-maxdoc进行判断, 商品则可能在1,2页都会出现
            if (!topFieldCollector.queueFull) {
                DocumentStoredFieldVisitor fieldVisitor = new DocumentStoredFieldVisitor(ID);
                this.indexSearcher.doc(doc, fieldVisitor);
                Document document = fieldVisitor.getDocument();
                if (document == null) {
                    return false;
                }
                String bid = document.get(ID);
                if (bid == null) {
                    return false;
                }
                Integer collectedDoc = collectedDocIds.get(bid);
                if (collectedDoc == null) {
                    collectedDocIds.put(bid, doc);
                }
                this.indexSearcher.addSameDocIdMapping(doc, collectedDoc == null ? EMPTY_DOCID : collectedDoc);
                return collectedDoc != null;
            }
            return false;
        }
    }

    public TopDocs topDocs() {
        return topFieldCollector.topDocs(0, topFieldCollector.topDocsSize());
    }
}
