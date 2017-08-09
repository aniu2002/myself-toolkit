package com.sparrow.collect.space;

/**
 * <B>Description</B> TODO <br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 * @createTime 2014年5月30日 下午4:18:27
 * @author tanghongjun
 */
public class Contants {
    
    
    public static final String getStringByArray(String... strs) {
        StringBuilder sBuilder = new StringBuilder();
        for (String str : strs) {
            sBuilder.append(str).append('.');
        }
        if (sBuilder.length()>1) {
            sBuilder.deleteCharAt(sBuilder.length()-1);
        }
        return sBuilder.toString();
    }
    
    public static final String SWITH_OVER_UPDATE_ID_TAG="swithOVer";
    
    public static final char HBASE_DATA_SPLIT_TAG='#';
 
    public static final String FULL_INDEX_BAK_TOOLS="searcher.basesearch.index.bak.tools.cla";
    public static final String IS_FULL_INDEX_BAK_TOOLS="searcher.basesearch.index.is.bak";
    public static final String RECORD_INDEX_ONLY_KEY_NAME="id";
    public static final String RECORD_INDEX_OPER_TYPE_NAME="operType";
    public static final String RECORD_INDEX_ONLY_ORDER_NAME="customOrder";

    public static final String SEARCH_PREFIX = "searcher.basesearch";
    /**
     * searcher.basesearch.$searchID.index.increment
     */
    public static final String SEARCH_INDEX_INCREMENT_OBJECT="index.increment";
    
    /**
     * searcher.basesearch.$searchID.index.full
     */
    public static final String SEARCH_INDEX_REMOTE_SERVER_OBJECT="index.remote.server";

    /**
     * searcher.basesearch.$searchID.index.full.remote.server
     */
    public static final String SEARCH_INDEX_FULL_REMOTE_SERVER_OBJECT="index.full.remote.server";
    
    /**
     * searcher.basesearch.$searchID.index.full
     */
    public static final String SEARCH_INDEX_FULL_OBJECT="index.full";
    /**
     * searcher.basesearch.$searchID.index.remote
     */
    public static final String SEARCH_INDEX_REMOTE_OBJECT="index.remote";
    /**
     * searcher.basesearch.$searchID.index.remote
     */
    public static final String SEARCH_INDEX_FULL_REMOTE_OBJECT="index.full.remote";
    /**
     * searcher.basesearch.$searchID.index.merge
     */
    public static final String SEARCH_INDEX_MERGE_OBJECT="index.merge";
    
    /**
     * searcher.basesearch.#searchID.ifield.$fieldName.analy.name
     */
    public static final String INDEX_FIELD_ANALYSI_NAME = "analy.name";
    
    /**
     * searcher.basesearch.$searchID.index.rtodoc.parser
     */
    public static final String SEARCH_SID_INDEX_RTODOC="index.rtodoc.parser";
    
    /**
     * searcher.basesearch.index.rtodoc.parser.
     */
    public static final String SEARCH_INDEX_RTODOC_PARSER_PREFIX=SEARCH_PREFIX + ".index.rtodoc.parser";
    /**
     * searcher.basesearch.index.rtodoc.parser.list
     */
    public static final String SEARCH_INDEX_RTODOC_PARSER_LIST=SEARCH_INDEX_RTODOC_PARSER_PREFIX + ".list";
    
    /**
     * searcher.basesearch.index.field.parser.
     */
    public static final String SEARCH_INDEX_FIELD_PARSE_PREFIX=SEARCH_PREFIX + ".index.field.parser";
    /**
     * searcher.basesearch.index.field.parser.list
     */
   public static final String SEARCH_INDEX_FIELD_PARSE_LIST=SEARCH_INDEX_FIELD_PARSE_PREFIX + ".list";
    
    public static final String KEYWORD = "keyword";
    
    /**
     * 1-10000，20000-30000
     */
    public static final String SEARCH_FULL_RANGE=".full.index.range";
    
