package com.sparrow.collect.space;

import com.dili.dd.searcher.bsearch.common.index.*;
import com.dili.dd.searcher.bsearch.common.search.NRTSearcher;
import com.sparrow.collect.website.SearchConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * master提供index,search服务, 读写操作的线程安全由lucene处理.
 * slaver提供全量索引服务, 然后会与master切换, 切换过程中不会阻塞master提供服务.
 * 索引的提交,备份,恢复由ReentrantLock保证线程安全.
 * Created by yaobo on 2014/11/17.
 */
public class AdvanceIndexSpace {

    private Log log = LogFactory.getLog(AdvanceIndexSpace.class);

    private String searchId;

    private SearchConfig config;

    private MSBean<IndexSpacer> indexSpace;

    private LocalIndex localIndex;

    private IndexVersion indexVersion;

    private IndexRecover indexRecover;

    private IndexCommit indexCommit;

    private IndexBackup indexBackup;

    private IndexRedoLog indexRedoLog;

    private ExecutorService executor;

    /**
     * 保证每次拿到正确的master和slaver, 保证提交,备份,还原操作的线程安全
     */
    private ReentrantLock spaceLock;

    public AdvanceIndexSpace() {

    }

    public void init(String searchId, SearchConfig config) throws IOException {
        this.log.info(searchId + " init");
        this.searchId = searchId;
        this.config = config;

        localIndex = new LocalIndex(searchId, config);
        indexVersion = new IndexVersion(searchId, config, localIndex.getMaster());
        indexRecover = new IndexRecover(searchId, config);
        indexSpace = new MSBean<IndexSpace>(new DiskIndexSpace(localIndex.getMaster()), new DiskIndexSpace(localIndex.getSlaver()));

        spaceLock = new ReentrantLock(true);
        executor = new ThreadPoolExecutor(0, 2, 60L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

        indexCommit = new IndexCommit(searchId, config, executor, spaceLock);
        indexBackup = new IndexBackup(searchId, config, executor, spaceLock);
        indexRedoLog = new IndexRedoLog(searchId, config);

        indexRecover.recover(getMaster(), indexVersion);
        indexSpace.getSlave().init(searchId, config);
        getMaster().init(searchId, config);
    }

    /**
     * 切换时, master直接改变引用, 不需要lock
     *
     * @return
     */
    public IndexSpace getMaster() {
        return indexSpace.getMaster();
    }

    /**
     * 切换时, slaver在master之后改变, 需要lock
     *
     * @return
     */
    public IndexSpace getSlaver() {
        try {
            spaceLock.lock();
            return indexSpace.getSlave();
        } finally {
            spaceLock.unlock();
        }
    }

    public NRTSearcher getSearcher() {
        IndexSpace master = getMaster();
        try {
            return new NRTSearcher(master, master.getSearcher());
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }


    public IndexVersion getIndexVersion() {
        return indexVersion;
    }


    public IndexRedoLog getIndexRedoLog() {
        return indexRedoLog;
    }

    public void add(Document... docs) throws IOException {
        this.getMaster().add(docs);
    }

    public void update(Document... docs) throws IOException {
        this.getMaster().update(docs);
    }

    public void delete(String... ids) throws IOException {
        this.getMaster().delete(ids);
    }

    public void close() throws IOException {
        this.executor.shutdown();
        this.getSlaver().close();
        this.getMaster().close();
        this.log.info(searchId + " closed");
    }

    public boolean switchOver() {
        try {
            spaceLock.lock();
            localIndex.switchOver();
            indexSpace.switchOver();
        } finally {
            spaceLock.unlock();
        }
        return true;
    }

    //TODO: 异步提交, 备份有问题, 版本号不好控制, 前期采用同步提交. 数据量大了再优化
    public void commit() {
//        indexCommit.asynCommit(getMaster(), indexVersion, true);
        indexCommit.commit(getMaster(), indexVersion);
//        try {
//            spaceLock.lock();
//            indexCommit.commit(getMaster(), indexVersion);
//        } finally {
//            spaceLock.unlock();
//        }
    }

    public void commitAndMerge() {
//        indexCommit.asynCommit(getMaster(), indexVersion, true);
        indexCommit.commit(getMaster(), indexVersion, true);
//        try {
//            spaceLock.lock();
//            indexCommit.commit(getMaster(), indexVersion);
//        } finally {
//            spaceLock.unlock();
//        }
    }

    public boolean backup() {
        return indexBackup.backup(getMaster(), indexVersion);
//        indexBackup.backup(getMaster(), indexVersion);
//        try {
//            spaceLock.lock();
//            indexBackup.backup(this.getMaster(), indexVersion);
//        } finally {
//            spaceLock.unlock();
//        }
    }

    public boolean backupSlaver() {
        return indexBackup.backup(getSlaver(), indexVersion);
    }

    public void aysnBackup(){
        indexBackup.aysnBackup(getMaster(), indexVersion);
    }
}
