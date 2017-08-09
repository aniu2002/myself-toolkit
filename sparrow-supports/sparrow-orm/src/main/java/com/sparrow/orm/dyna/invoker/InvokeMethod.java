package com.sparrow.orm.dyna.invoker;

import static com.sparrow.orm.dyna.common.Constants.COMMAND_DELETE;
import static com.sparrow.orm.dyna.common.Constants.COMMAND_INSERT;
import static com.sparrow.orm.dyna.common.Constants.COMMAND_SELECT;
import static com.sparrow.orm.dyna.common.Constants.COMMAND_SINGLE;
import static com.sparrow.orm.dyna.common.Constants.COMMAND_UNKNOW;
import static com.sparrow.orm.dyna.common.Constants.COMMAND_UPDATE;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.sparrow.orm.dyna.data.InvokeMeta;
import com.sparrow.orm.dyna.data.MethodMeta;
import com.sparrow.orm.dyna.data.MethodParam;
import com.sparrow.orm.template.HitTemplate;


/**
 * 获取method的参数名称 javassist和asm可以实现，jdk确实没有内置这个，大概是觉得参数名不重要吧
 * 
 */
public class InvokeMethod {
	final HitTemplate hitTemplate;
	final InvokeMeta invokerMeta;

	public InvokeMethod(InvokeMeta invokerMeta, HitTemplate hitTemplate) {
		this.invokerMeta = invokerMeta;
		this.hitTemplate = hitTemplate;
	}

	protected InvokeMeta getInvokeMeta() {
		return this.invokerMeta;
	}

	public HitTemplate getHitTemplate() {
		return hitTemplate;
	}

	protected Object doInvoke(Object args[]) {
		InvokeMeta invokeMeta = this.getInvokeMeta();
		int cmd = invokeMeta.getCommand();
		if (cmd == COMMAND_UNKNOW)
			throw new RuntimeException("无法动态代理的实体 ：" + invokeMeta.getError()
					+ " - " + invokeMeta.getMethodName());
		if (StringUtils.isEmpty(invokeMeta.getSql()))
			throw new RuntimeException("无法动态代理的实体bean,sql为空 ："
					+ invokeMeta.getMethodName());
		Object result = null;
		Class<?> resultType = invokeMeta.getWrapClass();
		boolean voidReturn = invokeMeta.isVoid();
		switch (cmd) {
		case COMMAND_INSERT:
			checkHasMethodArguments(invokeMeta, "插入");
			if (!voidReturn && !invokeMeta.isBaseType())
				throw new RuntimeException("插入实体bean时，返回的类型不是整型："
						+ invokeMeta.getMethodName());
			result = this.doUpdate(invokeMeta, args);
			break;
		case COMMAND_SINGLE:
			if (!voidReturn) {
				if (invokeMeta.isBaseType())
					result = this
							.doQueryForSimple(invokeMeta, args, resultType);
				else
					result = this
							.doQueryForObject(invokeMeta, args, resultType);
			}
			break;
		case COMMAND_SELECT:
			if (!voidReturn && !invokeMeta.isList())
				throw new RuntimeException("查询实体时，返回的类型不是列表类型："
						+ invokeMeta.getMethodName());
			result = this.doQuery(invokeMeta, args, resultType);
			break;
		case COMMAND_UPDATE:
			if (!voidReturn && !invokeMeta.isBaseType())
				throw new RuntimeException("更行实体bean时，返回的类型不是整型："
						+ invokeMeta.getMethodName());
			result = this.doUpdate(invokeMeta, args);
		case COMMAND_DELETE:
			if (!voidReturn && !invokeMeta.isBaseType())
				throw new RuntimeException("更行实体bean时，返回的类型不是整型："
						+ invokeMeta.getMethodName());
			result = this.doUpdate(invokeMeta, args);
			break;
		}
		return result;
	}

	public Object execute(Object args[]) throws Exception {
		return this.doInvoke(args);
	}

	protected void sqlExecute(InvokeMeta invokeMeta) {
		this.getHitTemplate().execute(invokeMeta.getSql());
	}

	protected int doUpdate(InvokeMeta invokeMeta, Object args[]) {
		MethodParam[] m = invokeMeta.getRelations();
		MethodParam mp = m[0];
		if (mp.isArray()) {
			Object[] o = (Object[]) args[0];
			return this.batchUpdate(invokeMeta, this.getArguments(o));
		} else if (mp.isList()) {
			List<?> o = (List<?>) args[0];
			return this.batchUpdate(invokeMeta, this.getArguments(o));
		} else
			return this.simpleUpdate(invokeMeta, args);
	}