    /**
     * searcher.basesearch.zk.servers
     */
    public static final String ZK_HOSTS=SEARCH_PREFIX + ".zk.servers";
    /**
     * searcher.basesearch.zk.timeout
     */
    public static final String ZK_TIMEOUT=SEARCH_PREFIX+".zk.timeout";
    
    /**
     * searcher.basesearch.zk.root.dir
     */
    public static final String ZK_ROOT_DIR=SEARCH_PREFIX+".zk.root.dir";
    /**
     * searcher.basesearch.$searchID.zk.lock.dir
     * searcher.basesearch.zk.lock.dir
     */
    public static final String ZK_LOCK_DIR=SEARCH_PREFIX+".zk.lock.dir";
    /**
     * searcher.basesearch.$searchID.zk.id.node
     * searcher.basesearch.zk.id.node
     */
    public static final String ZK_ID_NODE=SEARCH_PREFIX+".zk.id.node";
    
    /**
     * searchList
     */
    public static final String ZK_SEARCH_ITEM_LIST_DIR="searchList";

    public static final String ZK_SEARCH_NODE_DIR="searchNode";

    public static final String ZK_INDEX_NODE_DIR="indexNode";

    public static final String ZK_FULL_LOCK_NODE_DIR = "fullLock";

    public static final String ZK_INCREMENT_LOCK_NODE_DIR = "incrementLock";

    /**
     * searcher.basesearch.hbase.table.pool.count
     */
    public static final String HBASE_TABLE_POOL_COUNT=SEARCH_PREFIX+".hbase.table.pool.count";
    /**
     *  searcher.basesearch.hbase.index.log.table
     */
    public static final String HBASE_ADD_RECORD_TABLE_NAME=SEARCH_PREFIX+".hbase.index.log.table";
    /**
     *  searcher.basesearch.hbase.index.log.familys
     */
    public static final String HBASE_LOG_RECORD_TABLE_FAMILYS=SEARCH_PREFIX+".hbase.index.log.familys";
    
    /**
     * searcher.basesearch.#searchID.ifield.strategy.#
     */
    public static final String STRATEGY_TAG = "strategy";
    public static final String STRATEGY_TAG_PREFIX = SEARCH_PREFIX
            + '.' +STRATEGY_TAG;
    
  

    public static final String STRATEGY_LIST_TAG = STRATEGY_TAG_PREFIX + "list";
    
   public static final String STRING_PROCESS_TAG = "string.process";
    
    /**
     * 
     */
    public static final String STRING_PROCESS_TAG_PREFIX = SEARCH_PREFIX
            + '.' +STRING_PROCESS_TAG ;

    /**
     * searcher.basesearch.string.process.list
     */
    public static final String STRING_PROCESS_LIST_TAG = STRING_PROCESS_TAG_PREFIX + ".list";

    /**
     * searcher.basesearch.searchID.list
     */
    public static final String SEARCH_LIST_TAG = SEARCH_PREFIX
            + ".searchID.list";

    /**
     * searcher.basesearch.#searchID.ifield.list
     */
    public static final String SEARCHID_FIELD_LIST_TAG = "ifield.list";
    
    /**
     * ifield
     */
    public static final String SEARCHID_FIELD_TAG= "ifield";
    
    /**
     * searcher.basesearch.#searchID.ifield.#ifieldName.clas
     */
    public static final String SEARCHID_FIELD_CLA_LIST="clas";
    
    

    public static final String SEARCH_FIELD_DATA_SOURCE_TAG = "datasource";

    public static final String SEARCH_FIELD_STRATEGYS = "strategys";

    /**
     * searcher.basesearch.$searchID.is.ram
     */
    public static final String SEARCH_IS_SPACE_RAM = "is.ram";

