package com.sparrow.orm.trans;

import com.sparrow.orm.session.Session;
import com.sparrow.orm.session.SessionFactory;

/**
 * 用模板的方式操作事务
 * 
 * @author au
 * 
 */
public class OTransManager {
	ThreadLocal<Transaction> trans = new ThreadLocal<Transaction>();
	ThreadLocal<Session> session = new ThreadLocal<Session>();
	ThreadLocal<Integer> count = new ThreadLocal<Integer>();
	SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @return 当前线程的事务，如果没有事务，返回 null
	 */
	public Transaction get() {
		return trans.get();
	}

	Session getCurSession() {
		Session curSession = session.get();
		if (curSession == null) {
			curSession = this.sessionFactory.openSession();
			session.set(curSession);
		}
		return curSession;
	}

	public void begin(int level) {
		if (null == trans.get()) {
			Transaction tn = this.getCurSession().beginTranscation();
			tn.setLevel(level);
			trans.set(tn);
			count.set(1);
		} else
			count.set(count.get() + 1);
	}

	public void commit() {
		Integer n = count.get();
		if (n == null)
			throw new RuntimeException("未开启手动事务");
		n = n - 1;
		if (n == 0) {
			count.set(null);
			trans.get().commit();
			trans.set(null);
			this.closeSession();
			// SessionUtils.releaseSession(this.sessionFactory);
		} else
			count.set(n);
	}

	public void rollback() {
		Integer n = count.get();
		if (n == null)
			throw new RuntimeException("未开启手动事务");
		count.set(null);
		Transaction transaction = trans.get();
		// if (transaction != null)
		transaction.rollBack();
		trans.set(null);
		this.closeSession();
		// SessionUtils.releaseSession(sessionFactory);
	}

	public final Transaction currentTranscation() {
		return trans.get();
	}

	final void closeSession() {
		Session curSession = session.get();
		if (curSession != null) {
			curSession.close();
			session.set(null);
		}
	}
	/**
	 * <p>
	 * 你可以设置的事务级别是：
	 * <ul>
	 * <li>java.sql.Connection.TRANSACTION_NONE
	 * <li>java.sql.Connection.TRANSACTION_READ_UNCOMMITTED
	 * <li>java.sql.Connection.TRANSACTION_READ_COMMITTED
	 * <li>java.sql.Connection.TRANSACTION_REPEATABLE_READ
	 * <li>java.sql.Connection.TRANSACTION_SERIALIZABLE
	 * </ul>
	 */
}
