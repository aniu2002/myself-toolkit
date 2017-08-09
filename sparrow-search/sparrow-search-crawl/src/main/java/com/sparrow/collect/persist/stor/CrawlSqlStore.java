package com.sparrow.collect.persist.stor;

import com.sparrow.collect.orm.ParsedSql;
import com.sparrow.collect.persist.data.CrawlDataWrap;
import com.sparrow.collect.persist.format.CrawlFormat;
import com.sparrow.collect.persist.format.DataFormat;
import com.sparrow.collect.persist.PersistConfig;
import com.sparrow.collect.store.DataStoreFactory;

/**
 * Created by Administrator on 2017/7/29 0029.
 */
public class CrawlSqlStore extends SqlStore<CrawlDataWrap> {

    public CrawlSqlStore(PersistConfig config) {
        super(config);
    }

    @Override
    protected DataFormat<CrawlDataWrap> postHandleParsedSql(ParsedSql parsedSql, PersistConfig config) {
        return new CrawlFormat(DataStoreFactory.wrapParaNameIndexes(parsedSql), config.getFields());
    }

    @Override
    protected void doSave(Object[] objects) {
        this.saveObject(objects);
    }
}
