package com.sparrow.collect.orm.session;

import com.sparrow.collect.orm.PreparedStatementSetter;
import com.sparrow.collect.orm.extractor.BeanResultExtractor;
import com.sparrow.collect.orm.extractor.ResultExtractor;
import com.sparrow.collect.orm.extractor.SingleColumnResultExtractor;
import com.sparrow.collect.orm.extractor.SingleExtractor;
import com.sparrow.collect.orm.jdbc.ConnectionFactory;
import com.sparrow.collect.orm.named.BeanParameterSource;
import com.sparrow.collect.orm.named.SqlParameterSource;
import com.sparrow.collect.orm.setter.ArgumentPreparedStatementSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public abstract class AbstractSession extends Session implements IAccess {
    public static final int MAX_ROWS = 200;
    static final Logger logger = LoggerFactory.getLogger(AbstractSession.class);

    public AbstractSession(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    public static <T> T requiredSingleResult(Collection<T> results) {
        int size = (results != null ? results.size() : 0);
        if (size == 0) {
            throw new RuntimeException("需要1条记录,但是实际是0条");
        }
        if (results.size() > 1) {
            throw new RuntimeException("需要1条记录,但是实际是" + size + "条");
        }
        return results.iterator().next();
    }

    public <T> T querySimple(String sql, Class<T> clazz) {
        List<T> t = this.doQueryList(sql, this.getSingleColumnExtractor(clazz));
        return requiredSingleResult(t);
    }

    public <T> T querySimple(String sql, SqlParameterSource parameterSource,
                             Class<T> clazz) {
        List<T> t = this.doQueryList(sql, parameterSource,
                this.getSingleColumnExtractor(clazz), MAX_ROWS);
        return requiredSingleResult(t);
    }

    public <T> T querySimple(String sql, Object values[], Class<T> clazz) {
        PreparedStatementSetter setter = null;
        if (values != null && values.length > 0)
            setter = new ArgumentPreparedStatementSetter(values);
        List<T> t = this.queryForList(sql, setter,
                this.getSingleColumnExtractor(clazz), MAX_ROWS);
        return requiredSingleResult(t);
    }

    public <T> T queryObject(String sql, Class<T> clazz) {
        List<T> t = this.queryList(sql, clazz);
        return requiredSingleResult(t);
    }

    public <T> T queryObject(String sql, SqlParameterSource parameterSource,
                             Class<T> clazz) {
        List<T> t = this.queryList(sql, parameterSource, clazz);
        return requiredSingleResult(t);
    }

    public <T> T queryObject(String sql, Object vals[], Class<T> clazz) {
        List<T> t = this.queryList(sql, vals, clazz);
        return requiredSingleResult(t);
    }

    <T> ResultExtractor<T> getSingleColumnExtractor(Class<T> clazz) {
        return new SingleColumnResultExtractor<T>(clazz);
    }

    public <T> List<T> queryList(String sql, Class<T> clazz) {
        return this.doQueryList(sql, new BeanResultExtractor<T>(clazz));
    }

    public <T> List<T> queryList(String sql,
                                 SqlParameterSource parameterSource, Class<T> clazz) {
        return this.doQueryList(sql, parameterSource,
                new BeanResultExtractor<T>(clazz), MAX_ROWS);
    }

    public <T> List<T> queryList(String sql, Object values[], Class<T> clazz) {
        PreparedStatementSetter setter = null;
        if (values != null && values.length > 0)
            setter = new ArgumentPreparedStatementSetter(values);
        return this.queryForList(sql, setter,
                new BeanResultExtractor<T>(clazz), MAX_ROWS);
    }

    public <T> List<T> queryList(String sql, Object values[],
                                 ResultExtractor<T> extractor) {
        PreparedStatementSetter setter = null;
        if (values != null && values.length > 0)
            setter = new ArgumentPreparedStatementSetter(values);
        return this.queryForList(sql, setter, extractor, MAX_ROWS);
    }

    public <T> List<T> queryList(String sql, Object values[],
                                 ResultExtractor<T> extractor, int size) {
        PreparedStatementSetter setter = null;
        if (values != null && values.length > 0)
            setter = new ArgumentPreparedStatementSetter(values);
        return this.queryForList(sql, setter, extractor, size);
    }

    protected <T> List<T> doQueryList(String sql, ResultExtractor<T> extractor) {
        return this.doQueryList(sql, null, extractor, MAX_ROWS);
    }

    protected <T> List<T> doQueryList(String sql,
                                      SqlParameterSource paramSource, ResultExtractor<T> extractor) {
        return this.doQueryList(sql, paramSource, extractor, MAX_ROWS);
    }

    protected <T> List<T> doQueryList(String sql,
                                      SqlParameterSource paramSource,
                                      ResultExtractor<T> extractor,
                                      int maxRow) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (this.isShowSql())
            logger.info("SQL: {}", this.formatSql(sql));

        if (maxRow < 1)
            maxRow = MAX_ROWS;
        try {
            ps = this.namedParameterOperate.createPreparedStatement(
                    this.getConnection(), sql, paramSource, true);
            if (maxRow > 0)
                ps.setMaxRows(maxRow);
            // stmt.setFetchSize(Integer.MIN_VALUE);
            // stmt.setFetchDirection(ResultSet.FETCH_REVERSE);
            rs = ps.executeQuery();
            // rs.absolute(0);
            List<T> list = this.wrapResult(rs, extractor);
            int n = (list != null) ? list.size() : 0;
            if (this.isShowSql())
                logger.info("Effects : {}", String.valueOf(n));
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            // e.getNextException().printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
        }
        return null;
    }

    protected <T> List<T> queryForList(String sql,
                                       PreparedStatementSetter setter, ResultExtractor<T> extractor) {
        return this.queryForList(sql, setter, extractor, MAX_ROWS);
    }

    protected <T> List<T> queryForList(String sql,
                                       PreparedStatementSetter setter, ResultExtractor<T> extractor,
                                       int maxRow) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (this.isShowSql())
            logger.info("SQL: {}", this.formatSql(sql));

        try {
            ps = this.namedParameterOperate.createPreparedStatement(
                    this.getConnection(), sql, setter, true);
            if (maxRow > 0)
                ps.setMaxRows(maxRow);
            // stmt.setFetchSize(Integer.MIN_VALUE);
            // stmt.setFetchDirection(ResultSet.FETCH_REVERSE);
            rs = ps.executeQuery();
            // rs.absolute(0);
            List<T> l = this.wrapResult(rs, extractor);
            int n = (l == null ? 0 : l.size());
            if (this.showSql)
                logger.info("Effects : {}", String.valueOf(n));
            return l;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
        }
    }

    private <T> List<T> wrapResult(ResultSet rs, ResultExtractor<T> extractor,
                                   int maxSize) throws SQLException {
        return extractor.extract(rs, maxSize);
    }

    private <T> List<T> wrapResult(ResultSet rs, ResultExtractor<T> extractor)
            throws SQLException {
        return this.wrapResult(rs, extractor, MAX_ROWS);
    }

    private <T> T wrapSingleResult(ResultSet rs, SingleExtractor<T> extractor)
            throws SQLException {
        if (rs.next())
            return extractor.singleExtract(rs);
        else
            return null;
    }

    public <T> T getObject(String sql, Object bean, Class<T> clazz) {
        return this.getObject(sql, new BeanParameterSource(bean),
                new BeanResultExtractor<T>(clazz));
    }

    public <T> T getObject(String sql, Object values[], Class<T> clazz) {
        return this.getObject(sql, new ArgumentPreparedStatementSetter(values),
                new BeanResultExtractor<T>(clazz));
    }

    public <T> T getObject(String sql, SqlParameterSource paramSource,
                           SingleExtractor<T> extractor) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (this.showSql)
            logger.info("SQL: {}", this.formatSql(sql));

        try {
            ps = this.namedParameterOperate.createPreparedStatement(
                    this.getConnection(), sql, paramSource);
            rs = ps.executeQuery();
            T t = this.wrapSingleResult(rs, extractor);
            int n = (t == null ? 0 : 1);
            if (this.showSql)
                logger.info("Effects : {}", n);
            return t;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    public <T> T getObject(String sql, PreparedStatementSetter setter,
                           SingleExtractor<T> extractor) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (this.showSql)
            logger.info("SQL: {}", this.formatSql(sql));

        try {
            ps = this.namedParameterOperate.createPreparedStatement(
                    this.getConnection(), sql, setter);
            rs = ps.executeQuery();
            T t = this.wrapSingleResult(rs, extractor);
            int n = (t == null ? 0 : 1);
            if (this.showSql)
                logger.info("Effects : {}", String.valueOf(n));
            return t;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
        }
    }

    public int saveAndGenerateKey(String sql, Object object, String key) {
        try {
            if (this.showSql)
                logger.info("SQL: {}", this.formatSql(sql));
            int n = this.namedParameterOperate.executeWithKeyGenerate(
                    this.getConnection(), sql, object, key);
            if (this.showSql)
                logger.info("Effects : {}", String.valueOf(n));
            return n;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public void saveWithNoId(String sql, Object object) {
        try {
            if (this.showSql)
                logger.info("SQL: {}", this.formatSql(sql));
            PreparedStatement ps = this.namedParameterOperate.createPreparedStatement(
                    this.getConnection(),
                    sql,
                    new BeanParameterSource(object));
            int n = ps.executeUpdate();
            if (this.showSql)
                logger.info("Effects : {}", String.valueOf(n));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
