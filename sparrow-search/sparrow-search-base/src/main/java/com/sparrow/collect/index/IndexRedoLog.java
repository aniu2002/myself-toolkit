package com.sparrow.collect.index;

import com.sparrow.collect.website.SearchConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * Created by yaobo on 2014/11/19.
 */
public class IndexRedoLog {

    private Log log = LogFactory.getLog(IndexRedoLog.class);

    private String searchId;

    private SearchConfig config;

    public IndexRedoLog(String searchId, SearchConfig config) {
        this.searchId = searchId;
        this.config = config;
    }

    public synchronized boolean addLogs(final long version) throws IOException {
        log.debug(searchId + "addLogs : " + version);

        return true;
    }

    protected byte[] gethbaseRowkey(String searchID, long id) {
        return new StringBuilder().append(searchID).append("-").append(id).toString().getBytes();
    }

}
