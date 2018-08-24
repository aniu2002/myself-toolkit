package com.sparrow.collect.index;

import com.sparrow.collect.backup.DefaultFullIndexBackup;
import com.sparrow.collect.backup.FullIndexBackup;
import com.sparrow.collect.space.IndexSpace;
import com.sparrow.collect.utils.StringUtils;
import com.sparrow.collect.website.SearchConfig;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.IndexWriter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 备份索引到HDFS上.
 * 后面考虑压缩后再备份.
 * Created by yaobo on 2014/11/19.
 */
public class IndexBackup {

    private Log log = LogFactory.getLog(IndexBackup.class);

    private String searchId;

    private SearchConfig config;

    private ExecutorService executor;

    private ReentrantLock lock;

    private int backupNumber = 200;

    public IndexBackup(String searchId, SearchConfig config, ExecutorService executor, ReentrantLock lock) {
        this.searchId = searchId;
        this.config = config;
        this.executor = executor;
        this.lock = lock;
        this.backupNumber = this.config.getInt("searcher.basesearch.index.increment.backup.num", backupNumber);
    }

    public boolean backup(final IndexSpace indexSpace, final IndexVersion indexVersion) {
        IndexVersion.IndexVersionSnapshot snapshot = indexVersion.createSnapshot();
        return this.backup(indexSpace, indexVersion, snapshot);
    }

    protected boolean backup(final IndexSpace indexSpace, final IndexVersion indexVersion, final IndexVersion.IndexVersionSnapshot snapshot) {
        long oldBakVersion = snapshot.getBackup();
        try {
            lock.lock();
            log.info("backup index " + searchId + ":" + snapshot.getRam() + " begin");
            long start = System.currentTimeMillis();
            long bakVersion = snapshot.getRam() / backupNumber * backupNumber;
            indexVersion.setBackup(bakVersion);
            bakupIndex(indexSpace, snapshot, bakVersion);
            long end = System.currentTimeMillis();
            log.info("backup index " + searchId + ":" + snapshot.getRam() + " end; waste = " + (end - start) + "ms");
            return true;
        } catch (Exception e) {
            indexVersion.setBackup(oldBakVersion);
            log.error("backup索引异常", e);
            return false;
        } finally {
            lock.unlock();
        }
    }

    public void aysnBackup(final IndexSpace indexSpace, final IndexVersion indexVersion) {
        IndexVersion.IndexVersionSnapshot snapshot = indexVersion.createSnapshot();
        this.aysnBackup(indexSpace, indexVersion, snapshot);
    }

    public void aysnBackup(final IndexSpace indexSpace, final IndexVersion indexVersion,  final IndexVersion.IndexVersionSnapshot snapshot) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                backup(indexSpace, indexVersion);
            }
        });
    }

    public void bakupIndex(IndexSpace indexSpace, IndexVersion indexVersion, long versionID) throws IOException {
//        long versionID = indexVersion.getRam();
        FullIndexBackup fiBakup = null;
        String bakDir = config.get("searcher.basesearch." + searchId + ".index.hdfs.bakdir");
        String currentBakup = null;
        try {
            // 同步本地最新全量索引到hdfs服务
            currentBakup = bakDir.endsWith("/") ? bakDir + versionID : bakDir + '/' + versionID;
            fiBakup = new DefaultFullIndexBackup();
            fiBakup.initFileSystem("/", config);
            if (getBakupIndexMaxVersionAndDelOld(fiBakup) <= versionID) {
                fiBakup.delDir(currentBakup, config);
//                fiBakup.copyFromLocalFile(false, true, indexSpace.getIndexPath(), currentBakup, config);
                fiBakup.copyFromLocalFile(false, true, indexSpace.getIndexPath(), currentBakup, config, new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        if (IndexWriter.WRITE_LOCK_NAME.equals(pathname.getName())) {
                            return false;
                        }
                        return true;
                    }
                });
            }
        } catch (Exception e) {
            log.error(e);
            if (!StringUtils.isNullOrEmpty(currentBakup)) {
                fiBakup.delDir(currentBakup, config);
            }
        } finally {
            fiBakup.close();
        }
    }

    public long getBakupIndexMaxVersionAndDelOld(FullIndexBackup fiBakup)
            throws IOException {
        String bakDir = config.get("searcher.basesearch." + searchId + ".index.hdfs.bakdir");
        if (StringUtils.isNullOrEmpty(bakDir)) {
            return 0l;
        }
        List<Path> list = fiBakup.getFilesDir(bakDir, config);
        if (list.size() == 0) {
            return 0l;
        }
        long[] versions = new long[list.size()];
        for (int i = 0; i < list.size(); i++) {
            if (StringUtil.isCharOrNumberString(list.get(i).getName())) {
                versions[i] = (Long.parseLong(list.get(i).getName()));
            }
        }
        // 删除多余的数据
        delOldVersion(fiBakup, bakDir, versions);
        return NumberUtils.max(versions);
    }

    protected void delOldVersion(FullIndexBackup fiBakup, String bakDir, long[] versions) {
        Arrays.sort(versions);
        int count = config.getInt("searcher.basesearch." + searchId + ".index.hdfs.bak.count", 5);
        int verCount = versions.length;
        if (verCount <= count) {
            return;
        }
        bakDir = bakDir.endsWith("/") ? bakDir : bakDir + '/';
        for (int i = 0; i < verCount - count; i++) {
            try {
                fiBakup.delDir(bakDir + versions[i], config);
            } catch (Exception e) {
//                log.error(e.getMessage());
            }
        }
    }
}
