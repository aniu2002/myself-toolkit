package com.sparrow.orm.dao.simple;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.List;

import com.sparrow.core.utils.PropertyUtils;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.orm.page.PageResult;
import com.sparrow.orm.pojo.PojoMeta;
import com.sparrow.orm.session.Session;
import com.sparrow.orm.shema.NameRule;
import com.sparrow.orm.sql.builder.SqlBuilder;
import com.sparrow.orm.sql.builder.SqlHelper;
import com.sparrow.orm.sql.named.BeanParameterSource;
import com.sparrow.orm.sql.named.SqlParameterSource;
import com.sparrow.orm.template.ExecuteCallback;
import com.sparrow.orm.template.simple.OperateTemplate;
import com.sparrow.orm.template.simple.TemplateFactory;
import com.sparrow.orm.util.ValueSetter;

public class SimpleDao {
	private static final String PARAMETER_CHAR = ":";
	private static final String[] fieldsSet;
	private OperateTemplate operateTemplate = TemplateFactory
			.getOperateTemplate();

	static {
		String likeQueryFields = SystemConfig.getProperty("query.like.fields",
				"name,taskName,type,wfName");
		fieldsSet = StringUtils.tokenizeToStringArray(likeQueryFields, ",");
	}

	static boolean canUseLikeQuery(String fName) {
		for (String str : fieldsSet) {
			if (str.equals(fName))
				return true;
		}
		return false;
	}

	protected OperateTemplate getOperateTemplate() {
		return this.operateTemplate;
	}

	public <T> T queryObject(final String sql, final Object args[],
			final Class<T> clazz) {
		return this.operateTemplate.query(new ExecuteCallback<T>() {
			@Override
			public T execute(Session session)  {
				return session.queryObject(sql, args, clazz);
			}
		});
	}

	public <T> List<T> queryList(final Class<T> clazz) {
		return this.operateTemplate.query(new ExecuteCallback<List<T>>() {
			@Override
			public List<T> execute(Session session)  {
				return session.query(clazz);
			}
		});
	}

	public <T> List<T> queryList(final String sql, final Class<T> clazz) {
		return this.operateTemplate.query(new ExecuteCallback<List<T>>() {
			@Override
			public List<T> execute(Session session)  {
				return session.queryList(sql, clazz);
			}
		});
	}

	public <T> List<T> queryList(final String sql, final Object bean,
			final Class<T> clazz) {
		return this.operateTemplate.query(new ExecuteCallback<List<T>>() {
			@Override
			public List<T> execute(Session session)  {
				return session.queryList(sql, new BeanParameterSource(bean),
						clazz);
			}
		});
	}

	public <T> List<T> queryList(final String sql, final Object[] args,
			final Class<T> clazz) {
		return this.operateTemplate.query(new ExecuteCallback<List<T>>() {
			@Override
			public List<T> execute(Session session)  {
				return session.queryList(sql, args, clazz);
			}
		});
	}

	public <T> T getById(final Class<T> clazz, final Serializable id) {
		return this.operateTemplate.query(new ExecuteCallback<T>() {
			@Override
			public T execute(Session session)  {
				return session.getById(clazz, id);
			}
		});
	}

	public Integer save(final Object bean) {
		return this.operateTemplate.execute(new ExecuteCallback<Integer>() {
			@Override
			public Integer execute(Session session)  {
				return session.save(bean);
			}
		});
	}

	public Integer save(final Object bean, final Checker checker) {
		return this.operateTemplate.execute(new ExecuteCallback<Integer>() {
			@Override
			public Integer execute(Session session) throws Exception {
				if (checker == null || checker.check(bean, session))
					return session.save(bean);
				return -1;
			}
		});
	}

	public Integer update(final Object bean) {
		return this.operateTemplate.execute(new ExecuteCallback<Integer>() {
			@Override
			public Integer execute(Session session)  {
				return session.update(bean);
			}
		});
	}

	public Integer delete(final Class<?> clazz, final Serializable id) {
		return this.operateTemplate.execute(new ExecuteCallback<Integer>() {
			@Override
			public Integer execute(Session session)  {
				return session.delete(clazz, id);
			}
		});
	}

	protected Integer executeSimple(String sql, Serializable id) {
		return this.execute(sql, new Object[] { id });
	}

	protected Integer execute(final String sql, final Object values[]) {
		return this.operateTemplate.execute(new ExecuteCallback<Integer>() {
			@Override
			public Integer execute(Session session)  {
				return session.execute(sql, values);
			}
		});
	}

