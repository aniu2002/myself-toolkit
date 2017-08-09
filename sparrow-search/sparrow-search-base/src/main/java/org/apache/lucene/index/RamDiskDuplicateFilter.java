package org.apache.lucene.index;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Filter;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;

import java.io.IOException;
import java.util.*;

/**
 * 内存,磁盘排重filter
 * Created by yaobo on 2014/7/18.
 */
public class RamDiskDuplicateFilter extends Filter {

    private Log log = LogFactory.getLog(RamDiskDuplicateFilter.class);

    private String fieldName;

    public RamDiskDuplicateFilter(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        if (isRAMContext(context)) {
            return getRAMDocIdSet(context, acceptDocs);
        } else {
            return getDISKDocIdSet(context, acceptDocs);

        }
    }

    /**
     * 返回内存中fieldName所有的doc
     *
     * @param context
     * @param acceptDocs
     * @return
     * @throws IOException
     */
    private FixedBitSet getRAMDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        StandardDirectoryReader reader = (StandardDirectoryReader) context.parent.reader();
        FixedBitSet bits = new FixedBitSet(reader.maxDoc());
        Terms terms = context.reader().terms(fieldName);
        if (terms == null) {
            return bits;
        }
        TermsEnum termsEnum = terms.iterator(null);
        BytesRef currTerm;
        DocsEnum docs = null;
        while ((currTerm = termsEnum.next()) != null) {
            docs = termsEnum.docs(null, docs, DocsEnum.FLAG_NONE);
            int doc = docs.nextDoc();
            if (doc != Integer.MAX_VALUE) {
                bits.set(doc);
            }
        }
        return bits;
    }

    /**
     * 返回磁盘中fieldName与内存中不重复的doc, 通过term与内存中的term进行排重.
     *
     * @param context
     * @param acceptDocs
     * @return
     * @throws IOException
     */
    private DocIdSet getDISKDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {

        //获取所有内存ctx, 并取出内存中所有的term
        List<AtomicReaderContext> ramContexts = getRAMContexts(context);
        Set<String> ramTermSet = getRAMTermSet(ramContexts, acceptDocs);

        StandardDirectoryReader reader = (StandardDirectoryReader) context.parent.reader();
        FixedBitSet bits = new FixedBitSet(reader.maxDoc());

        Terms terms = context.reader().terms(fieldName);
        if (terms == null) {
            return bits;
        }
        TermsEnum termsEnum = terms.iterator(null);
        BytesRef currTerm;
        DocsEnum docs = null;
        while ((currTerm = termsEnum.next()) != null) {
            if (ramTermSet.contains(currTerm.utf8ToString())) {
                continue;
            }
            //TODO: 在lucene commit时, 先delete再insert. 出现0(delete),1(insert)的情况. term的倒排表为(0->1). 必须往后一直找到新的数据. merge后, 倒排表为(1)
            docs = termsEnum.docs(null, docs, DocsEnum.FLAG_NONE);
            int doc = docs.nextDoc();
            if (doc != DocIdSetIterator.NO_MORE_DOCS) {
                int lastDoc = doc;
                while ((doc = docs.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
                    lastDoc = doc;
                }
                bits.set(lastDoc);
            }
        }
        return bits;
    }

    /**
     * 返回contexts中所有的fieldName的term
     * @param contexts
     * @param acceptDocs
     * @return
     * @throws IOException
     */
    private Set<String> getRAMTermSet(List<AtomicReaderContext> contexts, Bits acceptDocs) throws IOException {
        Set<String> termSet = new HashSet<String>();
        for (AtomicReaderContext context : contexts) {
            TermsEnum termsEnum = context.reader().terms(fieldName).iterator(null);
            BytesRef currTerm;
            while ((currTerm = termsEnum.next()) != null) {
                termSet.add(currTerm.utf8ToString());
            }
        }
        return termSet;
    }

    /**
     * 返回IndexSearcher中所有的内存ctx
     *
     * @param context
     * @return
     */
    private List<AtomicReaderContext> getRAMContexts(AtomicReaderContext context) {
        List<AtomicReaderContext> ramContext = new ArrayList<AtomicReaderContext>();
        if (context.parent.parent == null){
            return ramContext;
        }
        for (AtomicReaderContext ctx : context.parent.parent.leaves()) {
            if (isRAMContext(ctx)) {
                ramContext.add(ctx);
            }
        }
        return ramContext;
    }

    /**
     * 是否是内存ctx
     *
     * @param context
     * @return
     */
    private boolean isRAMContext(AtomicReaderContext context) {
        DirectoryReader reader = (DirectoryReader) context.parent.reader();
        return reader.directory instanceof RAMDirectory;
    }

    @Override
    public String toString() {
        return "{RamDiskDuplicateFilter " +
                "duplicate field='" + fieldName + '\'' +
                '}';
    }
}
