package com.sparrow.collect.index;

import com.sparrow.collect.space.IndexSpace;
import com.sparrow.collect.website.SearchConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by yaobo on 2014/11/19.
 */
public class IndexCommit {

    private Log log = LogFactory.getLog(IndexCommit.class);

    private String searchId;

    private SearchConfig config;

    private ExecutorService executor;

    private ReentrantLock lock;

    private int commitNumber = 20;

    public IndexCommit(String searchId, SearchConfig config, ExecutorService executor, ReentrantLock lock) {
        this.searchId = searchId;
        this.config = config;
        this.executor = executor;
        this.lock = lock;
        this.commitNumber = this.config.getInt("searcher.basesearch.index.increment.commit.num", commitNumber);
    }

    public void commit(final IndexSpace indexSpace, final IndexVersion indexVersion) {
        this.commit(indexSpace, indexVersion, false);
    }

    public void commit(final IndexSpace indexSpace, final IndexVersion indexVersion, final boolean merge) {
        final IndexVersion.IndexVersionSnapshot snapshot = indexVersion.createSnapshot();
        this.commit(indexSpace, indexVersion, merge, snapshot);
    }

    /**
     * 同步提交
     * @param indexSpace
     * @param indexVersion
     * @param merge
     */
    protected void commit(final IndexSpace indexSpace, final IndexVersion indexVersion, final boolean merge, IndexVersion.IndexVersionSnapshot snapshot) {
        try {
            lock.lock();
            long start = System.currentTimeMillis();
            log.info("commit index " + searchId + ":" + snapshot.getRam() + " begin");
            indexSpace.commit();
            if (merge) {
                long ms = System.currentTimeMillis();
                log.info("merge index" + searchId + ":" + snapshot.getRam()+ " begin");
                indexSpace.merge();
                long me = System.currentTimeMillis();
                log.info("merge index" + searchId + ":" + snapshot.getRam()+ " end; waste =" + (me - ms) + "ms");
            }
            //write version and master
            long diskVersion = snapshot.getRam() / commitNumber * commitNumber;
            //IndexCompelete.writeVersion(indexSpace.getIndexPath(), diskVersion, Contants.SWITH_OVER_UPDATE_ID_TAG);
            indexVersion.setDisk(diskVersion);
            long end = System.currentTimeMillis();
            log.info("commit index " + searchId + ":" + snapshot.getRam() + " end; waste = " + (end - start) + "ms");
        } catch (Exception e) {
            log.error("commit索引异常", e);
        } finally {
            lock.unlock();
        }
    }


    public void asynCommit(final IndexSpace indexSpace, final IndexVersion indexVersion) {
        this.asynCommit(indexSpace, indexVersion, false);
    }

    public void asynCommit(final IndexSpace indexSpace, final IndexVersion indexVersion, final boolean merge) {
        final IndexVersion.IndexVersionSnapshot snapshot = indexVersion.createSnapshot();
        this.asynCommit(indexSpace, indexVersion, merge, snapshot);
    }



    /**
     * 异步提交
     * @param indexSpace
     * @param indexVersion
     * @param merge
     */
    public void asynCommit(final IndexSpace indexSpace, final IndexVersion indexVersion, final boolean merge, final IndexVersion.IndexVersionSnapshot snapshot)     {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                commit(indexSpace, indexVersion, merge, snapshot);
            }
        });
    }
}
