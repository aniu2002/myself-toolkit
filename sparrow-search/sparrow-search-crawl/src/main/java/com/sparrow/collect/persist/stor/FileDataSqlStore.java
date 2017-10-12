package com.sparrow.collect.persist.stor;

import com.sparrow.collect.crawler.check.UrlCheck;
import com.sparrow.collect.orm.ParsedSql;
import com.sparrow.collect.persist.PersistConfig;
import com.sparrow.collect.persist.format.DataFormat;
import com.sparrow.collect.persist.format.FileRowFormat;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Administrator on 2017/7/29 0029.
 */
public class FileDataSqlStore extends SqlStore<String> {
    private UrlCheck urlCheck;
    private AtomicLong counter = new AtomicLong(0);

    public FileDataSqlStore(PersistConfig config) {
        super(config);
    }

    public UrlCheck getUrlCheck() {
        return urlCheck;
    }

    public void setUrlCheck(UrlCheck urlCheck) {
        this.urlCheck = urlCheck;
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
        String str = objects[1].toString();
        if (this.urlCheck.check(str))
            return;
        this.saveObject(objects);
        counter.incrementAndGet();
        this.urlCheck.add(str);
    }

    @Override
    public void close() {
        super.close();
        System.out.println(" - Counter : " + counter.get());
    }
}
