package com.sparrow.orm.pool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import com.sparrow.core.log.Logger;
import com.sparrow.core.log.LoggerManager;
import com.sparrow.core.config.SystemConfig;

public class ConnectionPool {
    protected static final Map<String, ConnectionPool> POOL_MAP = new HashMap<String, ConnectionPool>();
    protected final static AtomicInteger gcounter = new AtomicInteger();
    protected DbConfig config;
    protected boolean isInitialized = false;
    // 空闲的数目
    private AtomicInteger free = new AtomicInteger(0);
    // 创建连接的步进数
    private int incrementNum = 3;
    // 总连接数
    private AtomicInteger counter = new AtomicInteger(0);
    private volatile boolean available = true;
    protected _Connection[] connections;
    protected Logger logger = LoggerManager.getSysLog();

    static {
        initializeTimer();
    }

    public ConnectionPool(DbConfig config) {
        this.config = config;
        if (POOL_MAP.get(config.poolName) != null) {
            throw new RuntimeException("Exist pool : " + config.poolName);
        }
        POOL_MAP.put(config.poolName, this);
        try {
            Class.forName(config.driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("加载驱动异常：" + config.driver);
        }
        connections = new _Connection[config.maxActive];
        try {
            int n = 0;
            for (int i = 0; i < config.minIdle; i++) {
                _Connection con;
                con = this.createConnection(config);
                if (con != null) {
                    connections[i] = con;
                    n++;
                }
            }
            this.free.set(n);
            this.counter.set(n);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取数据库连接失败:" + config.url);
        }
        this.isInitialized = true;
    }

    public final static synchronized ConnectionPool getDbPool(DbConfig config) {
        ConnectionPool pool = POOL_MAP.get(config.poolName);
        if (pool == null) {
            pool = new ConnectionPool(config);
        }
        return pool;
    }

    public void distroy() {
        POOL_MAP.remove(this.config.poolName + ".");
    }

    private static void initializeTimer() {
        Timer backupTimer = new Timer(true);
        int f = SystemConfig.getInt("jdbc.pool.check.period", 6);
        long offset = f * 60 * 1000;
        backupTimer.scheduleAtFixedRate(new Checker(), offset, offset);
    }

    public _Connection[] getConns() {
        return connections;
    }

    public void clear() {
        if (connections == null) {
            return;
        }
        for (int i = 0; i < connections.length; i++) {
            try {
                if (connections[i] != null) {
                    connections[i].conn.close();
                    connections[i] = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        this.counter.set(0);
        POOL_MAP.remove(config.poolName + ".");
    }

    public DbConfig getDbConfig() {
        return config;
    }

    static Connection createJdbcConnection(DbConfig dbc) throws SQLException {
        Connection conn = DriverManager.getConnection(dbc.url, dbc.user,
                dbc.password);
        return conn;
    }

    private _Connection createConnection(DbConfig dbc) throws SQLException {
        Connection conn = DriverManager.getConnection(dbc.url, dbc.user,
                dbc.password);
        _Connection dbconn = new _Connection(conn, dbc);
        dbconn.conn = conn;
        // dbconn.used = used;
        dbconn.lastAccessTime = System.currentTimeMillis();
        dbconn.poolName = dbc.poolName;
        return dbconn;
    }

    public Connection getConnection() {
        return this.getConnection(this.config.longTimeFlag);
    }

    boolean checkMySqlOnline(_Connection conn) {
        if (conn == null || conn.conn == null)
            return true;
        try {
            conn.conn.createStatement().execute("select 1");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public Connection getConnection(boolean bLongTimeFlag) {
        if (!this.isInitialized) {
            throw new RuntimeException("数据库连接异常，导致连接池不能使用，无法提供数据库连接");
        }
        long now = System.currentTimeMillis();
        _Connection conn = null;
        synchronized (this.connections) {
            // 并发同步问题，导致可能重复创建连接
            int len = this.counter.get();
            for (int i = 0; i < len; i++) {
                conn = this.connections[i];
                if (!conn.used) {
                    conn.used = true;
                    conn.lastAccessTime = now;
                    this.free.decrementAndGet();
                    return conn.getConnection();
                }
            }
            if (len < config.maxActive) {
                try {
                    if (len == 0)
                        conn = this.incrementAndGetConnection(this.connections,
                                config, len, config.minIdle, config.maxActive);
                    else
                        conn = this.incrementAndGetConnection(this.connections,
                                config, len, this.incrementNum,
                                config.maxActive);
                    if (conn == null)
                        return null;
                    conn.used = true;
                    this.free.decrementAndGet();
                    return conn.getConnection();
                } catch (SQLException e) {
                    this.available = false;
                    throw new RuntimeException("获取数据库连接失败:" + config.url);
                }
            } else {
                throw new RuntimeException("DBConnPoolImpl," + config.poolName
                        + ":Can't create more pool!");
            }
        }
    }

    private _Connection incrementAndGetConnection(_Connection[] conns,
                                                  DbConfig config, final int len, final int nums, final int max)
            throws SQLException {
        _Connection firstConn = null;
        int realCounts = 0;
        for (int i = len; i < max && realCounts < nums; i++) {
            _Connection conn = this.createConnection(this.config);
            if (conn == null)
                continue;
            if (firstConn == null)
                firstConn = conn;
            realCounts++;
            conns[i] = conn;
        }
        this.counter.addAndGet(realCounts);
        this.free.addAndGet(realCounts);
        return firstConn;
    }

    public boolean isAvailable() {
        return this.available;
    }

    public int getFree() {
        return this.free.get();
    }

    public int getCounter() {
        return this.counter.get();
    }

    public int getActive() {
        int active = this.counter.get() - this.free.get();
        return active;
    }

    public int getMaxSize() {
        return config.maxActive;
    }

    boolean checkFree(_Connection conn, long offset) {
        if (!conn.used)
            return true;
        if (conn.used) {
            // 如果时间大于了刷新时间，将重置used状态
            if (offset >= config.refershPeriod) {
                try {
                    if (!conn.conn.getAutoCommit()) {
                        conn.conn.rollback();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                conn.used = false;// 回收
                this.free.incrementAndGet();
                return true;
            }
        }
        return false;
    }

    private void check() {
        _Connection conn = null;
        boolean removed = false;
        long now = System.currentTimeMillis();
        int len = this.counter.get();
        synchronized (this.connections) {
            _Connection cons[] = new _Connection[config.maxActive];
            int j = 0, delc = 0, freeCount = 0;
            boolean needRemoveFree = false;
            for (int i = 0; i < len; i++) {
                removed = false;
                conn = this.connections[i];
                if (conn == null) {
                    delc++;
                    this.free.decrementAndGet();
                    continue;
                } else if (!conn.available) {
                    delc++;
                    try {
                        conn.close();
                        conn = null;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    this.free.decrementAndGet();
                    continue;
                }
                long offset = now - conn.lastAccessTime;
                // Logger.info("Conn[" + conn + "]-{Used:" + conn.used + "}");
                if (this.checkFree(conn, offset))
                    freeCount++;

                if (freeCount > config.minIdle)
                    needRemoveFree = true;
                // need remove this connection
                if (this.checkRemoved(conn, offset, needRemoveFree)) {
                    removed = true;
                    gcounter.decrementAndGet();
                    delc++;
                }
                // is not removed , store the connection
                if (!removed) {
                    cons[j] = conn;
                    j++;
                }
            }
            this.counter.set(len - delc);
            logger.debug(this.getPoolInfo());
            this.connections = cons;
            if (cons.length > 0) {
                this.available = this.checkMySqlOnline(cons[0]);
            }
        }
    }

    private boolean checkRemoved(_Connection conn, long offset,
                                 boolean removeFree) {
        // 回收长时间不用的
        if (conn.used) {
            return false;
        } else {
            // 空闲的已经足够多了，不需要保留了，则删除
            if (removeFree) {
                try {
                    conn.conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                this.free.decrementAndGet();
                return true;
            }
        }

        // 是否为长时贮存，并且最大等待时间不为-1
        if (!conn.longTimeFlag && config.maxWait != -1) {
            // 未使用的,有空闲的
            // removeFree && (offset >= config.maxWait)
            if ((offset >= config.maxWait)) {
                try {
                    if (!conn.conn.getAutoCommit()) {
                        conn.conn.rollback();
                    }
                    conn.conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                logger.debug(
                        "Connection[\"{}\"] removed !!  longTimeFlag : {} , maxWait : {} , lastAccessTime : {}",
                        conn.name, conn.longTimeFlag, config.maxWait,
                        conn.lastAccessTime);
                this.free.decrementAndGet();
                return true;
            }
        }
        return false;
    }

    public String getPoolInfo() {
        StringBuffer sb = new StringBuffer();
        int active = this.counter.get() - this.free.get();
        sb.append("[PoolName:\"").append(this.config.poolName)
                .append("\",Connected:").append(this.counter)
                .append(",Active:").append(active).append(",Free:")
                .append(this.free).append(",MaxSize:")
                .append(this.config.maxActive).append("]");
        return sb.toString();
    }

    public String getPoolDesc() {
        StringBuffer sb = new StringBuffer();
        int active = this.counter.get() - this.free.get();
        sb.append("[name:\"").append(this.config.poolName)
                .append("\",connected:").append(this.counter)
                .append(",active:").append(active).append(",free:")
                .append(this.free).append(",max:")
                .append(this.config.maxActive).append("]");
        return sb.toString();
    }

    private static class Checker extends TimerTask {
        public void run() {
            try {
                Iterator<ConnectionPool> iterator = POOL_MAP.values()
                        .iterator();
                ConnectionPool pool;
                while (iterator.hasNext()) {
                    pool = iterator.next();
                    pool.check();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError oome) {
                oome.printStackTrace(System.err);
            }
        }
    }

    class _Connection implements InvocationHandler {
        private final static String CLOSE_METHOD_NAME = "close";
        protected boolean longTimeFlag = false;
        protected String poolName = null;
        protected String name = null;
        protected Connection conn = null;
        private Connection _proxy_conn = null;
        private DbConfig dbConfig;
        // 数据库的忙状态
        protected volatile boolean used = false;
        // 是否可用，不可用需要回收
        protected volatile boolean available = true;
        // 用户最后一次访问该连接方法的时间
        protected volatile long lastAccessTime;

        _Connection(Connection conn, DbConfig dbConfig) {
            this.conn = conn;
            this.dbConfig = dbConfig;
            this.name = "connection-"
                    + String.valueOf(gcounter.incrementAndGet());
            this.longTimeFlag = dbConfig.longTimeFlag;
            this._proxy_conn = this.createProxyConnection();
        }

        Connection createProxyConnection() {
            Class<?>[] interfaces = {java.sql.Connection.class};
            Connection proxyConn = (Connection) Proxy.newProxyInstance(
                    ConnectionPool.class.getClassLoader(), interfaces, this);
            return proxyConn;
        }

        void resetConnection() throws SQLException {
            this.conn = null;
            this.conn = ConnectionPool.createJdbcConnection(this.dbConfig);
        }

        public Connection getConnection() {
            // Logger.debug("** Get connection[\"{}\"] from pool [\"{}\"]",
            // this.name, this.poolName);
            // 返回数据库连接conn的接管类，以便截住close方法
            return this._proxy_conn;
        }

        /**
         * 该方法真正的关闭了数据库的连接
         *
         * @throws SQLException
         */
        void close() throws SQLException {
            // 由于类属性conn是没有被接管的连接，因此一旦调用close方法后就直接关闭连接
            this.conn.close();
        }

        void kick() {
            this.lastAccessTime = System.currentTimeMillis();
        }

        Object retryx(Method m, Object[] args) {
            Object obj = null;
            try {
                this.resetConnection();
                obj = m.invoke(this.conn, args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
                this.available = false;
            }
            return obj;
        }

        @Override
        public Object invoke(Object proxy, Method m, Object[] args)
                throws Throwable {
            Object obj = null;
            // 判断是否调用了close的方法，如果调用close方法则把连接置为无用状态
            if (CLOSE_METHOD_NAME.equals(m.getName())) {
                // Logger.debug("** Return connection[\"{}\"] to pool [\"{}\"] ",
                // this.name, this.poolName);
                ConnectionPool.this.free.incrementAndGet();
                this.used = false;
            } else {
                try {
                    obj = m.invoke(this.conn, args);
                } catch (InvocationTargetException e) {
                    if (e.getTargetException() instanceof java.net.SocketException) {
                        this.available = false;
                        ConnectionPool.this.available = false;
                        // obj = this.retry(m, args);
                        throw e.getTargetException();
                    } else if (e.getTargetException() instanceof java.sql.SQLException) {
                        java.sql.SQLException ex = (SQLException) e
                                .getTargetException();
                        if ("08003".equals(ex.getSQLState())) {
                            this.available = false;
                            ConnectionPool.this.available = false;
                        }
                        throw ex;
                    } else
                        throw e.getTargetException();
                    // if (e.getTargetException() instanceof
                    // java.net.SocketException) {
                    // obj = this.retry(m, args);
                    // } else
                    // throw e;
                }
            }
            // 设置最后一次访问时间，以便及时清除超时的连接
            this.lastAccessTime = System.currentTimeMillis();
            return obj;
        }
    }
}
