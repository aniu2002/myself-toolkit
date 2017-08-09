package com.sparrow.collect.website.space;

import com.sparrow.collect.website.utils.StringUtils;
import com.sparrow.collect.website.utils.CloseUtil;
import org.apache.commons.io.FileUtils;
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
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class DiskIndexSpacer extends IndexSpacer {

    Log log = LogFactory.getLog(DiskIndexSpacer.class);
    
    
    public DiskIndexSpacer() {
        this(null, null, null, null);
    }

    public DiskIndexSpacer(Properties config, MSBean<Directory> directory,
            MSBean<IndexReader> reader, MSBean<IndexWriter> write,
            SpaceType type, MSBean<String> dirPath, String searchID) {
        super(config, directory, reader, write, type, dirPath, searchID);
    }

    public DiskIndexSpacer(Properties config, SpaceType type,
            MSBean<String> dirPath, String searchID) {
        this(config, null, null, null, type, dirPath, searchID);
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
    public IndexWriter getSlaveWriter() {
        return write.getSlave();
    }

    @Override
    public IndexReader getSlaveReaders() {
        checkReader();
        return reader.getSlave();
    }

    @Override
    public boolean switchOver( ) throws SwitchOverException {
        indexDataSync();
        while (!rwLock.writeLock().tryLock()) {
            sleep(10);
        }
        try {
            dirPath.switchOver();
            directory.switchOver();
            write.switchOver();
            if (reader != null) {
                reader.switchOver();
                if (reader.getMaster() != null
                        && reader.getMaster().maxDoc() > 1) {
                    reader.getMaster().document(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SwitchOverException(new StringBuilder().append(searchID)
                    .append(" ram index switch over ").toString(), e);
        } finally {
            rwLock.writeLock().unlock();
        }

        return true;
    }

    protected void indexDataSync() {
        while (!rwLock.readLock().tryLock()) {
            sleep(10);
        }
    }

    @Override
    public boolean addDocsBefore() {
        try {
            reader.getSlave().close();
            write.getSlave().close();
            directory.getSlave().close();
            File file = new File(dirPath.getSlave());
            FileUtils.deleteDirectory(file);
            if (file.mkdir()) {
                Directory dire = new MMapDirectory(file);
                directory.setSlave(dire);
                IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
                        Version.LUCENE_46, new IKAnalyzer(true));
                indexWriterConfig.setOpenMode(OpenMode.CREATE);
                write.setSlave(new IndexWriter(dire, indexWriterConfig));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean addDocs(Document... docs) throws IOException {
        while (!rwLock.writeLock().tryLock()) {
            sleep(1);
        }
        try {
            write.getSlave().addDocuments(Arrays.asList(docs));
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            rwLock.writeLock().unlock();
        }
        return true;
    }

    @Override
    public boolean addDocsAfter() {
        return false;
    }

    @Override
    public int commit() throws IOException {
        write.getSlave().commit();
        int numDocs = write.getSlave().numDocs();
        log.info(searchID + " disk slaver docs = " + numDocs);
        return numDocs;
    }

    public int mergeIndexAndReOpen() throws IOException {
//        CloseUtil.closeQuietly(reader.getSlave());
        write.getSlave().maybeMerge();
        write.getSlave().waitForMerges();
        write.getSlave().forceMergeDeletes();
        int indexCount = write.getSlave().maxDoc();
        if (reader != null) {
            CloseUtil.closeAndNull(reader.getSlave());
            reader.setSlave(DirectoryReader.open(directory.getSlave()));
        }
        // TODO 少一个测试搜索
        return indexCount;
    }

    @Override
    public boolean clearSlaveData() {
        closeSlave();
        try {
            FileUtils.deleteDirectory(new File(dirPath.getSlave()));
            System.out.println("----");
            initSlave();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean bakLocalAndClearSlaveData() throws IOException {
        closeSlave();
        File bakFile = new File(this.dirPath.getSlave() + ".bak");
        FileUtils.deleteDirectory(bakFile);
        new File(this.dirPath.getSlave()).renameTo(bakFile);
        initSlave();
        return true;
    }

    public void clearLocalBakSlaveData() throws IOException {
        File bakFile = new File(this.dirPath.getSlave() + ".bak");
        FileUtils.deleteDirectory(bakFile);
    }

    public boolean recoverLocalBakSlaveData() throws IOException{
        closeSlave();
        File bakFile = new File(this.dirPath.getSlave() + ".bak");
        File file = new File(this.dirPath.getSlave());
        FileUtils.deleteDirectory(file);
        bakFile.renameTo(file);
        initSlave();
        return true;
    }

    protected void checkReader() {
        if (reader == null) {
            reader = new MSBean<IndexReader>();
        }
        if (reader.getMaster() == null) {
            reader.setMaster(checkANDGetReader(directory.getMaster()));
        }
    }

    @Override
    public IndexReader[] getReaders() {
        try {
            while (!rwLock.readLock().tryLock()) {
                sleep(1);
            }
            checkReader();
            return new IndexReader[] { reader.getMaster() };
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rwLock.readLock().unlock();
        }
        return null;
    }

    @Override
    public void rollback() throws IOException {
        write.getSlave().rollback();
    }

    @Override
    public boolean delDocs(String... ids) throws IOException {
        List<Term> list = new ArrayList<Term>();
        for (String id : ids) {
            list.add(new Term(Contants.RECORD_INDEX_ONLY_KEY_NAME, id));
        }
        write.getSlave().deleteDocuments(list.toArray(new Term[] {}));
        write.getSlave().commit();
        write.getSlave().close();
        reInitWriter();
        /*
         * if (write.getMaster() == null) { return true; }
         * write.getMaster().deleteDocuments(list.toArray(new Term[]{}));
         */
        return true;
    }

    public void versionIncro( ) {
        log.info(new StringBuilder().append(searchID).append(
                " change version reOpen searcher"));
        try {
            closeSlave();
            FileUtils.deleteDirectory(new File(dirPath.getSlave()));
            FileUtils.copyDirectory(new File(dirPath.getMaster()), new File(
                    dirPath.getSlave()), new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (pathname.getName().endsWith(".lock")) {
                        return false;
                    }
                    return true;
                }
            });
            initSlave();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                write.getSlave().updateDocument(
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
            write.getSlave().updateDocument(
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
        MSBean<String> pathMsBean = new MSBean<String>(diskMaster, diskSlave);
        try {
            pathMsBean = getDiskPathBean(pathMsBean);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setConfig(config);
        this.spaceType = IndexSpacer.SpaceType.DISK;
        dirPath = pathMsBean;
        this.searchID = searchID;
        this.initDirectory();
        this.initWriter();
    }
}
