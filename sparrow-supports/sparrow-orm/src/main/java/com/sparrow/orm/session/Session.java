package com.sparrow.orm.session;

import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sparrow.core.log.LoggerManager;
import com.sparrow.core.log.sql.SqlLog;
import com.sparrow.orm.config.TableConfiguration;
import com.sparrow.orm.extractor.AbstractResultExtractor;
import com.sparrow.orm.extractor.BeanResultExtractor;
import com.sparrow.orm.extractor.PojoResultExtractor;
import com.sparrow.orm.extractor.ResultExtractor;
import com.sparrow.orm.extractor.SingleColumnResultExtractor;
import com.sparrow.orm.extractor.SingleExtractor;
import com.sparrow.orm.id.IdentifierGenerator;
import com.sparrow.orm.jdbc.JdbcContext;
import com.sparrow.orm.meta.MappingField;
import com.sparrow.orm.meta.MappingFieldsWrap;
import com.sparrow.orm.page.PageResult;
import com.sparrow.orm.sql.PreparedStatementSetter;
import com.sparrow.orm.sql.named.BeanParameterSource;
import com.sparrow.orm.sql.named.MapSqlParameterSource;
import com.sparrow.orm.sql.named.NamedParameterOperate;
import com.sparrow.orm.sql.named.SqlParameterSource;
import com.sparrow.orm.sql.setter.ArgumentPreparedStatementSetter;
import com.sparrow.orm.trans.Transaction;
import com.sparrow.orm.util.SQLUtils;
import com.sparrow.orm.util.ValueSetter;
import com.sparrow.orm.util.sql.Formatter;

public abstract class Session {
    public static final int MAX_ROWS = 200;

    private final int level = Connection.TRANSACTION_READ_COMMITTED;

    protected final Connection connection;
    protected SessionFactory sessionFactory;

    protected Map<Class<?>, AbstractResultExtractor<?>> extractors;
    protected NamedParameterOperate namedParameterOperate;

    protected SqlLog log = LoggerManager.getSqlLog();
    protected String dbType;
    protected boolean showSql;
    protected boolean formatSql;

    public Session(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.connection = sessionFactory.getConnection();
        this.dbType = sessionFactory.getDatabaseType();
        this.namedParameterOperate = sessionFactory.getNamedParameterOperate();
        this.extractors = sessionFactory.getExtractors();
    }

    public Session(Connection conn, TableConfiguration config, String type) {
        this.connection = conn;
        this.dbType = type;
        this.namedParameterOperate = new NamedParameterOperate();
        this.extractors = new ConcurrentHashMap<Class<?>, AbstractResultExtractor<?>>();
    }

