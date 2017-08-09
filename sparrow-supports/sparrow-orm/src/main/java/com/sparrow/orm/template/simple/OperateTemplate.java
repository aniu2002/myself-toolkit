package com.sparrow.orm.template.simple;

import java.sql.Connection;
import java.util.List;

import com.sparrow.core.log.LoggerManager;
import com.sparrow.orm.session.Session;
import com.sparrow.orm.session.SessionFactory;
import com.sparrow.orm.sql.named.BeanParameterSource;
import com.sparrow.orm.sql.named.SqlParameterSource;
import com.sparrow.orm.template.ExecuteCallback;
import com.sparrow.orm.trans.Transaction;

public abstract class OperateTemplate {
	public static final int DEFAULT_TRANSACTION_LEVAL = Connection.TRANSACTION_READ_COMMITTED;

	public abstract SessionFactory getSessionFactory();

	public final <T> T query(ExecuteCallback<T> callback) {
		if (callback == null)
			return null;
		Session session = this.getSessionFactory().openSession();
		T t = null;
		try {
			t = callback.execute(session);
			return t;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			session.close();
			session = null;
		}
	}

	public final <T> T execute(ExecuteCallback<T> callback) {
		if (callback == null)
			return null;
		Session session = this.getSessionFactory().openSession();
		Transaction tn = new Transaction(session.getConnection());
		tn.setLevel(DEFAULT_TRANSACTION_LEVAL);
		T t = null;
		// Logger.info("-- Begin transaction");
		try {
			t = callback.execute(session);
			tn.commit();
			// Logger.info("-- Commit transaction");
			return t;
		} catch (RuntimeException e) {
			tn.rollBack();
			LoggerManager.getSysLog().error("-- Rollback transaction");
			throw e;
		} catch (Exception e) {
			tn.rollBack();
			LoggerManager.getSysLog().error("-- Rollback transaction");
			throw new RuntimeException(e.getMessage());
		} finally {
			session.close();
			tn = null;
			session = null;
		}
	}

	public SqlParameterSource[] getParameterSources(List<?> list) {
		if (list == null || list.isEmpty())
			return null;
		int len = list.size();
		SqlParameterSource[] paras = new SqlParameterSource[len];
		for (int i = 0; i < len; i++) {
			paras[i] = new BeanParameterSource(list.get(i));
		}
		return paras;
	}
}
