package com.sparrow.collect.index;

import com.sparrow.collect.backup.DefaultFullIndexBackup;
import com.sparrow.collect.backup.FullIndexBackup;
import com.sparrow.collect.backup.IndexComplete;
import com.sparrow.collect.space.Contants;
import com.sparrow.collect.space.IndexSpace;
import com.sparrow.collect.website.SearchConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by yaobo on 2014/11/19.
 */
public class IndexRecover {

    private Log log = LogFactory.getLog(IndexRecover.class);

    private String searchId;

    private SearchConfig config;

    public IndexRecover(String searchId, SearchConfig config) {
        this.searchId = searchId;
        this.config = config;
    }

    public synchronized boolean recover(IndexSpace indexSpace, IndexVersion indexVersion) throws IOException {
        FullIndexBackup fiBakup = null;
        String bakDir = config.get("searcher.basesearch." + searchId + ".index.hdfs.bakdir");
        String syncBakup = null;
        try {
            fiBakup = new DefaultFullIndexBackup();
            fiBakup.initFileSystem("/" );
            long onlineMaxVersion = IndexComplete.readBakupIndexMaxVersion(searchId, config, fiBakup);
            if (onlineMaxVersion <= indexVersion.getRam() || onlineMaxVersion == 0) {
                return true;
            }
            log.info("zk version = " + onlineMaxVersion + " and ram version = " + indexVersion.getRam());
            log.info("recover index : " + searchId);
            // 从hdfs上读取最新的索引, 将master和slaver都设置为最新index
            syncBakup = bakDir.endsWith("/") ? bakDir + onlineMaxVersion : bakDir + '/' + onlineMaxVersion;

            File masterDirectory = new File(indexSpace.getIndexPath());
            FileUtils.deleteDirectory(masterDirectory);
            masterDirectory.mkdirs();
            fiBakup.copyToLocalFile(false, syncBakup, indexSpace.getIndexPath() );

            indexVersion.setDisk(onlineMaxVersion);
            indexVersion.diskSyncRam();
            IndexComplete.writeVersion(indexSpace.getIndexPath(), indexVersion.getDisk(), Contants.SWITH_OVER_UPDATE_ID_TAG);
            log.info("Copy HDFS bakIndex ToLocalFile : " + indexSpace.getIndexPath());
            return true;
        } catch (Exception e) {
            log.error("recover error", e);
            throw e;
        } finally {
            fiBakup.close();
        }
    }
}
