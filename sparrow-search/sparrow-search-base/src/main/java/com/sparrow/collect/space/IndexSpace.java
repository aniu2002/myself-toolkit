package com.sparrow.collect.space;

import com.sparrow.collect.analyze.AnalyzerController;
import com.sparrow.collect.website.SearchConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.ControlledRealTimeReopenThread;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.Arrays;

/**
 * NRT索引空间.
 * 依赖于IndexWriter的线程安全,本身不做线程安全处理.
 * Created by yaobo on 2014/11/11.
 */
public abstract class IndexSpace {

    protected Log log = LogFactory.getLog(this.getClass());

    protected String searchID;
    protected String indexPath;
    protected SearchConfig config;

    protected Directory directory;
    protected IndexWriter writer;
    protected TrackingIndexWriter tkWriter;
    protected SearcherManager searcherMgr;
    protected ControlledRealTimeReopenThread crtThread;

    protected volatile int maxMS = 50;

    public IndexSpace(String indexPath) {
        this.indexPath = indexPath;
    }

    protected abstract void initDirectory() throws IOException;

    protected void initWriter() throws IOException {
        LogMergePolicy mergePolicy = new LogDocMergePolicy();
        mergePolicy.setMergeFactor(10);
        mergePolicy.setMaxMergeDocs(Integer.MAX_VALUE);
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_46, AnalyzerController.getController().getSearchAnalyzer(searchID));
        indexWriterConfig.setMaxBufferedDocs(10000);
        indexWriterConfig.setMergePolicy(mergePolicy);
        indexWriterConfig.setRAMBufferSizeMB(64);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        this.writer = new IndexWriter(directory, indexWriterConfig);
    }

    protected void initNRTManager() throws IOException {
        searcherMgr = new SearcherManager(writer, true, new SearcherFactory());
        tkWriter = new TrackingIndexWriter(writer);
        crtThread = new ControlledRealTimeReopenThread(tkWriter, searcherMgr, 5.0, 0.025);
        crtThread.setDaemon(true);
        crtThread.setName(ControlledRealTimeReopenThread.class.getSimpleName() + "_" + searchID);
        crtThread.start();
    }

    public void init(String searchID, SearchConfig config) throws IOException {
        this.searchID = searchID;
        this.config = config;
        initDirectory();
        initWriter();
        initNRTManager();
    }

    public void commit() throws IOException {
        writer.commit();
    }

    public void merge() throws IOException {
        writer.maybeMerge();
        writer.waitForMerges();
        writer.forceMergeDeletes();
    }

    public IndexSearcher getSearcher() throws IOException {
        return searcherMgr.acquire();
    }

    public IndexReader getReader() throws IOException {
        return getSearcher().getIndexReader();
    }

    public void release(IndexSearcher searcher) throws IOException {
        searcherMgr.release(searcher);
    }

    public void add(Document... docs) throws IOException {
        tkWriter.addDocuments(Arrays.asList(docs));
        writeAfter();
    }

    public void update(Document... docs) throws IOException {
        for (Document doc : docs) {
            Term t = new Term(Contants.RECORD_INDEX_ONLY_KEY_NAME, doc.get(Contants.RECORD_INDEX_ONLY_KEY_NAME));
            tkWriter.updateDocument(t, doc);
        }
        writeAfter();
    }

    public void delete(String... ids) throws IOException {
        for (String id : ids) {
            Term t = new Term(Contants.RECORD_INDEX_ONLY_KEY_NAME, id);
            tkWriter.deleteDocuments(t);
        }
//        tkWriter.getAndIncrementGeneration();
        writeAfter();
    }

    public void addOnly(Document... docs) throws IOException {
        tkWriter.addDocuments(Arrays.asList(docs));
//        writeAfter();
    }

    public void updateOnly(Document... docs) throws IOException {
        for (Document doc : docs) {
            Term t = new Term(Contants.RECORD_INDEX_ONLY_KEY_NAME, doc.get(Contants.RECORD_INDEX_ONLY_KEY_NAME));
            tkWriter.updateDocument(t, doc);
        }
//        writeAfter();
    }

    public void deleteOnly(String... ids) throws IOException {
        for (String id : ids) {
            Term t = new Term(Contants.RECORD_INDEX_ONLY_KEY_NAME, id);
            tkWriter.deleteDocuments(t);
        }
//        writeAfter();
    }

    public void close() throws IOException {
        searcherMgr.close();
        crtThread.interrupt();
        crtThread.close();
        writer.commit();
        writer.close();
        directory.close();
    }

    public void writeAfter() throws IOException {
        try {
            crtThread.waitForGeneration(tkWriter.getGeneration(), maxMS);
        } catch (InterruptedException e) {
            log.error("crtThread.waitForGeneration error", e);
        }
    }

    public String getIndexPath() {
        return indexPath;
    }
}
