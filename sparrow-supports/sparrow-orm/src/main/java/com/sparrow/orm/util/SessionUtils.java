package com.sparrow.orm.util;

import java.sql.Connection;

import com.sparrow.orm.session.Session;
import com.sparrow.orm.session.SessionFactory;
import com.sparrow.orm.trans.Transaction;

public class SessionUtils {
	private static final ThreadLocal<Session> sessionLocal = new ThreadLocal<Session>();
	private static final ThreadLocal<Transaction> transactionLocal = new ThreadLocal<Transaction>();
	private static final ThreadLocal<Integer> countLocal = new ThreadLocal<Integer>();
	public static final int DEFAULT_TRANSACTION_LEVAL = Connection.TRANSACTION_READ_COMMITTED;

	public static final Session getSession(SessionFactory sessionFactory) {
		Session session = sessionLocal.get();
		if (session == null) {
			session = sessionFactory.openSession();
			sessionLocal.set(session);
		}
		return session;
	}

	public static final Session getSession() {
		return sessionLocal.get();
	}

	public static final void releaseSession() {
		Integer n = countLocal.get();
		if (n == null || n == 0) {
			Session session = sessionLocal.get();
			if (session != null) {
				session.close();
				sessionLocal.set(null);
			}
		}
	}

	public static final Transaction getTransaction() {
		return transactionLocal.get();
	}

	public static final Transaction begin() {
		return begin(getSession(), DEFAULT_TRANSACTION_LEVAL);
	}

	public static final Transaction begin(int level) {
		return begin(getSession(), level);
	}

	public static final Transaction begin(Session session, int level) {
		Transaction tn = transactionLocal.get();
		if (null == tn) {
			tn = new Transaction(session.getConnection());
			tn.setLevel(level);
			transactionLocal.set(tn);
			countLocal.set(1);
		} else
			countLocal.set(countLocal.get() + 1);
		return tn;
	}

	public static final void commit() {
		Integer n = countLocal.get();
		if (n == null)
			throw new RuntimeException("未开启手动事务");
		n = n - 1;
		if (n == 0) {
			countLocal.set(null);
			transactionLocal.get().commit();
			transactionLocal.set(null);
			releaseSession();
		} else
			countLocal.set(n);
	}

	public static final void rollback() {
		Integer n = countLocal.get();
		if (n == null)
			throw new RuntimeException("未开启手动事务");
		countLocal.set(null);
		Transaction transaction = transactionLocal.get();
		// if (transaction != null)
		transaction.rollBack();
		transactionLocal.set(null);
		releaseSession();
	}

	public static final void closeSession() {
		Integer n = countLocal.get();
		if (n == null)
			n = 0;
		// 有事务，不关闭
		if (n > 0)
			return;
		Session curSession = sessionLocal.get();
		if (curSession != null) {
			curSession.close();
			sessionLocal.set(null);
		}
	}
}
