package com.sparrow.collect.index.space;

import com.sparrow.collect.website.utils.CloseUtil;
import com.sparrow.collect.website.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class DiskIndexSpacer extends IndexSpacer {

    Log log = LogFactory.getLog(DiskIndexSpacer.class);


    public DiskIndexSpacer() {
        this(null, null, null, null);
    }

    public DiskIndexSpacer(Properties config, Directory directory,
                           IndexReader reader, IndexWriter writer,
                           SpaceType type, String dirPath, String searchID) {
        super(config, directory, reader, writer, type, dirPath, searchID);
    }

    public DiskIndexSpacer(Properties config, SpaceType type,
                           String dirPath, String searchID) {
        this(config, null, null, null, type, dirPath, searchID);
    }

    @Override
    public IndexReader getReader() {
        checkReader();
        return reader;
    }

    @Override
    public IndexWriter getWriter() {
        return writer;
    }

    @Override
    public boolean addDocs(Document... docs) throws IOException {
        while (!rwLock.writeLock().tryLock()) {
            sleep(1);
        }
        try {
            writer.addDocuments(Arrays.asList(docs));
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            rwLock.writeLock().unlock();
        }
        return true;
    }

    @Override
    public int commit() throws IOException {
        writer.commit();
        int numDocs = writer.numDocs();
        log.info(searchID + " disk slaver docs = " + numDocs);
        return numDocs;
    }

    public int mergeIndexAndReOpen() throws IOException {
//        CloseUtil.closeQuietly(reader.getSlave());
        writer.maybeMerge();
        writer.waitForMerges();
        writer.forceMergeDeletes();
        int indexCount = writer.maxDoc();
        if (reader != null) {
            CloseUtil.closeAndNull(reader);
            reader = DirectoryReader.open(directory);
        }
        return indexCount;
    }


    protected void checkReader() {
        if (reader == null) {
            reader = checkANDGetReader(directory);
        }
    }

    @Override
    public void rollback() throws IOException {
        writer.rollback();
    }

    @Override
    public boolean delDocs(String... ids) throws IOException {
        List<Term> list = new ArrayList<Term>();
        for (String id : ids) {
            list.add(new Term(Contants.RECORD_INDEX_ONLY_KEY_NAME, id));
        }
        writer.deleteDocuments(list.toArray(new Term[]{}));
        writer.commit();
        writer.close();
        reInitWriter();
        /*
         * if (write.getMaster() == null) { return true; }
         * write.getMaster().deleteDocuments(list.toArray(new Term[]{}));
         */
        return true;
    }

    @Override
    public boolean updateDocs(Map<String, Document> idAndDocs)
            throws IOException {
        while (!rwLock.writeLock().tryLock()) {
            sleep(1);
        }
        try {
            Set<Entry<String, Document>> set = idAndDocs.entrySet();
            for (Entry<String, Document> entry : set) {
                writer.updateDocument(
                        new Term(Contants.RECORD_INDEX_ONLY_KEY_NAME,
                                entry.getKey()), entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            rwLock.writeLock().unlock();
        }
        return true;
    }

    @Override
    public boolean updateDoc(String id, Document doc) throws IOException {
        while (!rwLock.writeLock().tryLock()) {
            sleep(1);
        }
        try {
            writer.updateDocument(
                    new Term(Contants.RECORD_INDEX_ONLY_KEY_NAME, id), doc);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            rwLock.writeLock().unlock();
        }
        return true;
    }

    @Override
    public void initIndexSpacer(String searchID, Properties config) {
        StringBuilder searchIDPrefix = new StringBuilder()
                .append(Contants.SEARCH_PREFIX).append('.').append(searchID)
                .append('.');
        String diskMaster = config.getProperty(new StringBuilder()
                .append(searchIDPrefix)
                .append(Contants.SEARCH_SPACE_DISK_MASTER).toString());
        String diskSlave = config.getProperty(new StringBuilder()
                .append(searchIDPrefix)
                .append(Contants.SEARCH_SPACE_DISK_SLAVE).toString());
        if (StringUtils.isNullOrEmpty(diskMaster)) {
            log.warn("init space disk : master=null;   not create disk index ");
        }
        log.info("init space disk : master=" + diskMaster + ";   slave="
                + diskSlave);
        String pathMsBean = diskMaster;
        try {
            pathMsBean = getDiskPathBean(pathMsBean);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setConfig(config);
        this.spaceType = SpaceType.DISK;
        dirPath = pathMsBean;
        this.searchID = searchID;
        this.initDirectory();
        this.initWriter();
    }
}
