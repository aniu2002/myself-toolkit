package com.sparrow.collect.orm.session;

import com.sparrow.collect.orm.PreparedStatementSetter;
import com.sparrow.collect.orm.extractor.*;
import com.sparrow.collect.orm.jdbc.ConnectionFactory;
import com.sparrow.collect.orm.named.BeanParameterSource;
import com.sparrow.collect.orm.named.LazyBeanParameterSource;
import com.sparrow.collect.orm.named.NamedParameterOperate;
import com.sparrow.collect.orm.named.SqlParameterSource;
import com.sparrow.collect.orm.setter.ArgumentPreparedStatementSetter;
import com.sparrow.collect.orm.utils.Formatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.sql.*;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Session implements IAccess {
    public static final int MAX_ROWS = 200;
    static final Logger logger = LoggerFactory.getLogger(Session.class);
    private Connection connection;
    private Object syncObject = new Object();
    private ConnectionFactory connectionFactory;

    private Map<Class<?>, AbstractResultExtractor<?>> extractors;
    private NamedParameterOperate namedParameterOperate;

    private String dbType;
    private boolean showSql;
    private boolean formatSql;

    public Session(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.dbType = connectionFactory.getDatabaseType();
        this.namedParameterOperate = new NamedParameterOperate(this.connectionFactory.isShowSql());
        this.extractors = new ConcurrentHashMap<Class<?>, AbstractResultExtractor<?>>();
        this.showSql = this.connectionFactory.isShowSql();
        this.formatSql = this.connectionFactory.isFormatSql();
    }

    public ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }

    public Map<Class<?>, AbstractResultExtractor<?>> getExtractors() {
        return extractors;
    }

    public NamedParameterOperate getNamedParameterOperate() {
        return namedParameterOperate;
    }

    public String getDbType() {
        return dbType;
    }

    public boolean isClosed() {
        return closed;
    }

    public Connection getConnection() {
        if (this.connection == null) {
            synchronized (syncObject) {
                if (this.connection == null) {
                    try {
                        this.connection = connectionFactory.getConnection();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e.getMessage());
                    }
                }
            }
        }
        return this.connection;
    }

    public boolean isShowSql() {
        return showSql;
    }

    public void setShowSql(boolean showSql) {
        this.showSql = showSql;
    }

    public boolean isFormatSql() {
        return formatSql;
    }

    public void setFormatSql(boolean formatSql) {
        this.formatSql = formatSql;
    }

    @SuppressWarnings("unchecked")
    public <T> AbstractResultExtractor<T> getResultExtractor(Class<T> clazz) {
        return (AbstractResultExtractor<T>) this.extractors.get(clazz);
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

        if (this.showSql)
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
            if (this.showSql)
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

    protected boolean hasResult(String sql, Object values[]) {
        PreparedStatementSetter setter = null;
        if (values != null && values.length > 0)
            setter = new ArgumentPreparedStatementSetter(values);
        return this.hasResult(sql, setter);
    }

    protected boolean hasResult(String sql, PreparedStatementSetter setter) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (this.showSql)
            logger.info("SQL: {}", this.formatSql(sql));
        try {
            ps = this.namedParameterOperate.createPreparedStatement(this.getConnection(), sql, setter, true);
            rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0)
                return true;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null)
                    ps.close();
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
        }
        return false;
    }

    protected <T> List<T> queryForList(String sql,
                                       PreparedStatementSetter setter, ResultExtractor<T> extractor,
                                       int maxRow) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (this.showSql)
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
            // System.out.println(e.getMessage());//
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

    protected String formatSql(String sql) {
        if (this.formatSql)
            return new Formatter(sql).format();
        else
            return sql;
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

    public Object execWithKeyGen(String sql, Object args[], String key) {
        try {
            if (this.showSql)
                logger.info("SQL: {}", this.formatSql(sql));
            Long n = (Long) this.namedParameterOperate.exeWithKeyGen(
                    this.getConnection(), sql, args, key);
            if (this.showSql)
                logger.info("Effects : {}", String.valueOf(n));
            return n;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public int execute(String sql) {
        int n;
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            if (this.showSql)
                logger.info("SQL: {}", this.formatSql(sql));
            n = pstmt.executeUpdate();
            if (this.showSql)
                logger.info("Effects : {}", String.valueOf(n));
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return n;
    }

    public int execute(String sql, Object object) {
        return this.execute(sql, new BeanParameterSource(object));
    }

    public int execute(String sql, SqlParameterSource paramSource) {
        PreparedStatement ps = null;
        if (this.showSql)
            logger.info("SQL: {}", this.formatSql(sql));
        try {
            int n = 0;
            ps = this.namedParameterOperate.createPreparedStatement(
                    this.getConnection(), sql, paramSource);
            n = ps.executeUpdate();
            if (this.showSql)
                logger.info("Effects : {}", String.valueOf(n));
            return n;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
            // System.out.println(e.getMessage());
            // e.getNextException().printStackTrace();
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
        }
    }

    public int execute(String sql, PreparedStatementSetter setter) {
        PreparedStatement ps = null;
        if (this.showSql)
            logger.info("SQL: {}", this.formatSql(sql));
        try {
            int n = 0;
            ps = this.namedParameterOperate.createPreparedStatement(
                    this.getConnection(), sql, setter);
            n = ps.executeUpdate();
            if (this.showSql)
                logger.info("Effects : {}", String.valueOf(n));
            return n;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
            // System.out.println(e.getMessage());
            // e.getNextException().printStackTrace();
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
        }
    }

    public int execute(String sql, Object values[]) {
        return this.execute(sql, new ArgumentPreparedStatementSetter(values));
    }

    void preparedStatementSet(PreparedStatement ps, int paramIndex,
                              Object inValue) throws SQLException {
        if (isStringValue(inValue.getClass())) {
            ps.setString(paramIndex, inValue.toString());
        } else if (isSqlTimestampValue(inValue.getClass())) {
            ps.setTimestamp(paramIndex, (Timestamp) inValue);
        } else if (isSqlDateValue(inValue.getClass())) {
            ps.setDate(paramIndex, (Date) inValue);
        } else if (isSqlTimeValue(inValue.getClass())) {
            ps.setTime(paramIndex, (Time) inValue);
        } else if (isDateValue(inValue.getClass())) {
            ps.setTimestamp(paramIndex, new java.sql.Timestamp(
                    ((java.util.Date) inValue).getTime()));
        } else if (inValue instanceof Calendar) {
            Calendar cal = (Calendar) inValue;
            ps.setTimestamp(paramIndex, new java.sql.Timestamp(cal.getTime()
                    .getTime()), cal);
        } else {
            // Fall back to generic setObject call without SQL type specified.
            ps.setObject(paramIndex, inValue);
        }
    }

    private static boolean isDateValue(Class<?> inValueType) {
        return java.util.Date.class.isAssignableFrom(inValueType);
    }

    private static boolean isSqlTimeValue(Class<?> inValueType) {
        return java.sql.Time.class.isAssignableFrom(inValueType);
    }

    private static boolean isSqlDateValue(Class<?> inValueType) {
        return java.sql.Date.class.isAssignableFrom(inValueType);
    }

    private static boolean isSqlTimestampValue(Class<?> inValueType) {
        return java.sql.Timestamp.class.isAssignableFrom(inValueType);
    }

    private static boolean isStringValue(Class<?> inValueType) {
        return (CharSequence.class.isAssignableFrom(inValueType) || StringWriter.class
                .isAssignableFrom(inValueType));
    }

    public int batchExecute(String[] sqls) {
        if (this.showSql)
            logger.info("SQL: {}", this.formatSql(sqls[0]));
        int effects;
        try {
            Statement pstmt = this.getConnection().createStatement();
            for (String sql : sqls) {
                pstmt.addBatch(sql);
            }
            int n[] = pstmt.executeBatch();
            effects = this.getEffects(n);
            if (this.showSql)
                logger.info("Effects : {}", String.valueOf(n));
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return effects;
    }

    public int batchExecuteSimple(String sql, List<?> values) {
        if (this.showSql)
            logger.info("SQL: {}", this.formatSql(sql));
        int effects = 0;
        try {
            PreparedStatement pstmt = this.getConnection()
                    .prepareStatement(sql);
            for (Object o : values) {
                this.preparedStatementSet(pstmt, 1, o);
                pstmt.addBatch();
            }
            int n[] = pstmt.executeBatch();
            effects = this.getEffects(n);
            if (this.showSql)
                logger.info("Effects : {}", String.valueOf(n));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return effects;
    }

    public int batchExecute(String sql, List<Object[]> values) {
        if (this.showSql)
            logger.info("SQL: {}", this.formatSql(sql));
        int effects = 0;
        try {
            PreparedStatement pstmt = this.getConnection()
                    .prepareStatement(sql);
            for (Object vals[] : values) {
                for (int i = 0; i < vals.length; i++)
                    this.preparedStatementSet(pstmt, i + 1, vals[i]);
                pstmt.addBatch();
            }
            int n[] = pstmt.executeBatch();
            effects = this.getEffects(n);
            if (this.showSql)
                logger.info("Effects : {}", String.valueOf(effects));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return effects;
    }

    public int batchExecute(String sql, SqlParameterSource[] paras) {
        PreparedStatement ps = null;
        if (this.showSql)
            logger.info("SQL: {}", this.formatSql(sql));
        try {
            int n = 0;
            ps = this.namedParameterOperate.createPreparedStatement(
                    this.getConnection(), sql, paras);
            int r[] = ps.executeBatch();
            n = this.getEffects(r);
            if (this.showSql)
                logger.info("Effects : {}", String.valueOf(n));
            return n;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
            // System.out.println(e.getMessage());
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
        }
    }

    protected int getEffects(int efs[]) {
        int i = 0;
        for (int e : efs) {
            if (this.connectionFactory.isOracle()) {
                if (e == Statement.SUCCESS_NO_INFO) // for oracle
                    i++;
            } else
                i += e;
        }
        return i;
    }

    public int executeUpdate(String sql) {
        try {
            PreparedStatement pstmt = null;
            pstmt = connection.prepareStatement(sql);

            if (this.showSql)
                logger.info("SQL: {}", this.formatSql(sql));
            // 批量执行
            // pstmt.setTimestamp(i + 1, );
            // pstmt.addBatch();
            int n = pstmt.executeUpdate();// pstmt.executeBatch();
            // this.connection.commit();
            if (this.showSql)
                logger.info("Effects : {}", String.valueOf(n));
            // this.connection.setAutoCommit(autoComit);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public void close() {
        if (this.closed)
            return;
        this.closed = true;
        this.extractors = null;
        this.namedParameterOperate = null;
/*        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        if (this.connectionFactory != null) {
            this.connectionFactory.destroy();
            this.connectionFactory = null;
        }
        this.connection = null;
    }

    protected SqlParameterSource[] getParameterSources(List<?> list) {
        if (list == null || list.isEmpty())
            return null;
        int len = list.size();
        SqlParameterSource[] paras = new SqlParameterSource[len];
        for (int i = 0; i < len; i++) {
            paras[i] = new BeanParameterSource(list.get(i));
        }
        return paras;
    }

    protected LazyBeanParameterSource[] getLazyParameterSources(List<?> list) {
        if (list == null || list.isEmpty())
            return null;
        int len = list.size();
        LazyBeanParameterSource[] paras = new LazyBeanParameterSource[len];
        int i = 0;
        for (Object obj : list) {
            paras[i++] = new LazyBeanParameterSource(obj);
        }
        return paras;
    }

    private volatile boolean closed = false;
}
