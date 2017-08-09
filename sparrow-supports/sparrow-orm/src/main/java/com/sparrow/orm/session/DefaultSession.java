package com.sparrow.orm.session;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.sparrow.core.utils.BeanForceUtil;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.orm.config.SqlMapConfig;
import com.sparrow.orm.config.TableConfiguration;
import com.sparrow.orm.config.TableMapping;
import com.sparrow.orm.extractor.BeanResultExtractor;
import com.sparrow.orm.extractor.MapResultExtractor;
import com.sparrow.orm.extractor.ResultExtractor;
import com.sparrow.orm.meta.MappingField;
import com.sparrow.orm.meta.MappingFieldsWrap;
import com.sparrow.orm.page.PageResult;
import com.sparrow.orm.sql.named.BeanParameterSource;
import com.sparrow.orm.sql.named.SqlParameterSource;
import com.sparrow.orm.sql.named.TableIdParameterSource;
import com.sparrow.orm.sql.named.TableMapParameterSource;
import com.sparrow.orm.util.SQLParser;

public class DefaultSession extends Session {
	private final TableConfiguration tableConfiguration;

	public DefaultSession(SessionFactory sessionFactory) {
		super(sessionFactory);
		this.tableConfiguration = sessionFactory.getTableConfiguration();
	}

	public DefaultSession(Connection conn, TableConfiguration config,
			String type) {
		super(conn, config, type);
		this.tableConfiguration = config;
	}

	public <T> List<T> getSQLObjectMap(String sql, Class<T> claz) {
		return this.queryList(sql, claz);
	}

	public PageResult pageSQLObjectMap(String sql, Class<?> claz,
			int pageIndex, int pageSize) {
		SQLParser parser = SQLParser.parse(sql);
		int records = this.getCount(this.getConnection(), parser.getCountSql());
		PageResult page = new PageResult();
		page.setTotal(records);
		sql = SQLParser.getPagedSql(this.dbType, sql, pageIndex, pageSize);
		page.setRows(this.queryList(sql, claz));
		return page;
	}

	protected int getCount(String sql, SqlParameterSource paramSource) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		if (this.showSql)
			log.prepare(this.formatSql(sql));
		try {
			ps = this.namedParameterOperate.createPreparedStatement(
					this.getConnection(), sql, paramSource, true);
			rs = ps.executeQuery();
			int records = 0;
			if (rs.next()) {
				records = Integer.parseInt(rs.getObject(1).toString());
			}
			if (this.showSql)
				log.effects(records);
			return records;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
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
		return 0;
	}

