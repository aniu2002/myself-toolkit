package com.sparrow.collect.website.space;

import com.sparrow.collect.website.utils.CloseUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class IndexSpacer {

    protected Properties config;
    protected MSBean<Directory> directory;
    protected MSBean<IndexReader> reader;
    protected MSBean<IndexWriter> write;
    protected SpaceType spaceType;
    protected MSBean<String> dirPath;
    protected String searchID;
    protected ReadWriteLock rwLock = new ReentrantReadWriteLock();
    Log log = LogFactory.getLog(IndexSpacer.class);

    public enum SpaceType {
        RAM, DISK, REMOTE, RAMDISK
    }

    public IndexSpacer(Properties config, SpaceType type,
                       MSBean<String> dirPath, String searchID) {
        super();
        this.config = config;
        this.spaceType = type;
        this.dirPath = dirPath;
        this.searchID = searchID;
    }

    public void setConfig(Properties config) {
        this.config = config;
    }

    public IndexSpacer(Properties config, MSBean<Directory> directory,
                       MSBean<IndexReader> reader, MSBean<IndexWriter> write,
                       SpaceType type, MSBean<String> dirPath, String searchID) {
        super();
        this.config = config;
        this.directory = directory;
        this.reader = reader;
        this.write = write;
        this.spaceType = type;
        this.dirPath = dirPath;
        this.searchID = searchID;
    }

    public IndexSpacer(Properties config, MSBean<Directory> directory,
                       MSBean<IndexReader> reader, MSBean<IndexWriter> write,
                       SpaceType type, String searchID) {
        super();
        this.config = config;
        this.directory = directory;
        this.reader = reader;
        this.write = write;
        this.spaceType = type;
        this.searchID = searchID;
    }

    public MSBean<String> getDirPath() {
        return dirPath;
    }

    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void closeMasters() {
        if (reader != null) {
            CloseUtil.closeAndNull(reader.getMaster());
        }
        if (write != null) {
            CloseUtil.closeAndNull(write.getMaster());
        }
        if (directory != null) {
            CloseUtil.closeAndNull(directory.getMaster());
        }
    }

    protected void initMaster() throws IOException {
        directory.setMaster(new MMapDirectory(new File(dirPath.getMaster())));
        write.setMaster(initIndexWriter(directory.getMaster()));
        if (reader != null) {
            reader.setMaster(DirectoryReader.open(directory.getMaster()));
        }
    }


    protected void initSlave() throws IOException {
        new File(dirPath.getSlave()).mkdirs();
        directory.setSlave(new MMapDirectory(new File(dirPath.getSlave())));
        directory.getSlave().clearLock("write.lock");
        write.setSlave(initIndexWriter(directory.getSlave()));
    }


    protected void closeSlave() {
        if (reader != null) {
            CloseUtil.closeAndNull(reader.getSlave());
        }
        if (write != null) {
            CloseUtil.closeAndNull(write.getSlave());
        }
        if (directory != null) {
            CloseUtil.closeAndNull(directory.getSlave());
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
            MSBean<Directory> msBean = new MSBean<Directory>(new MMapDirectory(
                    new File(dirPath.getMaster())), new MMapDirectory(new File(
                    dirPath.getSlave())));
            this.directory = msBean;
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
        MSBean<IndexReader> ireBean = new MSBean<IndexReader>();
        try {
            if (reader != null) {
                CloseUtil.closeAndNull(reader.getMaster());
                CloseUtil.closeAndNull(reader.getSlave());
            }
            ireBean.setMaster(checkANDGetReader(directory.getMaster()));
            ireBean.setSlave(checkANDGetReader(directory.getSlave()));
            this.reader = ireBean;
            log.info("end  " + this.searchID + " Reader init...");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            log.info(this.searchID + " indexReader init fialed...");
        } finally {

        }

    }

    public void initWriter() {
        if (this.write != null) {
            return;
        }
        reInitWriter();
    }

    public void reInitWriter() {
        if (directory == null) {
            return;
        }
        if (this.write != null) {
            CloseUtil.closeAndNull(write.getMaster());
            CloseUtil.closeAndNull(write.getSlave());
        }
        log.info("start init " + this.searchID + " indexWriter...");
        MSBean<IndexWriter> msBean = new MSBean<IndexWriter>();
        try {
            directory.getMaster().clearLock("write.lock");
            directory.getSlave().clearLock("write.lock");
            msBean.setMaster(initIndexWriter(directory.getMaster()));
            msBean.setSlave(initIndexWriter(directory.getSlave()));
            write = msBean;
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

    public abstract IndexReader getMasterReaders();

    public abstract IndexWriter getMasterIndexers();

    public abstract IndexReader[] getReaders();

    public List<IndexReader> getReaderList() {
        List<IndexReader> list = new ArrayList<IndexReader>(2);
        IndexReader[] inReaders = getReaders();
        if (inReaders == null) {
            return list;
        }
        for (IndexReader ir : inReaders) {
            if (ir != null) {
                list.add(ir);
            }
        }
        return list;
    }

    public abstract IndexWriter getSlaveWriter();

    public abstract IndexReader getSlaveReaders();

    public abstract boolean addDocsBefore();

    public abstract boolean addDocs(Document... docs) throws IOException;

    public abstract boolean delDocs(String... ids) throws IOException;

    public abstract boolean updateDocs(Map<String, Document> idAndDocs)
            throws IOException;

    public abstract boolean updateDoc(String id, Document doc)
            throws IOException;

    public abstract boolean addDocsAfter();

    public abstract int commit() throws IOException;

    public abstract void rollback() throws IOException;

    public abstract boolean clearSlaveData() throws SwitchOverException;

    public SpaceType getSpaceType() {
        return this.spaceType;
    }

    public abstract boolean switchOver()
            throws SwitchOverException;

    public void checkANDGetReader(Directory dire, List<IndexReader> l) {
        try {
            if (dire != null && dire.listAll().length > 4) {
                l.add(DirectoryReader.open(dire));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IndexReader checkANDGetReader(Directory dire) {
        try {
            if (dire != null && dire.listAll().length > 4) {
                return DirectoryReader.open(dire);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ReadWriteLock getRwLock() {
        return rwLock;
    }

    public abstract void initIndexSpacer(String searchID, Properties config);


    /**
     * 获取master, slaver路径
     * 验证完整性, 将完整->不完整; 新的->旧的.
     *
     * @param msBean
     * @return
     * @throws java.io.IOException
     */
    protected MSBean<String> getDiskPathBean(MSBean<String> msBean) throws IOException {
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.getName().endsWith(".lock")) {
                    return false;
                }
                return true;
            }
        };
        File file1 = new File(msBean.getMaster());
        File file2 = new File(msBean.getSlave());

        long isUpdate1 = file1.exists() ? new File(msBean.getMaster()).lastModified() : 0l;
        long isUpdate2 = file2.exists() ? new File(msBean.getSlave()).lastModified() : 0l;

        boolean isCom1 = checkIndexCompelete(file1);
        boolean isCom2 = checkIndexCompelete(file2);

        if (!isCom1 && !isCom2) {
            String message = msBean.getMaster() + " and " + msBean.getSlave() + " does not exist!!! ";
            log.warn(message);
        }
        if (isCom1 && !isCom2) {
            FileUtils.deleteDirectory(file2);
            FileUtils.copyDirectory(file1, file2, fileFilter, true);
            log.info(file2.getPath() + " is not complete, delete it; and copy " + file1.getPath() + " to " + file2.getPath());
        }
        if (!isCom1 && isCom2) {
            FileUtils.deleteDirectory(file1);
            FileUtils.copyDirectory(file2, file1, fileFilter, true);
            log.info(file1.getPath() + " is not complete, delete it; and copy " + file2.getPath() + " to " + file1.getPath());
        }

        //modify yb 11.07
        //只是切换应该有问题, slaver数据不是最新的, 在此基础上提交数据会有问题.
//        if (isUpdate1 < isUpdate2) {
//            msBean.switchOver();
//        }

        //索引都正常的情况下, 修改为将旧的copy成最新的数据
        //write.lock的存在, 每次启动都会出现最后修改时间不一致的情况.
        if (isCom1 & isCom2) {
            if (isUpdate1 < isUpdate2) {
                FileUtils.deleteDirectory(file1);
                FileUtils.copyDirectory(file2, file1, fileFilter, true);
                log.info(file1.getPath() + " is not latest version; copy " + file2.getPath() + " to " + file1.getPath());
            }
            if (isUpdate1 > isUpdate2) {
                FileUtils.deleteDirectory(file2);
                FileUtils.copyDirectory(file1, file2, fileFilter, true);
                log.info(file2.getPath() + " is not latest version; copy " + file1.getPath() + " to " + file2.getPath());
            }
        }
        return msBean;
    }

    protected boolean checkIndexCompelete(File indexDir) {
        IndexReader ir1 = null;
        Directory dire1 = null;
        boolean isCom = true;
        //验证文件是否完整
        try {
            dire1 = new SimpleFSDirectory(indexDir);
            if (dire1.listAll().length < 2) {
                throw new FileNotFoundException();
            }
            //此处clearlock后lastModified会变, checklastModified必须在checkIndexCompelete之前
            dire1.clearLock("write.lock");
            ir1 = DirectoryReader.open(dire1);
        } catch (CorruptIndexException e) {
            log.error(e);
            isCom = false;
        } catch (EOFException e) {
            log.error(e);
            isCom = false;
        } catch (FileNotFoundException e) {
            log.error(e);
            isCom = false;
        } catch (IOException e) {
            log.error(e);
        } finally {
            CloseUtil.closeAndNull(dire1);
            CloseUtil.closeAndNull(ir1);
        }
        if (!isCom) {
            try {
                FileUtils.deleteDirectory(indexDir);
            } catch (IOException e) {
                log.error(e);
            }
        }
        return isCom;
    }
}
