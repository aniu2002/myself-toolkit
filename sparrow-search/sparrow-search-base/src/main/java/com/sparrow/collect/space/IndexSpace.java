package com.sparrow.collect.space;

import com.dili.dd.searcher.basesearch.common.analyze.AnalyzerController;
import com.sparrow.collect.website.SearchConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
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

    //==================================================================================================================
//    public boolean recoverIndex() throws IOException, DLIndexSearchException {
//        FullIndexBakup fiBakup = null;
//        String bakDir = config.get("searcher.basesearch." + searchID + ".index.hdfs.bakdir");
//        String syncBakup = null;
//        try {
//            fiBakup = new DefaultFullIndexBakup();
//            fiBakup.initFileSystem("/", config);
//            long onlineMaxVersion = getBakupIndexMaxVersion(fiBakup);
//            if (onlineMaxVersion <= version.get() || onlineMaxVersion == 0) {
//                return true;
//            }
//            log.info("recover index : " + searchID);
//            // 从hdfs上读取最新的索引, 将master和slaver都设置为最新index
//            syncBakup = bakDir.endsWith("/") ? bakDir + onlineMaxVersion : bakDir + '/' + onlineMaxVersion;
//
//            File masterDirectory = new File(indexPath);
//            FileUtils.deleteDirectory(masterDirectory);
//            masterDirectory.mkdirs();
//            fiBakup.copyToLocalFile(false, syncBakup, indexPath, config);
//
//            setVersion(onlineMaxVersion);
//            IndexCompelete.writeVersion(indexPath, searchID, version.get(), Contants.SWITH_OVER_UPDATE_ID_TAG);
//            log.info("Copy HDFS bakIndex ToLocalFile : " + indexPath);
//            return true;
//        } catch (IOException e) {
//            log.error(e);
//            throw e;
//        } finally {
//            fiBakup.close();
//        }
//    }
//
//    public void bakupIndex() throws IOException {
//        long versionID = version.get();
//        FullIndexBakup fiBakup = null;
//        String bakDir = config.get("searcher.basesearch." + searchID + ".index.hdfs.bakdir");
//        String currentBakup = null;
//        try {
//            // 同步本地最新全量索引到hdfs服务
//            currentBakup = bakDir.endsWith("/") ? bakDir + versionID : bakDir + '/' + versionID;
//            fiBakup = new DefaultFullIndexBakup();
//            fiBakup.initFileSystem("/", config);
//            if (getBakupIndexMaxVersion(fiBakup) <= versionID) {
//                fiBakup.delDir(currentBakup, config);
//                fiBakup.copyFromLocalFile(false, true, indexPath, currentBakup, config);
//            }
//        } catch (IOException e) {
//            log.error(e);
//            if (!StringUtil.isNullOrEmpty(currentBakup)) {
//                fiBakup.delDir(currentBakup, config);
//            }
//        } finally {
//            fiBakup.close();
//        }
//    }
//
//    public long getBakupIndexMaxVersion(FullIndexBakup fiBakup)
//            throws IOException {
//        String bakDir = config.get("searcher.basesearch." + searchID + ".index.hdfs.bakdir");
//        if (StringUtil.isNullOrEmpty(bakDir)) {
//            return 0l;
//        }
//        List<Path> list = fiBakup.getFilesDir(bakDir, config);
//        if (list.size() == 0) {
//            return 0l;
//        }
//        long[] versions = new long[list.size()];
//        for (int i = 0; i < list.size(); i++) {
//            if (StringUtil.isCharOrNumberString(list.get(i).getName())) {
//                versions[i] = (Long.parseLong(list.get(i).getName()));
//            }
//        }
//        // 删除多余的数据
//        delOldVersion(fiBakup, bakDir, versions);
//        return NumberUtils.max(versions);
//    }
//
//    protected void delOldVersion(FullIndexBakup fiBakup, String bakDir, long[] versions) {
//        Arrays.sort(versions);
//        int count = config.getInt("searcher.basesearch." + searchID + ".index.hdfs.bak.count", 5);
//        int verCount = versions.length;
//        if (verCount <= count) {
//            return;
//        }
//        bakDir = bakDir.endsWith("/") ? bakDir : bakDir + '/';
//        for (int i = 0; i < verCount - count; i++) {
//            try {
//                fiBakup.delDir(bakDir + versions[i], config);
//            } catch (Exception e) {
//                log.error(e);
//            }
//        }
//    }
}
