package com.sparrow.collect.index;

import com.dili.dd.searcher.basesearch.common.config.Contants;
import com.dili.dd.searcher.basesearch.common.spaceBak.DefaultFullIndexBakup;
import com.dili.dd.searcher.basesearch.common.spaceBak.FullIndexBakup;
import com.dili.dd.searcher.basesearch.common.spaceBak.IndexCompelete;
import com.dili.dd.searcher.basesearch.common.util.StringUtil;
import com.dili.dd.searcher.basesearch.common.util.ZKContraller;
import com.dili.dd.searcher.bsearch.common.space.IndexSpace;
import com.dili.dd.searcher.common.zk.ZookeeperClient;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 版本管理
 * ram:内存上的版本, 处理索引时, +1
 * zk:zk上的全局版本号, 处理索引时, +1
 * disk:磁盘上的版本, 备份索引时, disk=ram
 * back:备份到hdfs上的版本, 提交索引时, back=ram
 * <p/>
 * zk==ram>=disk>=back
 * Created by yaobo on 2014/11/19.
 */
public class IndexVersion {
    private Log log = LogFactory.getLog(IndexVersion.class);

    protected String searchId;

    protected Configuration config;

    protected AtomicLong ram;

    protected volatile long zk;

    protected volatile long disk;

    protected volatile long backup;

    private String indexPath;

    private ReentrantReadWriteLock lock;

    public IndexVersion() {
        this.ram = new AtomicLong();
    }

    public IndexVersion(String searchId, Configuration config, String indexPath) {
        this.searchId = searchId;
        this.config = config;
        this.indexPath = indexPath;
        lock = new ReentrantReadWriteLock();
        this.init();
    }

    public void init() {
        FullIndexBakup fiBakup = null;
        try {
            lock.writeLock().lock();
            fiBakup = new DefaultFullIndexBakup();
            this.ram = new AtomicLong();
            this.onlineSyncZK();
            this.disk = IndexCompelete.readVersion(indexPath, searchId, Contants.SWITH_OVER_UPDATE_ID_TAG);
            this.diskSyncRam();
            this.backup = IndexCompelete.readBakupIndexMaxVersion(searchId, config, fiBakup);
        } finally {
            fiBakup.close();
            lock.writeLock().unlock();
        }
    }

    public long getRam() {
        try {
            lock.readLock().lock();
            return ram.get();
        } finally {
            lock.readLock().unlock();
        }
    }

    public long getDisk() {
        try {
            lock.readLock().lock();
            return disk;
        } finally {
            lock.readLock().unlock();
        }

    }

    public long getZk() {
        try {
            lock.readLock().lock();
            return zk;
        } finally {
            lock.readLock().unlock();
        }

    }

    public long getBackup() {
        try {
            lock.readLock().lock();
            return backup;
        } finally {
            lock.readLock().unlock();
        }
    }

    public long incrRam() {
        try {
            lock.writeLock().lock();
            return ram.incrementAndGet();
        } finally {
            lock.writeLock().unlock();
        }

    }

    public long incrZKWithOnline() {
        try {
            lock.writeLock().lock();
            ZookeeperClient zkc = ZKContraller.getZKClient(config);
            String path = ZKContraller.getSearchZKIDPath(config, searchId);
            zkc.autoIncreaseIdCustomer(path);
            this.zk = zkc.getIdCustomer(path);
        } catch (InterruptedException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        } catch (KeeperException e) {
            log.error(e);
        } finally {
            lock.writeLock().unlock();
        }
        return zk;
    }

    public long incrZKWithoutOnline() {
        try {
            lock.writeLock().lock();
            this.zk++;
        } finally {
            lock.writeLock().unlock();
        }
        return zk;
    }

    public long onlineSyncZK() {
        try {
            lock.writeLock().lock();
            zk = ZKContraller.getSearchZKID(config, searchId);
        } catch (InterruptedException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        } catch (KeeperException e) {
            log.error(e);
        } finally {
            lock.writeLock().unlock();
        }
        return this.zk;
    }

    public long zkSyncRam() {
        try {
            lock.writeLock().lock();
            this.ram.set(zk);
            return this.ram.get();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void ramSyncDisk() {
        try {
            lock.writeLock().lock();
            disk = ram.get();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void diskSyncRam() {
        try {
            lock.writeLock().lock();
            ram.set(disk);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void ramSyncBackup() {
        try {
            lock.writeLock().lock();
            backup = ram.get();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void setDisk(long disk) {
        try {
            lock.writeLock().lock();
            this.disk = disk;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void setBackup(long backup) {
        try {
            lock.writeLock().lock();
            this.backup = backup;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public IndexVersionSnapshot createSnapshot() {
        try {
            lock.readLock().lock();
            return new IndexVersionSnapshot(this);
        } finally {
            lock.readLock().unlock();
        }
    }


    public class IndexVersionSnapshot extends IndexVersion {

        private IndexVersionSnapshot(IndexVersion version) {
            this.ram = new AtomicLong();
            this.ram.set(version.ram.get());
            this.zk = version.zk;
            this.disk = version.disk;
            this.backup = version.backup;
        }

        @Override
        public void init() {
            throw new RuntimeException("IndexVersionSnapshot can't invoke init()");
        }

        @Override
        public long getRam() {
            return this.ram.get();
        }

        @Override
        public long getDisk() {
            return this.disk;
        }

        @Override
        public long getZk() {
            return this.zk;
        }

        @Override
        public long getBackup() {
            return this.backup;
        }

        @Override
        public long incrRam() {
            throw new RuntimeException("IndexVersionSnapshot can't invoke incrRam()");
        }

        @Override
        public long incrZKWithOnline() {
            throw new RuntimeException("IndexVersionSnapshot can't invoke incrZKWithOnline()");
        }

        @Override
        public long incrZKWithoutOnline() {
            throw new RuntimeException("IndexVersionSnapshot can't invoke incrZKWithoutOnline()");
        }

        @Override
        public long onlineSyncZK() {
            throw new RuntimeException("IndexVersionSnapshot can't invoke onlineSyncZK()");
        }

        @Override
        public void diskSyncRam() {
            throw new RuntimeException("IndexVersionSnapshot can't invoke diskSyncRam()");
        }

        @Override
        public void setDisk(long disk) {
            throw new RuntimeException("IndexVersionSnapshot can't invoke setDisk()");
        }

        @Override
        public void setBackup(long backup) {
            throw new RuntimeException("IndexVersionSnapshot can't invoke setBackup()");
        }

        @Override
        public IndexVersionSnapshot createSnapshot() {
            throw new RuntimeException("IndexVersionSnapshot can't invoke createSnapshot()");
        }

        @Override
        public long zkSyncRam() {
            throw new RuntimeException("IndexVersionSnapshot can't invoke zkSyncRam()");
        }

        @Override
        public void ramSyncDisk() {
            throw new RuntimeException("IndexVersionSnapshot can't invoke ramSyncDisk()");
        }

        @Override
        public void ramSyncBackup() {
            throw new RuntimeException("IndexVersionSnapshot can't invoke ramSyncBackup()");
        }
    }

}