    /**
     * searcher.basesearch.$searchID.disk.master
     */
    public static final String SEARCH_SPACE_DISK_MASTER = "disk.master";
    /**
     * searcher.basesearch.$searchID.disk.slave
     */
    public static final String SEARCH_SPACE_DISK_SLAVE = "disk.slaver";
    /**
     * searcher.basesearch.$searchID.ram.disk.master
     */
    public static final String SEARCH_SPACE_RAM_DISK_MASTER = "ram.disk.master";
    /**
     * searcher.basesearch.$searchID.ram.disk.slaver
     */
    public static final String SEARCH_SPACE_RAM_DISK_SLAVE = "ram.disk.slaver";
    
    /**
     * searcher.basesearch.$searchID.ram.max.size.KB
     */
    public static final String SEARCH_RAM_SIZE_TAG="ram.max.size.KB";
    
    public static final String SEARCH_ANALYZER_PREFIX="analyzer";
    
    /**
     * searcher.basesearch.analyzer.list
     */
    public static final String SEARCH_ANALYZER_LIST=SEARCH_ANALYZER_PREFIX + ".list";
    
    public static final String CLASS_TAG="cla";
    
    public static final String SEARCH_CONFIG_OBSERVER_TAG=SEARCH_PREFIX+".config.Observable.list";
    
    /**
     * searcher.basesearch.$searchID.record.config.path
     */
    public static final String SEARCH_RECORD_CONFIG_PATH="record.config.path";
    
    /**
     * searcher.basesearch.$searchID.record.data.parser.cla
     */
    public static final String SEARCH_RECORD_DATA_PARSER="record.data.parser";
    
    /**
     * searcher.basesearch.index.rpc.server.hosts
     */
    public static final String SEARCH_RPC_INDEX_HOSTS= "index.rpc.server.hosts";
    
    /**
     * searcher.basesearch.$searchID.index.rpc.services.host
     */
    public static final String SEARCH_RPC_INDEX_SERVICES_HOST="index.rpc.services.host";
    
    /**
     * searcher.basesearch.$searchID.index.rpc.port
     */
    public static final String SEARCH_RPC_INDEX_PORT="index.rpc.port";
    
    /**
     * searcher.basesearch.$searchID.index.rpc.boss.count
     */
    public static final String SEARCH_RPC_INDEX_BOSS_COUNT="index.rpc.boss.count";
    
    /**
     * searcher.basesearch.$searchID.index.rpc.work.count
     */
    public static final String SEARCH_RPC_INDEX_WORK_COUNT="index.rpc.work.count";
    
    /**
     * searcher.basesearch.$searchID.index.rpc.connection.timeout.millis
     */
    public static final String SEARCH_RPC_INDEX_CONNECTION_TIMEOUT="index.rpc.connection.timeout.millis";
    
    /**
     * searcher.basesearch.$searchID.index.rpc.call.timeout.millis
     */
    public static final String SEARCH_RPC_INDEX_CALL_TIMEOUT="index.rpc.call.timeout.millis";
    
    /**
     * sindex.mq.username
     */
    public static final String INDEX_MQ_USERNAME="index.mq.username";
    
    /**
     * index.mq.passwd
     */
    public static final String INDEX_MQ_PASSWORD="index.mq.passwd";
    
    /**
     * index.mq.url
     */
    public static final String INDEX_MQ_URL="index.mq.url";
    
    /**
     * index.mq.transacted
     */
    public static final String INDEX_MQ_TRANSACTED="index.mq.transacted";
     
    
    /**
     * index.mq.transacted
     */
    public static final String INDEX_MQ_ACKNOWLEDGE="index.mq.acknowledge";
    
    /**
     * index.mq.recerver.timeout
     */
    public static final String INDEX_MQ_RECERVER_TIMEOUT="index.mq.recerver.timeout";
    
    /**
     * sdata.key
     */
    public static final String INDEX_SOURCE_DATA_KEY_TAG="sdata.key";
    
}
