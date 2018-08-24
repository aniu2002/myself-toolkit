package com.sparrow.collect.index;

import com.sparrow.collect.space.Contants;
import com.sparrow.collect.space.MSBean;
import com.sparrow.collect.space.SwitchOverException;
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

public class RamDocIndex extends DocIndex {
    private long maxRamSize = 1024 * 1024;
    Log log = LogFactory.getLog(RamDocIndex.class);

    public RamDocIndex() {
        this(null, null, null);
    }

    public RamDocIndex(Properties config, MSBean<Directory> directory,
                       MSBean<IndexReader> reader, MSBean<IndexWriter> write,
                       SpaceType type, String searchID) {
        super(config, directory, reader, write, type, searchID);
    }

    public RamDocIndex(Properties config, SpaceType type, String searchID) {
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
    public IndexReader getMasterReaders() {
        checkReader();
        return reader.getMaster();
    }

    @Override
    public IndexWriter getMasterIndexers() {

        return write.getMaster();
    }

    @Override
    public IndexReader[] getReaders() {
        try {
            while (!rwLock.readLock().tryLock()) {
                sleep(1);
            }
            checkReader();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rwLock.readLock().unlock();
        }
        return new IndexReader[]{reader.getMaster(), reader.getSlave()};
    }

    protected void checkReader() {
        if (reader == null) {
            reader = new MSBean<IndexReader>(
                    checkANDGetReader(directory.getMaster()),
                    checkANDGetReader(directory.getSlave()));
        }
        if (reader.getMaster() == null) {
            reader.setMaster(checkANDGetReader(directory.getMaster()));
        }
        if (reader.getSlave() == null) {
            reader.setSlave(checkANDGetReader(directory.getSlave()));
        }
    }

    @Override
    public IndexWriter getSlaveWriter() {
        return write.getSlave();
    }

    @Override
    public IndexReader getSlaveReaders() {
        checkReader();
        return reader.getSlave();
    }

    @Override
    public boolean switchOver() throws SwitchOverException {
        while (!rwLock.writeLock().tryLock()) {
            sleep(10);
        }
        log.debug(new StringBuilder().append(searchID).append(
                " ram index start switch over"));
        try {
            clearSlaveData();
            if (reader != null) {
                reader.switchOver();
            }
            write.switchOver();
            directory.switchOver();
        } finally {
            rwLock.writeLock().unlock();
        }
        log.debug(new StringBuilder().append(searchID).append(
                " ram index end switch over"));
        return true;
    }

    @Override
    public boolean clearSlaveData() throws SwitchOverException {
        try {
            log.debug(new StringBuilder().append(searchID).append(
                    "ram index start switch -> create new slave ram"));
            if (directory != null) {
                directory.setSlave(new RAMDirectory());
            }
            log.debug(new StringBuilder().append(searchID).append(
                    "ram index start switch -> create new ram writer"));
            if (write != null) {
                write.setSlave(initIndexWriter(directory.getSlave()));
            }
            if (reader != null) {
                reader.setSlave(null);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new SwitchOverException(new StringBuilder().append(searchID)
                    .append(" ram index switch over ").toString(), e);
        }
    }

    public long getMaxRamSize() {
        return maxRamSize;
    }

    public void setMaxRamSize(long maxRamSize) {
        this.maxRamSize = maxRamSize;
    }

    private long getMasterCurrentSize() {
        RAMDirectory masterDirectory = (RAMDirectory) directory.getMaster();
        return masterDirectory.sizeInBytes();
    }

    /**
     * this method is TODO
     *
     * @return
     * @createTime 2014年5月21日 下午7:11:31
     * @author tanghongjun
     */
    public boolean isMasterFull() {
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
            write.getMaster().addDocuments(Arrays.asList(docs));
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
            write.getMaster().commit();
            log.info(searchID + " ram  docs = " + write.getMaster().numDocs());
        } finally {
            rwLock.writeLock().unlock();
        }
        int numDocs = write.getMaster().numDocs();
        return numDocs;
    }

    @Override
    public boolean addDocsBefore() {
        return false;
    }

    @Override
    public boolean addDocsAfter() {
        return false;
    }

    @Override
    public void rollback() throws IOException {
        while (!rwLock.writeLock().tryLock()) {
            sleep(1);
        }
        try {
            write.getMaster().rollback();
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
                write.getMaster().deleteDocuments(
                        new Term(Contants.RECORD_INDEX_ONLY_KEY_NAME, id));
                if (directory.getSlave() != null && write.getSlave() != null) {
                    write.getSlave().deleteDocuments(
                            new Term(Contants.RECORD_INDEX_ONLY_KEY_NAME, id));
                }
            }
            write.getMaster().commit();
            write.getMaster().close();
            if (directory.getSlave() != null && write.getSlave() != null) {
                write.getSlave().commit();
                write.getSlave().close();
            }
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
                write.getMaster().updateDocument(
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
            write.getMaster().updateDocument(
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
        MSBean<Directory> msBean = new MSBean<Directory>(new RAMDirectory(),
                new RAMDirectory());
        this.directory = msBean;
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
            this.spaceType = DocIndex.SpaceType.RAM;
            this.searchID = searchID;
            initRamSize();
            this.initDirectory();
            this.initWriter();
        }
    }
}
