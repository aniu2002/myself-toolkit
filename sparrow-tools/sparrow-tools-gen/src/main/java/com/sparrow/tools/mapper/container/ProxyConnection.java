package com.sparrow.tools.mapper.container;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

public class ProxyConnection implements InvocationHandler {
	private final static String CLOSE_METHOD_NAME = "close";
	private Connection conn = null;
	private Connection _proxy_conn = null;

	public ProxyConnection(Connection conn) {
		this.conn = conn;
		Class<?>[] interfaces = { java.sql.Connection.class };
		this._proxy_conn = (Connection) Proxy.newProxyInstance(conn.getClass()
				.getClassLoader(), interfaces, this);
	}

	public Connection getConnection() {
		return _proxy_conn;
	}

	/**
	 * 该方法真正的关闭了数据库的连接
	 * 
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		// 由于类属性conn是没有被接管的连接，因此一旦调用close方法后就直接关闭连接
		conn.close();
	}

	public Object invoke(Object proxy, Method m, Object[] args)
			throws Throwable {
		Object obj = null;
		// 判断是否调用了close的方法，如果调用close方法则把连接置为无用状态
		if (CLOSE_METHOD_NAME.equals(m.getName())) {
			return obj;
		} else
			obj = m.invoke(conn, args);
		return obj;
	}
}