	protected Integer execute(final String sql, final Object bean) {
		return this.operateTemplate.execute(new ExecuteCallback<Integer>() {
			@Override
			public Integer execute(Session session)  {
				return session.execute(sql, bean);
			}
		});
	}

	protected Integer batchExecuteSimple(final String sql, final List<Long> ids) {
		return this.operateTemplate.execute(new ExecuteCallback<Integer>() {
			@Override
			public Integer execute(Session session)  {
				return session.batchExecuteSimple(sql, ids);
			}
		});
	}

	public Integer batchSave(List<?> beans) {
		if (beans == null || beans.isEmpty())
			return 0;
		Object bean = beans.get(0);
		String sql = PojoMeta.getInsertSql(bean.getClass());
		if (StringUtils.isEmpty(sql))
			return null;
		return this.batchExecute(sql, beans);
	}

	public Integer batchUpdate(List<?> beans) {
		if (beans == null || beans.isEmpty())
			return 0;
		Object bean = beans.get(0);
		String sql = PojoMeta.getUpdateSql(bean.getClass());
		if (StringUtils.isEmpty(sql))
			return null;
		return this.batchExecute(sql, beans);
	}

	protected Integer batchExecute(final String sql, List<?> beans) {
		final SqlParameterSource[] paras = this.operateTemplate
				.getParameterSources(beans);
		return this.operateTemplate.execute(new ExecuteCallback<Integer>() {
			@Override
			public Integer execute(Session session)  {
				return session.batchExecute(sql, paras);
			}
		});
	}

	protected SqlBuilder getQueryBuilder(Object bean) {
		if (bean == null)
			return null;
		return this.getQueryBuilder(bean.getClass());
	}

	protected SqlBuilder getQueryBuilder(Class<?> clazz) {
		if (clazz == null)
			return null;
		String sql = PojoMeta.getQuerySql(clazz);
		if (StringUtils.isEmpty(sql))
			return null;
		return SqlHelper.selectSql(sql);
	}

	protected SqlBuilder getDeleteBuilder(Class<?> clazz) {
		if (clazz == null)
			return null;
		String table = PojoMeta.getTable(clazz);
		return SqlHelper.delete(table);
	}

	protected SqlBuilder getCountBuilder(Class<?> clazz) {
		if (clazz == null)
			return null;
		String table = PojoMeta.getTable(clazz);
		return SqlHelper.count(table);
	}

	protected String getDeleteSql(Class<?> clazz) {
		if (clazz == null)
			return null;
		String sql = PojoMeta.getDeleteSql(clazz);
		return sql;
	}

	public PageResult pageQueryx(final Object obj, int page, int limit) {
		final Class<?> claz = obj.getClass();
		if (claz == null)
			return null;
		String table = PojoMeta.getTable(claz);
		if (StringUtils.isEmpty(table))
			return null;
		final SqlBuilder sqlBuilder = SqlHelper.selectFrom(table);
		final SqlBuilder countSqlBuilder = SqlHelper.count(table);
		PropertyDescriptor[] propDescriptors = PropertyUtils
				.getPropertyDescriptors(claz);
		PropertyDescriptor propDescriptor;
		Object val;
		String column;
		String pname;

		boolean f = true;
		for (int i = 0; i < propDescriptors.length; i++) {
			propDescriptor = propDescriptors[i];
			pname = propDescriptor.getName();
			if ("class".equals(pname))
				continue;
			val = ValueSetter.getValue(propDescriptor, obj);
			if (val != null) {
				if (f) {
					f = false;
					sqlBuilder.where();
					countSqlBuilder.where();
				}
				column = NameRule.fieldToColumn(propDescriptor.getName());
				sqlBuilder.andEquals(column, PARAMETER_CHAR).append(
						propDescriptor.getName());
				countSqlBuilder.andEquals(column, PARAMETER_CHAR).append(
						propDescriptor.getName());
			}
		}
		long start;
		if (page < 1)
			page = 1;
		start = (page - 1) * limit;
		sqlBuilder.appends(" order by id desc limit ", start, ",", limit);
		return this.operateTemplate.query(new ExecuteCallback<PageResult>() {
			@Override
			public PageResult execute(Session session) {
				PageResult page = new PageResult();
				SqlParameterSource paraSource = new BeanParameterSource(obj);
				page.setRows(session.queryList(sqlBuilder.sql(), paraSource,
						claz));
				page.setTotal(session.querySimple(countSqlBuilder.sql(),
						paraSource, Integer.class));
				return page;
			}
		});
	}

