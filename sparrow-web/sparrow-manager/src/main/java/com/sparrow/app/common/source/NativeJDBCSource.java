package com.sparrow.app.common.source;

import com.sparrow.orm.session.Session;
import com.sparrow.orm.session.SessionFactory;

import java.sql.Connection;
import java.util.List;

/**
 * Created by yuanzc on 2016/1/4.
 */
public class NativeJDBCSource extends DBSource {
    private ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<Connection>();
    private final SessionFactory sessionFactory;

    public NativeJDBCSource(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public boolean initialize() {
        return false;
    }

    @Override
    public <T> List<T> query(String script, Object data, Class<T> wrappedClass, int page, int limit) {
        if (this.sessionFactory == null) {
            throw new RuntimeException("session factory is null");
        }
        Session session = null;
        try {
            session = this.sessionFactory.openSession();
            this.connectionThreadLocal.set(session.getConnection());
            return super.query(script, data, wrappedClass, page, limit);
        } finally {
            this.connectionThreadLocal.set(null);
            if (session != null)
                session.close();
        }
    }

    @Override
    public <T> T getData(String script, Object data, Class<T> wrappedClass) {
        if (this.sessionFactory == null) {
            throw new RuntimeException("session factory is null");
        }
        try {
            this.connectionThreadLocal.set(this.sessionFactory.getConnection());
            return super.getData(script, data, wrappedClass);
        } finally {
            this.connectionThreadLocal.set(null);
        }
    }

    @Override
    public boolean destroy() {
        return false;
    }

    /**
     * 获取链接
     *
     * @return
     */
    protected Connection getConnection() {
        return this.connectionThreadLocal.get();
    }
}
