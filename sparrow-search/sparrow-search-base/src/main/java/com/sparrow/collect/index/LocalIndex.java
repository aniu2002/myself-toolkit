package com.sparrow.collect.index;

import com.dili.dd.searcher.basesearch.common.bean.MSBean;
import com.dili.dd.searcher.basesearch.common.config.Contants;
import com.dili.dd.searcher.basesearch.common.spaceBak.IndexCompelete;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by yaobo on 2014/11/19.
 */
public class LocalIndex {

    private Log log = LogFactory.getLog(LocalIndex.class);

    private String searchId;

    private Configuration config;

    private MSBean<String> indexPath;

    private ReentrantReadWriteLock lock;

    public LocalIndex(String searchId, Configuration config) {
        this.searchId = searchId;
        this.config = config;
        this.lock = new ReentrantReadWriteLock();
        init();
        log.info(searchId + " master =" + indexPath.getMaster() + "; and slaver = " + indexPath.getSlave());
    }

    public String getMaster(){
        try {
            lock.readLock().lock();
            return indexPath.getMaster();
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getSlaver(){
        try {
            lock.readLock().lock();
            return indexPath.getSlave();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 切换, 由space保证线程安全
     */
    public void switchOver(){
        try {
            lock.writeLock().lock();
            indexPath.switchOver();
        } finally {
            lock.writeLock().unlock();
        }
    }


    private void init(){
        try {
            lock.writeLock().lock();
            String master = config.get(new StringBuilder().append("searcher.basesearch.").append(searchId).append(".disk.master").toString());
            String slaver = config.get(new StringBuilder().append("searcher.basesearch.").append(searchId).append(".disk.slaver").toString());
            indexPath = new MSBean<>(master, slaver);

            File masterFile = new File(this.indexPath.getMaster());
            if (!masterFile.exists()) {
                masterFile.mkdirs();
            }

            File slaverFile = new File(this.indexPath.getSlave());
            if (!slaverFile.exists()) {
                slaverFile.mkdirs();
            }

            chooseMaster();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 通过versionFile中记录的indexPath, 判断master与slaver
     */
    private void chooseMaster(){
        try {
            String lastIndexPath = IndexCompelete.readVersionIndexPath(this.indexPath.getMaster(), searchId, Contants.SWITH_OVER_UPDATE_ID_TAG);
            if (indexPath.getMaster().equals(lastIndexPath)){

            }else if (indexPath.getSlave().equals(lastIndexPath)){
                indexPath.switchOver();
            }else{
                chooseMasterByLastTime();
            }
        } catch (Exception e) {
            log.error(e);
            chooseMasterByLastTime();
        }
    }

    /**
     * 没有版本文件, 文件中的路径与master, slaver不一致, 按最近更新时间判断
     */
    private void chooseMasterByLastTime(){
        File master = new File(indexPath.getMaster());
        File slaver = new File(indexPath.getSlave());

        if (master.lastModified() < slaver.lastModified()){
            indexPath.switchOver();
        }
    }


}
