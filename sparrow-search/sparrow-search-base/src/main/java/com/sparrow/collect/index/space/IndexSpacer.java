package com.sparrow.collect.index.space;

import com.sparrow.collect.index.Constants;
import com.sparrow.collect.index.SpaceType;
import com.sparrow.collect.website.utils.CloseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public abstract class IndexSpacer implements IndexService, Closeable {
    String index;
    File dirPath;
    ReadWriteLock rwLock = new ReentrantReadWriteLock();
    protected Directory directory;
    protected IndexReader reader;
    protected IndexWriter writer;
    private Analyzer analyzer;

    public IndexSpacer(String index, String dirPath, Analyzer analyzer) {
        this.index = index;
        this.dirPath = this.createIndexDirectory(dirPath);
        this.analyzer = analyzer;
    }

    public final void close() {
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

    private File createIndexDirectory(String dirPath) {
        String dir = dirPath;
        if (StringUtils.isEmpty(dirPath))
            dir = String.format("%s/%s", System.getProperty("user.home"), this.index);
        File file = new File(dir);
        if (file.exists() && file.isDirectory())
            return file;
        file.mkdirs();
        return file;
    }

    public final void initialize() throws IOException {
        Directory dir = this.initDirectory(this.dirPath);
        if (dir == null) {
            throw new IOException("Index path error : " + this.dirPath);
        }
        //new MMapDirectory(new File(dirPath));
        this.writer = this.initIndexWriter(dir, analyzer);
        if (reader != null) {
            reader = DirectoryReader.open(dir);
        }
        this.directory = dir;
    }

    private IndexWriter initIndexWriter(Directory directory, Analyzer analyzer)
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
                Version.LUCENE_46, analyzer //new IKAnalyzer(true)
        );
        indexWriterConfig.setMaxBufferedDocs(10000);
        indexWriterConfig.setMergePolicy(mergePolicy);
        indexWriterConfig.setRAMBufferSizeMB(64);
        indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
        return new IndexWriter(directory, indexWriterConfig);
    }

    final IndexReader checkANDGetReader(Directory dire) {
        try {
            if (dire != null && dire.listAll().length > 4) {
                return DirectoryReader.open(dire);
            }
        } catch (IOException e) {
            log.error("Check and get reader error : ", e);
        }
        return null;
    }

    final void checkReader() {
        if (reader == null) {
            reader = checkANDGetReader(directory);
        }
    }

    public final IndexReader getReader() {
        checkReader();
        return reader;
    }

    final void reInitWriter() {
        if (directory == null) {
            return;
        }
        if (this.writer != null) {
            CloseUtil.closeAndNull(writer);
        }
        try {
            writer = initIndexWriter(directory, this.analyzer);
        } catch (IOException e) {
            log.error("Initialize index writer error : ", e);
        }
    }

    protected abstract SpaceType getSpaceType();

    protected abstract Directory initDirectory(File dir);

    @Override
    public final boolean addDocuments(Document... docs) throws IOException {
        try {
            writer.addDocuments(Arrays.asList(docs));
        } catch (IOException e) {
            log.error("Add document error : ", e);
        } finally {
            rwLock.writeLock().unlock();
        }
        return true;
    }

    @Override
    public final int commit() throws IOException {
        int numDocs;
        rwLock.writeLock().lock();
        try {
            // 优化操作
            // writer.commit();
            // writer.optimize();
            writer.commit();
            numDocs = writer.numDocs();
            log.info("Commit RAM docs = {}", numDocs);
        } finally {
            rwLock.writeLock().unlock();
        }
        return numDocs;
    }

    @Override
    public final void rollback() throws IOException {
        rwLock.writeLock().lock();
        try {
            writer.rollback();
        } catch (IOException e) {
            log.error("Rollback error : ", e);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public final boolean deleteDocuments(String... ids) throws IOException {
        rwLock.writeLock().lock();
        try {
            for (String id : ids) {
                writer.deleteDocuments(new Term(Constants.RECORD_INDEX_ONLY_KEY_NAME, id));
            }
            // writer.commit();
        } catch (IOException e) {
            log.error("Delete document error : ", e);
        } finally {
            rwLock.writeLock().unlock();
        }
        return true;
    }

    @Override
    public final boolean updateDocuments(Map<String, Document> idAndDocs)
            throws IOException {
        rwLock.writeLock().lock();
        try {
            Set<Map.Entry<String, Document>> set = idAndDocs.entrySet();
            for (Map.Entry<String, Document> entry : set) {
                writer.updateDocument(new Term(Constants.RECORD_INDEX_ONLY_KEY_NAME, entry.getKey()), entry.getValue());
            }
        } catch (IOException e) {
            log.error("Update document map error : ", e);
        } finally {
            rwLock.writeLock().unlock();
        }
        return true;
    }

    @Override
    public boolean updateDocument(String id, Document doc) throws IOException {
        rwLock.writeLock().lock();
        try {
            writer.updateDocument(new Term(Constants.RECORD_INDEX_ONLY_KEY_NAME, id), doc);
        } catch (IOException e) {
            log.error("Update document error : ", e);
        } finally {
            rwLock.writeLock().unlock();
        }
        return true;
    }

    public int mergeIndexAndReOpen() throws IOException {
        return 0;
    }
}
