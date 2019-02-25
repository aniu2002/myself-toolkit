package com.sparrow.collect.index.space;

import com.sparrow.collect.website.utils.CloseUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class IndexSpacer {
    protected Properties config;
    protected Directory directory;
    protected IndexReader reader;
    protected IndexWriter writer;
    protected SpaceType spaceType;
    protected String dirPath;
    protected String searchID;
    protected ReadWriteLock rwLock = new ReentrantReadWriteLock();
    Log log = LogFactory.getLog(IndexSpacer.class);

    public enum SpaceType {
        RAM, DISK, REMOTE, RAM_DISK
    }

    public IndexSpacer(Properties config, SpaceType type,
                       String dirPath, String searchID) {
        super();
        this.config = config;
        this.spaceType = type;
        this.dirPath = dirPath;
        this.searchID = searchID;
    }

    public void setConfig(Properties config) {
        this.config = config;
    }

    public IndexSpacer(Properties config, Directory directory,
                       IndexReader reader, IndexWriter writer,
                       SpaceType type, String dirPath, String searchID) {
        super();
        this.config = config;
        this.directory = directory;
        this.reader = reader;
        this.writer = writer;
        this.spaceType = type;
        this.dirPath = dirPath;
        this.searchID = searchID;
    }

    public IndexSpacer(Properties config, Directory directory,
                       IndexReader reader, IndexWriter writer,
                       SpaceType type, String searchID) {
        super();
        this.config = config;
        this.directory = directory;
        this.reader = reader;
        this.writer = writer;
        this.spaceType = type;
        this.searchID = searchID;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void close() {
        if (reader != null) {
            CloseUtil.closeAndNull(reader);
        }
        if (writer != null) {
            CloseUtil.closeAndNull(writer);
        }
        if (directory != null) {
            CloseUtil.closeAndNull(directory);
        }
    }

    protected void initialize() throws IOException {
        directory = new MMapDirectory(new File(dirPath));
        writer = initIndexWriter(directory);
        if (reader != null) {
            reader = DirectoryReader.open(directory);
        }
    }

    public void initDirectory() {
        if (this.directory != null) {
            return;
        }
        if (dirPath == null) {
            return;
        }
        log.info("start init " + this.searchID + " ramDirectory...");
        try {
            this.directory = new MMapDirectory(new File(dirPath));
            log.info("end  " + this.searchID + " ramDirectory init...");
        } catch (IOException e) {
            e.printStackTrace();
            log.info(this.searchID + " Directory init fialed...");
        }
    }

    public void initReader() {
        if (this.reader != null) {
            return;
        }
        reInitReader();
    }

    public void reInitReader() {
        if (directory == null) {
            return;
        }
        log.info("start init " + this.searchID + " Reader...");
        IndexReader ireBean = checkANDGetReader(directory);
        try {
            if (reader != null) {
                CloseUtil.closeAndNull(reader);
            }
            this.reader = ireBean;
            log.info("end  " + this.searchID + " Reader init...");
        } catch (Exception e) {
            log.info(this.searchID + " indexReader init fialed...");
        }

    }

    public void initWriter() {
        if (this.writer != null) {
            return;
        }
        reInitWriter();
    }

    public void reInitWriter() {
        if (directory == null) {
            return;
        }
        if (this.writer != null) {
            CloseUtil.closeAndNull(writer);
        }
        log.info("start init " + this.searchID + " indexWriter...");
        try {
            writer = initIndexWriter(directory);
            log.info("end  " + this.searchID + " indexWriter init...");
        } catch (IOException e) {
            e.printStackTrace();
            log.info(this.searchID + " indexWriter init fialed...");
        }
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
        indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
        return new IndexWriter(directory, indexWriterConfig);
    }

    public abstract IndexReader getReader();

    public abstract IndexWriter getWriter();

    public abstract boolean addDocs(Document... docs) throws IOException;

    public abstract boolean delDocs(String... ids) throws IOException;

    public abstract boolean updateDocs(Map<String, Document> idAndDocs)
            throws IOException;

    public abstract boolean updateDoc(String id, Document doc)
            throws IOException;

    public abstract int commit() throws IOException;

    public abstract void rollback() throws IOException;

    public SpaceType getSpaceType() {
        return this.spaceType;
    }

    protected void checkANDGetReader(Directory dire, List<IndexReader> l) {
        try {
            if (dire != null && dire.listAll().length > 4) {
                l.add(DirectoryReader.open(dire));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected IndexReader checkANDGetReader(Directory dire) {
        try {
            if (dire != null && dire.listAll().length > 4) {
                return DirectoryReader.open(dire);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected abstract void initIndexSpacer(String searchID, Properties config);


    /**
     * 获取master, slaver路径
     * 验证完整性, 将完整->不完整; 新的->旧的.
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    protected String getDiskPathBean(String fileName) throws IOException {
        return fileName;
    }
}
