package com.sparrow.collect.persist.stor;

import com.sparrow.collect.orm.ParsedSql;
import com.sparrow.collect.orm.jdbc.DataSourceConnectionFactory;
import com.sparrow.collect.orm.session.Session;
import com.sparrow.collect.orm.utils.NamedParameterUtils;
import com.sparrow.collect.persist.format.DataFormat;
import com.sparrow.collect.persist.PersistConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Project Name: sparrow-egg
 * Package Name: com.sparrow.collect.store
 * Author : YZC
 * Date: 2016/12/13
 * Time: 16:37
 */
public abstract class SqlStore<D> extends AbstractStore<D> {
    private Session session;
    private List<Object[]> saveList = new ArrayList(100);
    private final Lock lock = new ReentrantLock();
    protected DataFormat<D> format;
    protected String sql;

    public SqlStore(PersistConfig config) {
        super(config);
        this.session = new Session(new DataSourceConnectionFactory(config.getProps()));
        this.initializePersist(config);
    }

    protected void initializePersist(PersistConfig config) {
        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(config.getSql());
        if (parsedSql.hasNamedParas() && parsedSql.hasTraditionalParas())
            throw new RuntimeException("不能同时处理named参数和传统的'?'参数");
        this.sql = parsedSql.getActualSql();
        this.format = this.postHandleParsedSql(parsedSql, config);
    }

    protected abstract DataFormat<D> postHandleParsedSql(ParsedSql parsedSql, PersistConfig config);

    @Override
    public DataFormat<D> getFormat() {
        return this.format;
    }

    protected void saveObject(Object[] object) {
        if (object == null)
            return;
        boolean swap = false;
        lock.lock();
        this.saveList.add(object);
        if (this.saveList.size() >= 100) {
            swap = true;
        }
        lock.unlock();
        if (swap) {
            this.batchSave(this.sql, this.saveList);
            this.saveList.clear();
        }
    }

    void batchSave(String sql, List<Object[]> values) {
        this.session.batchExecute(sql, values);
    }

    public void close() {
        if (!this.saveList.isEmpty())
            this.batchSave(this.sql, this.saveList);
        session.close();
    }
}
