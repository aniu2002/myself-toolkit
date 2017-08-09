package com.sparrow.app.data.provider;

import com.sparrow.orm.sql.PreparedStatementSetter;

/**
 * Created by yuanzc on 2016/1/5.
 */
public class QueryStub {
    private String sql;
    private PreparedStatementSetter setter;

    public QueryStub() {
    }

    public QueryStub(String sql) {
        this.sql = sql;
    }

    public QueryStub(String sql, PreparedStatementSetter setter) {
        this.sql = sql;
        this.setter = setter;
    }

    public PreparedStatementSetter getSetter() {
        return setter;
    }

    public void setSetter(PreparedStatementSetter setter) {
        this.setter = setter;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
