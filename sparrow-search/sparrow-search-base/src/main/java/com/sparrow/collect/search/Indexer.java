package com.sparrow.collect.search;

import com.sparrow.collect.website.SearchConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Indexer implements Cloneable {
    SearchConfig config;
    private String searchID;
    protected Log log = LogFactory.getLog(this.getClass());
    protected volatile long id = 0;

    public Indexer() {
    }

    public void init()   {

    }

    public void setConfig(SearchConfig config) {
        this.config = config;
    }

    protected SearchConfig getConfig() {
        return this.config;
    }

    public Document getDocByRecord(IDataRecorder record, SearchConfig config) {
        Document doc = new Document();
        RecordToDocController.getRtoDController().getRtoDParser(
                config.get(Contants.getStringByArray(new String[]{Contants.SEARCH_PREFIX, searchID, Contants.SEARCH_SID_INDEX_RTODOC}))).parsed(doc, record, config, searchID);
        return doc;
    }

    /**
     * 解析数据出错, 跳过该跳记录
     *
     * @param config
     * @param records
     * @return
     */
    public Document[] getDocByRecords(Configuration config,
                                      IDataRecorder... records) {
        List<Document> list = new ArrayList<Document>();
        for (IDataRecorder dataRecord : records) {
            try {
                list.add(getDocByRecord(dataRecord, config));
            } catch (Exception e) {
                log.error("从IDataRecorder解析数据到Document出错, 丢弃该数据 : " + dataRecord.toString(), e);
            }
        }
        return list.toArray(new Document[]{});
    }

    protected SpaceController getSpaceController() {
        return SpaceController.getController();
    }

    public void setSearchID(String searchID) {
        this.searchID = searchID;
    }

    protected long getFullID() throws InterruptedException, IOException, KeeperException {
        long zkId = ZKContraller.getSearchZKID(config, searchID);
        return zkId;
    }

    protected long syncOnlineToLocalID() throws InterruptedException, IOException, KeeperException {
        id = ZKContraller.getSearchZKID(config, searchID);
        return id;
    }

    protected boolean syncIDLocalToOnline() throws ZKFullIDException {
        long result;
        String path = null;
        try {
            ZookeeperClient zkc = ZKContraller.getZKClient(config);
            path = ZKContraller.getSearchZKIDPath(config, searchID);
            zkc.autoIncreaseIdCustomer(path);
            result = ZKContraller.getSearchZKID(config, searchID);
            log.debug(new StringBuilder().append(searchID).append(" ").append(path).append(" id=").append(result).append(" local ID=").append(id));
            if (id == result) {
                return true;
            }
        } catch (KeeperException e) {
            e.printStackTrace();
            throw new ZKFullIDException(path + e.getMessage(), e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new ZKFullIDException(path + e.getMessage(), e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ZKFullIDException(path + e.getMessage(), e);
        }
        return false;
    }

    public String getSearchID() {
        return searchID;
    }

    protected byte[] gethbaseRowkey(String searchID, long id) {
        return new StringBuilder().append(searchID).append("-").append(id)
                .toString().getBytes();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public abstract void indexHandler(String searchID, Configuration config,
                                      IDataRecorder... records) throws IndexException;

    public abstract void remoteIncreHandler(String searchID, Configuration config,
                                            IDataRecorder... records) throws IndexException;

    public void remoteFullHandler(String searchID, Configuration config, String version) throws IndexException {

    }

    public void setRpcServerInfo(RPCServerInfo RPCSInfo) {
    }

    public RPCServerInfo getRpcServerInfo() {
        return null;
    }

    public static void getRecordID(IDataRecorder recorder, String idTag) {
        CharSequence id = recorder
                .getValues()
                .get(idTag).get(0);
        recorder.setId(id);
    }

    public void repair() throws IndexException {
//        log.debug("repair : " + getName());
    }

}
