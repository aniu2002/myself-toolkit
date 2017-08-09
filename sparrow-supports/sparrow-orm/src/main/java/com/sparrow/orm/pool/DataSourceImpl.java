package com.sparrow.orm.pool;

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.sparrow.core.log.SysLogger;


public class DataSourceImpl implements DataSource {
	private ConnectionParam connParam = null;
	private List<_Connection> conns = new ArrayList<_Connection>();;
	private int connectionCount = 0;

	public DataSourceImpl(ConnectionParam param) {
		this.connParam = param;
	}

	public Connection getConnection() throws SQLException {
		return null;
	}

	public Connection getConnection(String user, String password)
			throws SQLException {
		// 首先从连接池中找出空闲的对象
		Connection conn = getFreeConnection(0);
		if (conn == null) {
			// 判断是否超过最大连接数,如果超过最大连接数
			// 则等待一定时间查看是否有空闲连接,否则抛出异常告诉用户无可用连接
			if (getConnectionCount() >= connParam.getMaxConnection())
				conn = getFreeConnection(connParam.getWaitTime());
			else {// 没有超过连接数，重新获取一个数据库的连接
				connParam.setUser(user);
				connParam.setPassword(password);
				Connection conn2 = DriverManager.getConnection(connParam
						.getUrl(), user, password);
				// 代理将要返回的连接对象
				_Connection _conn = new _Connection(conn2);
				synchronized (conns) {
					conns.add(_conn);
				}
				conn = _conn.getConnection();
			}
		}
		connectionCount++;
		return conn;
	}

	private int getConnectionCount() {
		return connectionCount;
	}

	/**
	 * 从连接池中取一个空闲的连接
	 * 
	 * @param nTimeout
	 *            如果该参数值为0则没有连接时只是返回一个null 否则的话等待nTimeout毫秒看是否还有空闲连接，如果没有抛出异常
	 * @return Connection
	 * @throws SQLException
	 */
	protected synchronized Connection getFreeConnection(long nTimeout)
			throws SQLException {
		Connection conn = null;
		Iterator<_Connection> iter = conns.iterator();
		while (iter.hasNext()) {
			_Connection _conn = (_Connection) iter.next();
			if (!_conn.used) {
				conn = _conn.getConnection();
				_conn.used = true;
				break;
			}
		}
		if (conn == null && nTimeout > 0) {
			// 等待nTimeout毫秒以便看是否有空闲连接
			try {
				Thread.sleep(nTimeout);
			} catch (Exception e) {
			}
			conn = getFreeConnection(0);
			if (conn == null)
				throw new SQLException("没有可用的数据库连接");
		}
		return conn;
	}

	public PrintWriter getLogWriter() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getLoginTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
	}

	public void setLoginTimeout(int seconds) throws SQLException {

	}

	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return false;
	}

	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return null;
	}

	public void stop() {

	}

	public int close() throws SQLException {
		int cc = 0;
		SQLException excp = null;
		Iterator<_Connection> iter = conns.iterator();
		while (iter.hasNext()) {
			try {
				((_Connection) iter.next()).close();
				cc++;
			} catch (Exception e) {
				if (e instanceof SQLException)
					excp = (SQLException) e;
			}
		}
		if (excp != null)
			throw excp;
		return cc;
	}

	public void initConnection() {
		try {
			Class.forName(connParam.getDriver());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	class _Connection implements InvocationHandler {
		private final static String CLOSE_METHOD_NAME = "close";
		protected boolean longTimeFlag = false;
		protected String poolName = null;
		protected Connection conn = null;
		private Connection _proxy_conn = null;
		// 数据库的忙状态
		protected boolean used = false;
		// 用户最后一次访问该连接方法的时间
		protected long lastAccessTime;

		_Connection(Connection conn) {
			this.conn = conn;
			this._proxy_conn = (Connection) Proxy.newProxyInstance(conn
					.getClass().getClassLoader(), conn.getClass()
					.getInterfaces(), this);
			// this.used = inUse;
		}

		/**
		 * Returns the conn.
		 * 
		 * @return Connection
		 */
		public Connection getConnection() {
			// 返回数据库连接conn的接管类，以便截住close方法
			return _proxy_conn;
		}

		/**
		 * 该方法真正的关闭了数据库的连接
		 * 
		 * @throws SQLException
		 */
		void close() throws SQLException {
			// 由于类属性conn是没有被接管的连接，因此一旦调用close方法后就直接关闭连接
			conn.close();
		}

		/**
		 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
		 *      java.lang.reflect.Method, java.lang.Object)
		 */
		public Object invoke(Object proxy, Method m, Object[] args)
				throws Throwable {
			Object obj = null;
			// 判断是否调用了close的方法，如果调用close方法则把连接置为无用状态
			if (CLOSE_METHOD_NAME.equals(m.getName())) {
				SysLogger.info("Proxy close method invoke .... ");
				used = false;
			} else
				obj = m.invoke(conn, args);
			// 设置最后一次访问时间，以便及时清除超时的连接
			lastAccessTime = System.currentTimeMillis();
			return obj;
		}
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

}