    public int getLevel() {
        return level;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public Map<Class<?>, AbstractResultExtractor<?>> getExtractors() {
        return extractors;
    }

    public NamedParameterOperate getNamedParameterOperate() {
        return namedParameterOperate;
    }

    public SqlLog getLog() {
        return log;
    }

    public String getDbType() {
        return dbType;
    }

    public boolean isClosed() {
        return closed;
    }

    public Connection getConnection() {
        return connection;
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

    @SuppressWarnings("unchecked")
    public <T> AbstractResultExtractor<T> createResultExtractor(Class<T> clazz,
                                                                MappingFieldsWrap mappingFieldsWrap) {
        AbstractResultExtractor<T> extractor = (AbstractResultExtractor<T>) this.extractors
                .get(clazz);
        if (extractor == null) {
            extractor = new PojoResultExtractor<T>(clazz, mappingFieldsWrap);
            this.extractors.put(clazz, extractor);
        }
        return extractor;
    }

    public <T> List<T> getSQLObjectMap(String sql, Class<T> claz) {
        return this.queryList(sql, claz);
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
                                      SqlParameterSource paramSource, ResultExtractor<T> extractor,
                                      int maxRow) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (this.showSql)
            log.prepare(this.formatSql(sql));

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
                log.effects(n);
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            // e.getNextException().printStackTrace();
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

        if (this.showSql)
            log.prepare(this.formatSql(sql));

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
                log.effects(n);
            return l;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
            // System.out.println(e.getMessage());//
            // e.getNextException().printStackTrace();
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
            log.prepare(this.formatSql(sql));

        try {
            ps = this.namedParameterOperate.createPreparedStatement(
                    this.getConnection(), sql, paramSource);
            rs = ps.executeQuery();
            T t = this.wrapSingleResult(rs, extractor);
            int n = (t == null ? 0 : 1);
            if (this.showSql)
                log.effects(n);
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

    public <T> T getObject(String sql, PreparedStatementSetter setter,
                           SingleExtractor<T> extractor) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (this.showSql)
            log.prepare(this.formatSql(sql));

        try {
            ps = this.namedParameterOperate.createPreparedStatement(
                    this.getConnection(), sql, setter);
            rs = ps.executeQuery();
            T t = this.wrapSingleResult(rs, extractor);
            int n = (t == null ? 0 : 1);
            if (this.showSql)
                log.effects(n);
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
                log.prepare(this.formatSql(sql));
            int n = this.namedParameterOperate.executeWithKeyGenerate(
                    this.getConnection(), sql, object, key);
            if (this.showSql)
                log.effects(n);
            return n;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public Object exeWithKeyGen(String sql, Object args[], String key) {
        try {
            if (this.showSql)
                log.prepare(this.formatSql(sql));
            Long n = (Long) this.namedParameterOperate.exeWithKeyGen(
                    this.getConnection(), sql, args, key);
            if (this.showSql)
                log.effects(1);
            return n;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    protected void generateKey(Object object, MappingFieldsWrap mappingWrap) {
        IdentifierGenerator idGenerator = mappingWrap.getGenerator();
        MappingField mappingField = mappingWrap.getKeyFeild();
        // 获取字段值的生成器
        if (idGenerator != null) {
            Object val = ValueSetter.getValue(mappingField.getProp(), object);
            // po 属性未手动设置值,则采用生成器生成
            if (val == null) {
                try {
                    ValueSetter.setValue(mappingField.getProp(), object,
                            idGenerator.generate(this.getJdbcContext()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private JdbcContext getJdbcContext() {
        return new DefaultJdbcContext(this.connection);
    }

    public int execute(String sql) {
        int n = 0;
        try {
            PreparedStatement pstmt = null;
            pstmt = connection.prepareStatement(sql);
            if (this.showSql)
                log.prepare(this.formatSql(sql));
            n = pstmt.executeUpdate();
            if (this.showSql)
                log.effects(n);
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
            log.prepare(this.formatSql(sql));
        try {
            int n = 0;
            ps = this.namedParameterOperate.createPreparedStatement(
                    this.getConnection(), sql, paramSource);
            n = ps.executeUpdate();
            if (this.showSql)
                log.effects(n);
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
            log.prepare(this.formatSql(sql));
        try {
            int n = 0;
            ps = this.namedParameterOperate.createPreparedStatement(
                    this.getConnection(), sql, setter);
            n = ps.executeUpdate();
            if (this.showSql)
                log.effects(n);
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
        return (java.util.Date.class.isAssignableFrom(inValueType) && !(java.sql.Date.class
                .isAssignableFrom(inValueType)
                || java.sql.Time.class.isAssignableFrom(inValueType) || java.sql.Timestamp.class
                .isAssignableFrom(inValueType)));
    }

    private static boolean isStringValue(Class<?> inValueType) {
        return (CharSequence.class.isAssignableFrom(inValueType) || StringWriter.class
                .isAssignableFrom(inValueType));
    }

    public int batchExecute(String[] sqls) {
        if (this.showSql)
            log.prepare(this.formatSql(sqls[0]));
        int effects = 0;
        try {
            Statement pstmt = this.getConnection().createStatement();
            for (String sql : sqls) {
                pstmt.addBatch(sql);
            }
            int n[] = pstmt.executeBatch();
            effects = this.getEffects(n);
            if (this.showSql)
                log.effects(effects);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return effects;
    }

    public int batchExecuteSimple(String sql, List<?> values) {
        if (this.showSql)
            log.prepare(this.formatSql(sql));
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
                log.effects(effects);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return effects;
    }

    public int batchExecute(String sql, List<Object[]> values) {
        if (this.showSql)
            log.prepare(this.formatSql(sql));
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
                log.effects(effects);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return effects;
    }

    public int batchExecute(String sql, SqlParameterSource[] paras) {
        PreparedStatement ps = null;
        if (this.showSql)
            log.prepare(this.formatSql(sql));
        try {
            int n = 0;
            ps = this.namedParameterOperate.createPreparedStatement(
                    this.getConnection(), sql, paras);
            int r[] = ps.executeBatch();
            n = this.getEffects(r);
            if (this.showSql)
                log.effects(n);
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
            if (this.sessionFactory.isOracle()) {
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
                log.prepare(this.formatSql(sql));
            //
            // 批量执行
            // pstmt.setTimestamp(i + 1, );
            // pstmt.addBatch();
            int n = pstmt.executeUpdate();// pstmt.executeBatch();
            // this.connection.commit();
            if (this.showSql)
                log.effects(n);
            // this.connection.setAutoCommit(autoComit);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static <T> int generate(ResultSet rs, SingleExtractor<T> extractor,
                                    BeanCallback callback) {
        if (extractor == null || extractor == null)
            return 0;
        int n = 0;
        try {
            while (rs.next()) {
                n++;
                callback.callback(extractor.singleExtract(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n;
    }

    public <T> int queryForCallback(String sql, Object values[], Class<T> claz,
                                    BeanCallback callback) {
        if (this.showSql)
            log.prepared(this.formatSql(sql), values);
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql,
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            if (values != null)
                SQLUtils.setParams(connection, stmt, values, this.dbType);
            rs = stmt.executeQuery();
            if (rs == null) {
                if (this.showSql)
                    log.effects(0);
                return 0;
            }
            int n = generate(rs, new BeanResultExtractor<T>(claz), callback);
            if (this.showSql)
                log.effects(n);
            return n;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
        }
    }

    public int queryBack(String sql, Map<String, Object> param, RowCallbackHandler callback) {
        if (this.showSql)
            log.prepare(this.formatSql(sql));
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = this.namedParameterOperate.createPreparedStatement(
                    this.getConnection(), sql, new MapSqlParameterSource(param), true);
            rs = ps.executeQuery();
            if (rs == null) {
                if (this.showSql)
                    log.effects(0);
                return 0;
            }
            int n = this.processRow(rs, callback);
            if (this.showSql)
                log.effects(n);
            return n;
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

    private int processRow(ResultSet rs, RowCallbackHandler callback) {
        if (callback == null)
            return 0;
        int n = 0;
        try {
            while (rs.next()) {
                n++;
                callback.processRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n;
    }

    public int executeCallback(String sql, StatementCallbackHandler callback) {
        if (this.showSql)
            log.prepare(this.formatSql(sql));
        PreparedStatement ps = null;
        try {
            ps = this.getConnection().prepareStatement(sql);
            int n = callback.processStatement(ps);
            if (this.showSql)
                log.effects(n);
            return n;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
        }
    }

    public void close() {
        if (this.closed)
            return;
        this.closed = true;
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (sessionFactory != null)
            sessionFactory.removeSession();
        this.extractors = null;
        this.extractors = null;
        this.namedParameterOperate = null;
    }

    public final Transaction beginTranscation() {
        Transaction tx = new Transaction(this.connection);
        tx.setLevel(level);
        return tx;
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

    public abstract <T> T getById(Class<T> clazz, Serializable id);

    public abstract <T> List<T> query(Class<T> claz);

    public abstract Integer save(Object bean);

    public abstract Integer normalSave(Object bean);

    public abstract Integer update(Object bean);

    public abstract int delete(Class<?> claz, Serializable id);

    public abstract Integer batchSave(List<?> beans);

    public abstract Integer batchUpdate(List<?> beans);

    public abstract <T> Integer batchDelete(Class<T> claz, List<?> ids);

    public abstract <T> PageResult pageQuery(String sql, Object args[],
                                             ResultExtractor<T> extractor, int pageIndex, int pageSize);

    public abstract <T> PageResult pageQuery(String sql,
                                             SqlParameterSource parameterSource, ResultExtractor<T> extractor,
                                             int pageIndex, int pageSize);

    private volatile boolean closed = false;
}
