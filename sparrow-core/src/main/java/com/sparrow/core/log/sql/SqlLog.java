package com.sparrow.core.log.sql;

import java.util.Iterator;
import java.util.Map;

import com.sparrow.core.utils.date.TimeUtils;

/**
 * 
 * SQL日志输出类，记录SQL语句、SQL参数、执行时间/影响数
 * 
 * @author YZC
 * @version 1.0 (2013-12-16)
 * @modify
 */
public abstract class SqlLog {
	public static final String LOG_SEPARATOR = "-----------------------------------------";
	public static final String TEXT_DESC = "(Text..)";
	final ThreadLocal<Long> localTimer = new ThreadLocal<Long>();

	public void prepare(String sql, Object bean) {
		localTimer.set(System.currentTimeMillis());
		this.info(LOG_SEPARATOR);
		this.info("SQL   : {}", sql);
		this.info(wrapParas(bean));
	}

	public void params(String str) {
		this.info(str);
	}

	public void prepare(String sql, Map<String, Object> map) {
		localTimer.set(System.currentTimeMillis());
		this.info(LOG_SEPARATOR);
		this.info("SQL   : {}", sql);
		this.info(wrapParas(map));
	}

	public void prepare(String sql) {
		localTimer.set(System.currentTimeMillis());
		this.info(LOG_SEPARATOR);
		this.info("SQL   : {}", sql);
	}

	public void prepared(String sql, Object[] beans) {
		localTimer.set(System.currentTimeMillis());
		this.info(LOG_SEPARATOR);
		this.info("SQL   : {}", sql);
		this.info(wrapParas(beans));
	}

	public void prepareProcedure(String procedureName) {
		localTimer.set(System.currentTimeMillis());
		this.info(LOG_SEPARATOR);
		this.info("Procedure   : {}", procedureName);
	}

	public void prepareFunction(String functionName) {
		localTimer.set(System.currentTimeMillis());
		// this.info(LOG_SEPARATOR);
		this.info("Function   : {}", functionName);
	}

	public void effects(int n) {
		Long t = localTimer.get();
		if (t != null) {
			long b = System.currentTimeMillis() - t;
			this.info("Costs : {}ms, Effect rows({})", b, n);
		} else
			this.info("Effect rows({})", n);
		this.info(LOG_SEPARATOR);
	}

	public void effects() {
		Long t = localTimer.get();
		if (t != null) {
			long b = System.currentTimeMillis() - t;
			info("Costs : {}ms", b);
		}
		this.info(LOG_SEPARATOR);
	}

	static String wrapParas(Object bean) {
		if (bean == null)
			return null;
		return bean.toString();
	}

	public final static String wrapParas(Map<String, Object> map) {
		if (map == null || map.isEmpty())
			return null;
		StringBuilder sb = new StringBuilder("Paras : ");
		Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
		Map.Entry<String, Object> entry;
		Object value;
		boolean notFirst = false;

		while (entries.hasNext()) {
			entry = entries.next();
			if (notFirst)
				sb.append(",");
			else
				notFirst = true;
			value = entry.getValue();
			if (value == null) {
				sb.append(entry.getKey()).append("='").append(value)
						.append('\'');
				continue;
			}
			if (value instanceof java.util.Date)
				value = TimeUtils.date2String((java.util.Date) value);
			else if (value instanceof String) {
				String t = (String) value;
				if (t.length() > 50)
					value = TEXT_DESC;
			}
			sb.append(entry.getKey()).append("='").append(value).append('\'');
		}
		return sb.toString();
	}

	static String wrapParas(Object[] beans) {
		if (beans == null || beans.length == 0)
			return null;
		StringBuilder sb = new StringBuilder("Paras : ");
		Object value;
		boolean notFirst = false;

		for (int i = 0; i < beans.length; i++) {
			value = beans[i];
			if (notFirst)
				sb.append(",");
			else
				notFirst = true;
			if (value == null) {
				sb.append('[').append(i).append("]=null");
				continue;
			}
			if (value instanceof java.util.Date)
				value = TimeUtils.date2String((java.util.Date) value);
			else if (value instanceof String) {
				String t = (String) value;
				if (t.length() > 50)
					value = TEXT_DESC;
			}
			sb.append('[').append(i).append("]='").append(value).append('\'');
		}
		return sb.toString();
	}

	protected abstract void info(String msg);

	protected abstract void info(String msg, Object... args);
}
