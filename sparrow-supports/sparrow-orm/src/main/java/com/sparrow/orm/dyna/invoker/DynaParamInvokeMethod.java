package com.sparrow.orm.dyna.invoker;

import java.sql.Types;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.sparrow.orm.config.TableMapping;
import com.sparrow.orm.dyna.common.Tool;
import com.sparrow.orm.dyna.data.InvokeMeta;
import com.sparrow.orm.dyna.data.MethodParam;
import com.sparrow.orm.dyna.proxy.ProxyHelper;
import com.sparrow.orm.dyna.sql.SqlBuilder;
import com.sparrow.orm.dyna.sql.SqlHelper;
import com.sparrow.orm.meta.MappingField;
import com.sparrow.orm.sql.SqlParameterValue;
import com.sparrow.orm.sql.named.BeanParameterSource;
import com.sparrow.orm.sql.named.MapSqlParameterSource;
import com.sparrow.orm.sql.named.SimpleSqlParameterSource;
import com.sparrow.orm.sql.named.SqlParameterSource;
import com.sparrow.orm.template.HitTemplate;


/**
 * 获取method的参数名称 javassist和asm可以实现，jdk确实没有内置这个，大概是觉得参数名不重要吧
 * 
 */
public class DynaParamInvokeMethod extends InvokeMethod {

	public DynaParamInvokeMethod(InvokeMeta invokerMeta, HitTemplate hitTemplate) {
		super(invokerMeta, hitTemplate);
	}

	@Override
	protected int doUpdate(InvokeMeta invokeMeta, Object[] args) {
		MethodParam[] m = invokeMeta.getRelations();
		MethodParam mp = m[0];
		if (mp.isArray()) {
			Object[] o = (Object[]) args[0];
			return this.namedBatchUpdate(invokeMeta, Arrays.asList(o));
		} else if (mp.isList())
			return this.namedBatchUpdate(invokeMeta, (List<?>) args[0]);
		else
			return this.simpleUpdate(invokeMeta, args);
	}

	protected int simpleUpdate(InvokeMeta invokeMeta, Object[] args) {
		SqlParameterSource parameterSource = null;
		String sql = invokeMeta.getSql();
		if (args != null && args.length > 0) {
			parameterSource = this.getSqlParameterSource(
					invokeMeta.getRelations(), args);
		}
		if (parameterSource == null)
			return this.getHitTemplate().execute(sql);
		else
			return this.getHitTemplate().execute(sql, parameterSource);
	}

	int namedBatchUpdate(InvokeMeta invokeMeta, List<?> pojos) {
		String sql = invokeMeta.getSql();
		return this.getHitTemplate().batchExecute(sql,
				this.getSqlParameterSources(pojos));
	}

	SqlParameterSource[] getSqlParameterSources(List<?> pojos) {
		if (pojos == null || pojos.isEmpty())
			return null;
		SqlParameterSource[] al = new SqlParameterSource[pojos.size()];
		Iterator<?> iterator = pojos.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			al[i++] = new BeanParameterSource(iterator.next());
		}
		return al;
	}

	@Override
	protected <T> List<T> doQuery(InvokeMeta invokeMeta, Object[] args,
			Class<T> wrapClass) {
		SqlParameterSource parameterSource = null;
		String sql = invokeMeta.getSql();
		if (args != null && args.length > 0)
			parameterSource = this.getSqlParameterSource(
					invokeMeta.getRelations(), args);
		List<T> results;
		if (parameterSource == null)
			results = this.getHitTemplate().query(sql, wrapClass);
		else
			results = this.getHitTemplate().query(sql, parameterSource,
					wrapClass);
		return results;
	}

	<T> List<T> dynaQuery(InvokeMeta invokeMeta, Object queryEntity,
			Class<T> wrapClass) {
		SqlBuilder sqlBuilder = SqlHelper.selectSql(invokeMeta.getSql());
		TableMapping tm = invokeMeta.getTableMapping();
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		// 构造动态sql
		boolean f = this.handleDynaParameter(queryEntity, tm.getPrimaryKey(),
				sqlBuilder, parameterSource, false);
		// 有primary其条件就不用
		f = f
				|| this.handleDynaParameter(queryEntity, tm.getColumns(),
						sqlBuilder, parameterSource, f);
		String sql = sqlBuilder.sql();
		List<T> results;
		if (f)
			results = this.getHitTemplate().query(sql, parameterSource,
					wrapClass);
		else
			results = this.getHitTemplate().query(sql, wrapClass);
		return results;
	}

	boolean skipSqlQueryColumn(int sqlType) {
		switch (sqlType) {
		case Types.BLOB:
			return true;
		case Types.CLOB:
			return true;
		case Types.LONGNVARCHAR:
			return true;
		case Types.LONGVARBINARY:
			return true;
		case Types.LONGVARCHAR:
			return true;
		case Types.ARRAY:
			return true;
		case Types.NCLOB:
			return true;
		case Types.VARBINARY:
			return true;
		}
		return false;
	}

	boolean handleDynaParameter(Object entity, MappingField[] feilds,
			SqlBuilder sqlBuilder, MapSqlParameterSource parameterSource,
			boolean haseParas) {
		MappingField f;
		Object v;
		boolean g = haseParas;
		for (int i = 0; i < feilds.length; i++) {
			f = feilds[i];
			if (this.skipSqlQueryColumn(f.getSqlType()))
				continue;
			v = ProxyHelper.getValue(f.getProp(), entity);
			if (v != null) {
				if (!g) {
					if (!sqlBuilder.isHasWhere())
						sqlBuilder.where();
					g = true;
				}
				sqlBuilder.andEquals(f.getColumn(), ":" + f.getField());
				SqlParameterValue value = new SqlParameterValue(f.getSqlType(),
						v);
				parameterSource.addValue(f.getField(), value);
			}
		}
		return g;
	}

	@Override
	protected <T> T doQueryForSimple(InvokeMeta invokeMeta, Object[] args,
			Class<T> clazz) {
		SqlParameterSource parameterSource = null;
		String sql = invokeMeta.getSql();
		if (args != null && args.length > 0)
			parameterSource = this.getSqlParameterSource(
					invokeMeta.getRelations(), args);
		T t;
		if (parameterSource == null)
			t = this.getHitTemplate().querySimple(sql, clazz);
		else
			t = this.getHitTemplate().querySimple(sql, parameterSource, clazz);
		return t;
	}

	@Override
	protected <T> T doQueryForObject(InvokeMeta invokeMeta, Object[] args,
			Class<T> clazz) {
		SqlParameterSource parameterSource = null;
		String sql = invokeMeta.getSql();
		if (args != null && args.length > 0)
			parameterSource = this.getSqlParameterSource(
					invokeMeta.getRelations(), args);
		T t;
		if (parameterSource == null)
			t = this.getHitTemplate().queryForObject(sql, clazz);
		else
			t = this.getHitTemplate().queryForObject(sql, parameterSource,
					clazz);
		return t;
	}

	SqlParameterSource getSqlParameterSource(MethodParam[] types, Object args[]) {
		if (ProxyHelper.isEmpty(types) || ProxyHelper.isEmpty(args))
			return null;
		if (args.length == 1) {
			MethodParam type = types[0];
			if (Tool.isBaseType(type.getType()))
				return new SimpleSqlParameterSource(type.getName(), args[0]);
			else
				return new BeanParameterSource(args[0]);
		}

		return new DynaMapParameterSource(types, args);
	}
}
