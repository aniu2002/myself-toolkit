package com.sparrow.collect.index.space;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

public class RamIndexSpacer extends IndexSpacer {
    private long maxRamSize = 1024 * 1024;
    Log log = LogFactory.getLog(RamIndexSpacer.class);

    public RamIndexSpacer() {
        this(null, null, null);
    }

    public RamIndexSpacer(Properties config, Directory directory,
                          IndexReader reader, IndexWriter writer,
                          SpaceType type, String searchID) {
        super(config, directory, reader, writer, type, searchID);
    }

    public RamIndexSpacer(Properties config, SpaceType type, String searchID) {
        this(config, null, null, null, type, searchID);
        if (config == null || StringUtils.isBlank(searchID)) {
            return;
        }
        initRamSize();
    }

    private void initRamSize() {
        long ramSize = Integer.parseInt(config.getProperty(
                Contants.getStringByArray(new String[]{
                        "searcher.basesearch", searchID,
                        "ram.max.size.MB"}), "0"
        )) * 1024;
        if (ramSize == 0) {
            ramSize = Integer.parseInt(config.getProperty(
                    Contants.getStringByArray(new String[]{
                            "searcher.basesearch", searchID,
                            "ram.max.size.KB"}), "0"
            ));
        }
        maxRamSize = ramSize;
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

    protected void checkReader() {
        if (reader == null) {
            reader = checkANDGetReader(directory);
        }
    }

    public long getMaxRamSize() {
        return maxRamSize;
    }

    public void setMaxRamSize(long maxRamSize) {
        this.maxRamSize = maxRamSize;
    }

    private long getMasterCurrentSize() {
        RAMDirectory masterDirectory = (RAMDirectory) directory;
        return masterDirectory.sizeInBytes();
    }

    /**
     * this method is TODO
     *
     * @return
     * @createTime 2014年5月21日 下午7:11:31
     * @author tanghongjun
     */
    public boolean isFull() {
        log.debug(new StringBuilder().append("check ").append(searchID)
                .append(" ram index maxRamSize=").append(maxRamSize)
                .append(" currentSize=").append(getMasterCurrentSize())
                .toString());
        if (getMasterCurrentSize() >= maxRamSize) {
            return true;
        }
        return false;
    }

    @Override
    public boolean addDocs(Document... docs) throws IOException {
        while (!rwLock.writeLock().tryLock()) {
            sleep(1);
        }
        try {
            writer.addDocuments(Arrays.asList(docs));
            // write.getMaster().prepareCommit();
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
        while (!rwLock.writeLock().tryLock()) {
            sleep(1);
        }
        try {
            writer.commit();
            log.info(searchID + " ram  docs = " + writer.numDocs());
        } finally {
            rwLock.writeLock().unlock();
        }
        int numDocs = writer.numDocs();
        return numDocs;
    }

    @Override
    public void rollback() throws IOException {
        while (!rwLock.writeLock().tryLock()) {
            sleep(1);
        }
        try {
            writer.rollback();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public boolean delDocs(String... ids) throws IOException {
        while (!rwLock.writeLock().tryLock()) {
            sleep(1);
        }
        try {
            for (String id : ids) {
                writer.deleteDocuments(new Term(Contants.RECORD_INDEX_ONLY_KEY_NAME, id));
            }
            writer.commit();
            writer.close();
            reInitWriter();
            // write.getMaster().prepareCommit();
            // versionIncro(DLExecuter.getDefaultExecuter());
        } catch (IOException e) {
            e.printStackTrace();
            // throw e;
        } finally {
            rwLock.writeLock().unlock();
        }
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
                                entry.getKey()), entry.getValue()
                );
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
            // versionIncro(DLExecuter.getDefaultExecuter());
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            rwLock.writeLock().unlock();
        }
        return true;
    }

    @Override
    public void initDirectory() {
        if (this.directory != null) {
            return;
        }
        log.info("start init " + this.searchID + " ramDirectory...");
        this.directory = new RAMDirectory();
        log.info("end  " + this.searchID + " ramDirectory init...");
    }

    @Override
    public void initIndexSpacer(String searchID, Properties config) {
        StringBuilder searchIDPrefix = new StringBuilder()
                .append(Contants.SEARCH_PREFIX).append('.').append(searchID)
                .append('.');
        boolean isRam = "true".equals(config.getProperty(
                new StringBuilder().append(searchIDPrefix)
                        .append(Contants.SEARCH_IS_SPACE_RAM).toString(), "true"
        ));
        log.info("init space ram:" + isRam);
        if (isRam) {
            this.config = config;
            this.spaceType = SpaceType.RAM;
            this.searchID = searchID;
            initRamSize();
            this.initDirectory();
            this.initWriter();
        }
    }
}