	private int getCount(Connection connection, String sql) {
		if (this.showSql)
			log.prepare(this.formatSql(sql));
		Statement st = null;
		ResultSet rs = null;
		try {
			st = connection.createStatement();
			rs = st.executeQuery(sql);
			int records = 0;
			if (rs.next()) {
				records = Integer.parseInt(rs.getObject(1).toString());
			}
			st.close();
			rs.close();
			return records;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public <T> PageResult pageQuery(Class<T> claz, int pageIndex, int pageSize) {
		TableMapping tableMapping = this.tableConfiguration
				.getTableMapping(claz);
		if (tableMapping == null)
			throw new RuntimeException("Has no '" + claz.getName()
					+ "' table mapping ");
		String sql = tableMapping.getSelectSqlNoWhere();
		int records = this
				.getCount(this.connection, tableMapping.getCountSql());
		PageResult page = new PageResult();
		page.setTotal(records);
		sql = SQLParser.getPagedSql(this.dbType, sql, pageIndex, pageSize);
		page.setRows(this.doQueryList(
				sql,
				null,
				this.createResultExtractor(claz, tableMapping.getMappingWrap()),
				pageSize));
		return page;
	}

	public <T> PageResult pageQuery(String sql, Object object, Class<T> claz,
			int pageIndex, int pageSize) {
		SQLParser parser = SQLParser.parse(sql);
		int records = this.getCount(this.connection, parser.getCountSql());
		PageResult page = new PageResult();
		page.setTotal(records);
		sql = SQLParser.getPagedSql(this.dbType, sql, pageIndex, pageSize);
		page.setRows(this.doQueryList(sql, new BeanParameterSource(object),
				new BeanResultExtractor<T>(claz), pageSize));
		return page;
	}

	public PageResult pageQuery(Object object, int pageIndex, int pageSize) {
		Class<?> claz = object.getClass();
		TableMapping tableMapping = this.tableConfiguration
				.getTableMapping(claz);
		if (tableMapping == null)
			throw new RuntimeException("Has no '" + claz.getName()
					+ "' table mapping ");
		String sql = tableMapping.getSelectSqlNoWhere();
		int records = this
				.getCount(this.connection, tableMapping.getCountSql());
		PageResult page = new PageResult();
		page.setTotal(records);
		sql = SQLParser.getPagedSql(this.dbType, sql, pageIndex, pageSize);
		page.setRows(this.doQueryList(
				sql,
				null,
				this.createResultExtractor(claz, tableMapping.getMappingWrap()),
				pageSize));
		return page;
	}

	public List<?> getSQLMap(String id) {
		SqlMapConfig cfg = this.tableConfiguration.getSqlMap(id);
		if (cfg == null)
			throw new RuntimeException("Has no '" + id + "' sql mapping...");
		Class<?> c;
		try {
			c = BeanForceUtil.loadClass(cfg.getObj());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("class not found ! ");
		}
		return this.queryList(cfg.getSql(), c);
	}

	public PageResult pageQuerySqlMap(String id, int pageIndex, int pageSize) {
		SqlMapConfig cfg = this.tableConfiguration.getSqlMap(id);
		if (cfg == null)
			throw new RuntimeException("Has no '" + id + "' sql mapping...");
		String sql = cfg.getSql();
		SQLParser parser = SQLParser.parse(sql);
		int records = this.getCount(this.connection, parser.getCountSql());
		PageResult page = new PageResult();
		page.setTotal(records);
		sql = SQLParser.getPagedSql(this.dbType, sql, pageIndex, pageSize);
		Class<?> c;
		try {
			c = BeanForceUtil.loadClass(cfg.getObj());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("class not found ! ");
		}
		page.setRows(this.pageQueryList(sql, c));
		return page;
	}

	private <T> List<T> pageQueryList(String sql, Class<T> clazz) {
		return this.doQueryList(sql, null, new BeanResultExtractor<T>(clazz));
	}

	public List<?> sqlQuery(String sql) {
		SQLParser parser = SQLParser.parse(sql);
		String table = parser.getTable();
		TableMapping cfg = tableConfiguration.getMappingByTable(table);
		if (cfg != null) {
			return this.doQueryList(
					sql,
					this.createResultExtractor(cfg.getMapClazz(),
							cfg.getMappingWrap()));
		} else
			return this.doQueryList(sql, new MapResultExtractor());
	}

	public PageResult pageSqlQuery(String sql, int pageIndex, int pageSize) {
		SQLParser parser = SQLParser.parse(sql);
		String sqll = parser.getCountSql();
		String table = parser.getTable();
		TableMapping cfg = this.tableConfiguration.getMappingByTable(table);

		int records = this.getCount(this.connection, sqll);
		PageResult page = new PageResult();
		page.setTotal(records);
		sqll = SQLParser.getPagedSql(this.dbType, sql, pageIndex, pageSize);

		List<?> list;
		if (cfg != null) {
			list = this.doQueryList(
					sql,
					this.createResultExtractor(cfg.getMapClazz(),
							cfg.getMappingWrap()));
		} else
			list = this.doQueryList(sql, new MapResultExtractor());
		page.setRows(list);
		return page;
	}

	private TableMapping getTableMapping(Object object) {
		return this.tableConfiguration.getTableMapping(object.getClass());
	}

	public <T> T getById(Class<T> clazz, Serializable id) {
		TableMapping tableMapping = this.tableConfiguration
				.getTableMapping(clazz);
		String sql = tableMapping.getSelectSql();
		MappingFieldsWrap wrap = tableMapping.getMappingWrap();
		return this.getObject(sql, new TableIdParameterSource(id, wrap),
				this.createResultExtractor(clazz, wrap));
	}

	public <T> List<T> query(Class<T> claz) {
		TableMapping tableMapping = this.tableConfiguration
				.getTableMapping(claz);
		if (tableMapping == null)
			throw new RuntimeException("Has no '" + claz.getName()
					+ "' table mapping ");
		String sql = tableMapping.getSelectSqlNoWhere();
		SqlParameterSource paramSource = null;
		return this
				.doQueryList(
						sql,
						paramSource,
						this.createResultExtractor(claz,
								tableMapping.getMappingWrap()));
	}

	public Integer save(Object bean) {
		TableMapping cfg = getTableMapping(bean);
		this.generateKey(bean, cfg.getMappingWrap());
		return this.execute(cfg.getInsertSql(), new BeanParameterSource(bean));
	}

	public Integer normalSave(Object bean) {
		return this.save(bean);
	}

	public Integer update(Object bean) {
		TableMapping cfg = getTableMapping(bean);
		return this.execute(cfg.getUpdateSql(), new TableMapParameterSource(
				bean, cfg.getMappingWrap()));
	}

	public int delete(Class<?> claz, Serializable id) {
		TableMapping cfg = this.tableConfiguration.getTableMapping(claz);
		return this.execute(cfg.getDeleteSql(), new TableIdParameterSource(id,
				cfg.getMappingWrap()));
	}

	public Integer batchSave(List<?> beans) {
		if (beans == null || beans.isEmpty())
			return 0;
		Object bean = beans.get(0);
		TableMapping cfg = this.getTableMapping(bean);
		String sql = cfg.getInsertSql();
		if (StringUtils.isEmpty(sql))
			return null;
		SqlParameterSource[] paras = this.getParameterSources(beans);
		return this.batchExecute(sql, paras);
	}

	public Integer batchUpdate(List<?> beans) {
		if (beans == null || beans.isEmpty())
			return 0;
		Object bean = beans.get(0);
		TableMapping cfg = this.getTableMapping(bean);
		String sql = cfg.getUpdateSql();
		if (StringUtils.isEmpty(sql))
			return null;
		SqlParameterSource[] paras = this.getParameterSources(beans);
		return this.batchExecute(sql, paras);
	}

	public <T> Integer batchDelete(Class<T> claz, List<?> ids) {
		TableMapping cfg = this.tableConfiguration.getTableMapping(claz);
		if (cfg == null)
			return 0;
		String sql = this.getDeleteSql(cfg);
		return this.batchExecuteSimple(sql, ids);
	}

	String getDeleteSql(TableMapping cfg) {
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM ").append(cfg.getTableName()).append(" WHERE ");
		MappingField[] fields = cfg.getPrimaryKey();
		if (fields != null && fields.length > 0)
			buildPrimaryWhereSql(fields, sb);
		else
			buildPrimaryWhereSql(cfg.getColumns(), sb);
		return sb.toString();
	}

	static void buildPrimaryWhereSql(MappingField[] fields, StringBuilder sb) {
		MappingField mapField;
		boolean notFirst = false;
		for (int i = 0; i < fields.length; i++) {
			mapField = fields[i];
			if (notFirst) {
				sb.append(" AND ");
			} else
				notFirst = true;
			sb.append(mapField.getColumn()).append("=").append('?');
		}
	}

	public <T> PageResult pageQuery(String sql, Object args[],
			ResultExtractor<T> extractor, int pageIndex, int pageSize) {
		SQLParser parser = SQLParser.parse(sql);
		int records = this.querySimple(parser.getCountSql(), args,
				Integer.class);
		PageResult page = new PageResult();
		page.setTotal(records);
		sql = SQLParser.getPagedSql(this.dbType, sql, pageIndex, pageSize);
		page.setRows(this.queryList(sql, args, extractor, pageSize));
		return page;
	}

	public <T> PageResult pageQuery(String sql,
			SqlParameterSource parameterSource, ResultExtractor<T> extractor,
			int pageIndex, int pageSize) {
		SQLParser parser = SQLParser.parse(sql);
		int records = this.getCount(parser.getCountSql(), parameterSource);
		PageResult page = new PageResult();
		page.setTotal(records);
		sql = SQLParser.getPagedSql(this.dbType, sql, pageIndex, pageSize);
		page.setRows(this
				.doQueryList(sql, parameterSource, extractor, pageSize));
		return page;
	}

}
