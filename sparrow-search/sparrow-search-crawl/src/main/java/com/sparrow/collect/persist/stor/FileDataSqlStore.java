package com.sparrow.collect.persist.stor;

import com.sparrow.collect.orm.ParsedSql;
import com.sparrow.collect.persist.PersistConfig;
import com.sparrow.collect.persist.format.DataFormat;
import com.sparrow.collect.persist.format.FileRowFormat;

/**
 * Created by Administrator on 2017/7/29 0029.
 */
public class FileDataSqlStore extends SqlStore<String> {

    public FileDataSqlStore(PersistConfig config) {
        super(config);
    }

    @Override
    protected void initializePersist(PersistConfig config) {
        this.sql = config.getSql();
        this.format = this.postHandleParsedSql(null, config);
    }

    @Override
    protected DataFormat postHandleParsedSql(ParsedSql parsedSql, PersistConfig config) {
        return new FileRowFormat();
    }

    @Override
    protected void doSave(Object[] objects) {
        this.saveObject(objects);
    }
}
