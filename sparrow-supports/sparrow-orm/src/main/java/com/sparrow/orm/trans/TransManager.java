package com.sparrow.orm.trans;

import com.sparrow.orm.session.SessionFactory;
import com.sparrow.orm.util.SessionUtils;

/**
 * 用模板的方式操作事务
 * 
 * @author au
 * 
 */
public class TransManager {
	SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void begin(int level) {
		SessionUtils.getSession(sessionFactory);
		SessionUtils.begin(level);
	}

	public void commit() {
		SessionUtils.commit();
	}

	public void rollback() {
		SessionUtils.rollback();
	}

	public final Transaction currentTranscation() {
		return SessionUtils.getTransaction();
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
