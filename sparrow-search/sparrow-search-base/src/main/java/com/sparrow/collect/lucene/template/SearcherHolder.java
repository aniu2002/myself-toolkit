package com.sparrow.collect.lucene.template;

import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

public class SearcherHolder {
    private IndexSearcher searcher;

    protected void initDirectory(String indexPath) throws IOException {
        File file = new File(indexPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        MMapDirectory diskDirectory = new MMapDirectory(file);
        Directory directory = new RAMDirectory(diskDirectory, IOContext.DEFAULT);
        diskDirectory.close();
    }

    public void openRam(String indexPath) throws IOException {
        FSDirectory directoryX = FSDirectory.open(new File(indexPath));
        IndexReader indexReader = DirectoryReader.open(directoryX);

        IndexWriter writer = new IndexWriter(directoryX, new IndexWriterConfig(Version.LUCENE_46, new IKAnalyzer(true)));
    }

    protected IndexWriter initIndexWriter(Directory directory)
            throws IOException {
        LogMergePolicy mergePolicy = new LogDocMergePolicy();
        // 索引基本配置
        // 设置segment添加文档(Document)时的合并频率
        // 值较小,建立索引的速度就较慢
        // 值较大,建立索引的速度就较快,>10适合批量建立索引
        mergePolicy.setMergeFactor(10);
        // 设置segment最大合并文档(Document)数
        // 值较小有利于追加索引的速度
        // 值较大,适合批量建立索引和更快的搜索
        mergePolicy.setMaxMergeDocs(Integer.MAX_VALUE);
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
                Version.LUCENE_46, new IKAnalyzer(true)
        );
        indexWriterConfig.setMaxBufferedDocs(10000);
        indexWriterConfig.setMergePolicy(mergePolicy);
        indexWriterConfig.setRAMBufferSizeMB(64);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        return new IndexWriter(directory, indexWriterConfig);
    }

    public IndexSearcher getIndexSearcher(String indexPath) throws IOException {
        Directory directory = new MMapDirectory(new File(indexPath));
        LogMergePolicy mergePolicy = new LogDocMergePolicy();
        mergePolicy.setMergeFactor(10);
        mergePolicy.setMaxMergeDocs(Integer.MAX_VALUE);
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_46, new IKAnalyzer(true));
        indexWriterConfig.setMaxBufferedDocs(10000);
        indexWriterConfig.setMergePolicy(mergePolicy);
        indexWriterConfig.setRAMBufferSizeMB(64);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        IndexWriter writer = new IndexWriter(directory, indexWriterConfig);

        SearcherManager searcherMgr = new SearcherManager(writer, true, new org.apache.lucene.search.SearcherFactory());
        TrackingIndexWriter tkWriter = new TrackingIndexWriter(writer);
        Thread crtThread = new ControlledRealTimeReopenThread(tkWriter, searcherMgr, 5.0, 0.025);
        crtThread.setDaemon(true);
        crtThread.setName(ControlledRealTimeReopenThread.class.getSimpleName());
        crtThread.start();
        return searcherMgr.acquire();
    }

    public SearcherHolder(IndexSearcher searcher) {
        this.searcher = searcher;
    }

    public IndexSearcher getSearcher() {
        return this.searcher;
    }

    public void setSearcher(IndexSearcher searcher) {
        this.searcher = searcher;
    }
}