	public PageResult pageQuery(final Object obj, int page, int limit) {
		final Class<?> claz = obj.getClass();
		if (claz == null)
			return null;
		String table = PojoMeta.getTable(claz);
		if (StringUtils.isEmpty(table))
			return null;
		final SqlBuilder sqlBuilder = SqlHelper.selectFrom(table);
		final SqlBuilder countSqlBuilder = SqlHelper.count(table);
		PropertyDescriptor[] propDescriptors = PropertyUtils
				.getPropertyDescriptors(claz);
		PropertyDescriptor propDescriptor;
		Object val;
		String column;
		String pname;

		boolean f = true;
		boolean isStr = false;
		for (int i = 0; i < propDescriptors.length; i++) {
			propDescriptor = propDescriptors[i];
			pname = propDescriptor.getName();
			if ("class".equals(pname))
				continue;
			val = ValueSetter.getValue(propDescriptor, obj);
			if (val != null) {
				if (f) {
					f = false;
					sqlBuilder.where();
					countSqlBuilder.where();
				}
				isStr = String.class.isAssignableFrom(propDescriptor
						.getPropertyType()) && canUseLikeQuery(pname);

				column = NameRule.fieldToColumn(propDescriptor.getName());
				if (isStr) {
					sqlBuilder.andLike(column, "'%").append(val.toString(),
							"%'");
					countSqlBuilder.andLike(column, "'%").append(
							val.toString(), "%'");
				} else {
					sqlBuilder.andEquals(column, PARAMETER_CHAR).append(
							propDescriptor.getName());
					countSqlBuilder.andEquals(column, PARAMETER_CHAR).append(
							propDescriptor.getName());
				}
			}
		}
		long start;
		if (page < 1)
			page = 1;
		start = (page - 1) * limit;
		sqlBuilder.appends(" order by id desc limit ", start, ",", limit);
		return this.operateTemplate.query(new ExecuteCallback<PageResult>() {
			@Override
			public PageResult execute(Session session)  {
				PageResult page = new PageResult();
				SqlParameterSource paraSource = new BeanParameterSource(obj);
				page.setRows(session.queryList(sqlBuilder.sql(), paraSource,
						claz));
				page.setTotal(session.querySimple(countSqlBuilder.sql(),
						paraSource, Integer.class));
				return page;
			}
		});
	}

	public PageResult pageQuery(String table, String columns, final Object obj,
			int page, int limit) {
		if (obj == null)
			return null;
		if (StringUtils.isEmpty(table))
			return null;
		final Class<?> claz = obj.getClass();
		final SqlBuilder sqlBuilder = SqlHelper.select(columns).from(table);
		final SqlBuilder countSqlBuilder = SqlHelper.count(table);
		PropertyDescriptor[] propDescriptors = PropertyUtils
				.getPropertyDescriptors(claz);
		PropertyDescriptor propDescriptor;
		Object val;
		String column;
		String pname;

		boolean f = true;
		boolean isStr = false;
		for (int i = 0; i < propDescriptors.length; i++) {
			propDescriptor = propDescriptors[i];
			pname = propDescriptor.getName();
			if ("class".equals(pname))
				continue;
			val = ValueSetter.getValue(propDescriptor, obj);
			if (val != null) {
				if (f) {
					f = false;
					sqlBuilder.where();
					countSqlBuilder.where();
				}
				column = NameRule.fieldToColumn(propDescriptor.getName());
				isStr = String.class.isAssignableFrom(propDescriptor
						.getPropertyType()) && canUseLikeQuery(val.toString());
				if (isStr) {
					sqlBuilder.andLike(column, "'%").append(val.toString(),
							"%'");
					countSqlBuilder.andLike(column, "'%").append(
							val.toString(), "%'");
				} else {
					sqlBuilder.andEquals(column, PARAMETER_CHAR).append(
							propDescriptor.getName());
					countSqlBuilder.andEquals(column, PARAMETER_CHAR).append(
							propDescriptor.getName());
				}
			}
		}
		long start;
		if (page < 1)
			page = 1;
		start = (page - 1) * limit;
		sqlBuilder.appends(" limit ", start, ",", limit);
		return this.operateTemplate.query(new ExecuteCallback<PageResult>() {
			@Override
			public PageResult execute(Session session)  {
				PageResult page = new PageResult();
				SqlParameterSource paraSource = new BeanParameterSource(obj);
				page.setRows(session.queryList(sqlBuilder.sql(), paraSource,
						claz));
				page.setTotal(session.querySimple(countSqlBuilder.sql(),
						paraSource, Integer.class));
				return page;
			}
		});
	}
}
