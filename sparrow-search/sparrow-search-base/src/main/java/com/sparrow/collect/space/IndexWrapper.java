package com.sparrow.collect.space;

import com.sparrow.collect.website.SearchConfig;
import com.sparrow.collect.search.NRTSearcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;

import java.io.IOException;

/**
 * master提供index,search服务, 读写操作的线程安全由lucene处理.
 * slaver提供全量索引服务, 然后会与master切换, 切换过程中不会阻塞master提供服务.
 * 索引的提交,备份,恢复由ReentrantLock保证线程安全.
 */
public class IndexWrapper {

    private Log log = LogFactory.getLog(IndexWrapper.class);

    private String searchId;

    private IndexSpace indexSpace;

    public IndexWrapper() {

    }

    public void init(String searchId, SearchConfig config) throws IOException {
        this.log.info(searchId + " init");
        this.searchId = searchId;
        String path = config.get(String.format("doc.%s.path", searchId));
        indexSpace = new DiskIndexSpace(path);
        indexSpace.initDirectory();
        indexSpace.init(searchId, config);
    }

    protected IndexSpace getMaster() {
        return this.indexSpace;
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
        this.getMaster().close();
        this.log.info(searchId + " closed");
    }
}