	protected int simpleUpdate(InvokeMeta invokeMeta, Object args[]) {
		Object vals[] = null;
		String sql = invokeMeta.getSql();
		if (args != null && args.length > 0)
			vals = this.getArguments(invokeMeta, args);
		if (vals == null || vals.length == 0)
			return this.getHitTemplate().execute(sql);
		else
			return this.getHitTemplate().execute(sql, vals);
	}

	protected int getSuccessEffects(int efs[]) {
		int i = 0;
		for (int e : efs)
			if (e == Statement.SUCCESS_NO_INFO)
				i++;
		return i;
	}

	List<Object[]> getArguments(List<?> pojos) {
		if (pojos == null || pojos.isEmpty())
			return null;
		List<Object[]> args = new ArrayList<Object[]>();
		Iterator<?> iterator = pojos.iterator();
		while (iterator.hasNext()) {
			args.add(new Object[] { iterator.next() });
		}
		return args;
	}

	List<Object[]> getArguments(Object[] vals) {
		if (vals == null || vals.length == 0)
			return null;
		List<Object[]> args = new ArrayList<Object[]>();
		for (int i = 0; i < vals.length; i++)
			args.add(new Object[] { vals[i] });
		return args;
	}

	int batchUpdate(InvokeMeta invokeMeta, List<Object[]> args) {
		String sql = invokeMeta.getSql();
		if (args == null || args.isEmpty())
			return this.getHitTemplate().execute(sql);
		else
			return this.getHitTemplate().batchExecute(sql, args);
	}

	protected <T> List<T> doQuery(InvokeMeta invokeMeta, Object args[],
			Class<T> wrapClass) {
		Object vals[] = null;
		List<T> results;
		String sql = invokeMeta.getSql();
		if (args != null && args.length > 0)
			vals = this.getArguments(invokeMeta, args);
		if (vals == null || vals.length == 0)
			results = this.getHitTemplate().query(sql, wrapClass);
		else
			results = this.getHitTemplate().query(sql, vals, wrapClass);
		return results;
	}

	protected <T> T doQueryForSimple(InvokeMeta invokeMeta, Object args[],
			Class<T> clazz) {
		Object vals[] = null;
		T t;
		String sql = invokeMeta.getSql();
		// queryForObject(sql, parameterSource, clazz) 单个字段,基础数据类型获取 ,比如 integer
		// queryForObject(sql, parameterSource, new
		// BeanPropertyRowMapper<T>(wrapClass) pojoclass
		if (args != null && args.length > 0)
			vals = this.getArguments(invokeMeta, args);
		if (vals == null || vals.length == 0)
			t = this.getHitTemplate().querySimple(sql, clazz);
		else
			t = this.getHitTemplate().querySimple(sql, vals, clazz);
		return t;
	}

	protected <T> T doQueryForObject(InvokeMeta invokeMeta, Object args[],
			Class<T> clazz) {
		Object vals[] = null;
		T t;
		String sql = invokeMeta.getSql();
		// queryForObject(sql, parameterSource, clazz) 单个字段,基础数据类型获取 ,比如 integer
		// queryForObject(sql, parameterSource, new
		// BeanPropertyRowMapper<T>(wrapClass) pojoclass
		if (args != null && args.length > 0)
			vals = this.getArguments(invokeMeta, args);
		if (vals == null || vals.length == 0)
			t = this.getHitTemplate().queryForObject(sql, clazz);
		else
			t = this.getHitTemplate().queryForObject(sql, vals, clazz);
		return t;
	}

	Object[] getArguments(InvokeMeta invokeMeta, Object args[]) {
		MethodParam[] relations = invokeMeta.getRelations();
		if (relations == null || relations.length == 0)
			return null;
		if (args == null || args.length == 0)
			return null;
		if (args.length == 1)
			return args;
		Object vals[] = new Object[relations.length];
		for (int i = 0; i < vals.length; i++) {
			int idx = relations[i].getIndex();
			vals[i] = args[idx];
		}
		return vals;
	}

	void checkNoParameters(MethodMeta mmeta, String s) {
		if (!mmeta.hasArguments())
			throw new RuntimeException("方法上无参数，entity无法执行[" + s + "]操作");
	}

	void checkHasMethodArguments(InvokeMeta invokeMeta, String s) {
		if (!invokeMeta.hasMethodArguments())
			throw new RuntimeException("方法上无参数，entity无法执行[" + s + "]操作");
	}

	void checkNoParameters(MethodMeta mmeta, String s, int n) {
		if (!mmeta.hasArguments())
			throw new RuntimeException("方法上无参数，entity无法执行操作:" + s);
		else if (mmeta.getParamCount() < n)
			throw new RuntimeException("方法上无参数，entity无法执行操作:" + s + ",至少需要" + n
					+ "个参数");
	}
}
