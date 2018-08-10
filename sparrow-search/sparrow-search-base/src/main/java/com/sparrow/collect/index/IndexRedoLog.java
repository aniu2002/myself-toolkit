package com.sparrow.collect.index;

import com.dili.dd.searcher.basesearch.common.avro.IDataRecorder;
import com.dili.dd.searcher.basesearch.common.config.Contants;
import com.dili.dd.searcher.basesearch.common.constant.TableName;
import com.dili.dd.searcher.bsearch.common.monitor.DataMonitorService;
import com.dili.dd.searcher.common.hbase.HbaseCallback;
import com.dili.dd.searcher.common.hbase.HbaseTemplateFactory;
import com.dili.dd.searcher.common.hbase.exception.HbaseAccessException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by yaobo on 2014/11/19.
 */
public class IndexRedoLog {

    private Log log = LogFactory.getLog(IndexRedoLog.class);

    private String searchId;

    private Configuration config;

    public IndexRedoLog(String searchId, Configuration config) {
        this.searchId = searchId;
        this.config = config;
    }

    public synchronized boolean addLogs(final long version, final IDataRecorder... records) throws IOException {
        log.debug(searchId + "addLogs : " + version);
        String table = TableName.getRedoLogTableName(searchId);
        try {
            HbaseTemplateFactory.getHbaseTemplate().execute(table, new HbaseCallback() {
                @Override
                public Object doInHbase(HTableInterface hTable) throws Exception {
                    String family = config.getStrings(Contants.HBASE_LOG_RECORD_TABLE_FAMILYS)[0];
                    try {
                        for (IDataRecorder recorder : records) {
                            Put put = new Put(gethbaseRowkey(searchId, version));
                            addPut(family, put, recorder);
                            hTable.put(put);
//                            throw new IOException("test addLogs exception");
                        }
                    } catch (IOException e) {
                        log.error("addLogs error", e);
                        throw e;
                    }
                    return null;
                }
            });
        } catch (HbaseAccessException e) {
            DataMonitorService.monitorError("43", this.getClass(), "add_index_log", "保存事务日志异常");
            log.error("保存事务日志异常", e);
            throw new IOException(e);
        }
        return true;
    }

    protected byte[] gethbaseRowkey(String searchID, long id) {
        return new StringBuilder().append(searchID).append("-").append(id).toString().getBytes();
    }

    protected void addPut(String family, Put put, IDataRecorder recorder) {
        Set<CharSequence> set = recorder.getValues().keySet();
        List<CharSequence> list = null;
        StringBuilder sb = null;
        for (CharSequence cs : set) {
            list = recorder.getValues().get(cs);
            sb = new StringBuilder();
            for (CharSequence lcs : list) {
                sb.append(lcs).append(Contants.HBASE_DATA_SPLIT_TAG);
            }

            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            put.add(Bytes.toBytes(family), Bytes.toBytes(cs.toString()), Bytes.toBytes(sb.toString()));
        }
        put.add(Bytes.toBytes(family), Bytes.toBytes(Contants.RECORD_INDEX_ONLY_KEY_NAME), Bytes.toBytes(recorder.getId().toString()));
        put.add(Bytes.toBytes(family), Bytes.toBytes(Contants.RECORD_INDEX_OPER_TYPE_NAME), Bytes.toBytes(recorder.getOperType().toString()));
    }
}
