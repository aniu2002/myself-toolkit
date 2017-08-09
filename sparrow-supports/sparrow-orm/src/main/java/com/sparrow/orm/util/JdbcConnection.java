package com.sparrow.orm.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
/**
* 数据库连接工具类
* @version 1.0
*/
public class JdbcConnection {
    // 定义线程本地变量，每个线程访问它都会获得不同的对象
    // 使用ThreadLocal使一个连接绑定到一个线程上
    private static ThreadLocal<Connection> currentConnection = new ThreadLocal<Connection>();
    private static String username="system";    //用户名，都有效
    private static String password="admin";        //密码，都有效
    private static String dbName="orcl";        //数据库名称，access无效
    private static String ip="127.0.0.1";        //数据库服务器IP地址，access无效
    private static String dataPath = "/dbName.mdb";    //access有效
    private static String resourceName=null;    //为null时不使用连接池，或者起名 jdbc/mysql，或者jdbc/oracle，或者jdbc/access，或者jdbc/derby
    private static String databaseType = "mysql";    //四个值：access mysql oracle derby ，必须设定
    /**
     * 
     * @return 得到一个数据库连接
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = currentConnection.get();
        if (conn == null) {
            if(null==resourceName){
                if("mysql".equals(databaseType.toLowerCase())){
                    conn = getMySqlConnection();
                }else if("oracle".equals(databaseType.toLowerCase())){
                    conn = getOracleConnection();
                }else if("derby".equals(databaseType.toLowerCase())){
                    conn = getDerbyConnection();
                }else if("access".equals(databaseType.toLowerCase())){
                    conn = getAccessConnection();
                }else{
                    System.out.println("在 JdbcConnection.java 中数据库类型没有设置");
                    throw new SQLException("数据库类型未设置");
                }
            }else{
                conn = getConnectionByPool();
            }            
            currentConnection.set(conn);
        }
        return conn;
    }
    /**
     * 关闭Oracle数据库连接
     * @throws SQLException
     */
    public static void closeConnection() throws SQLException{
        Connection conn = currentConnection.get();
        conn.close();
        currentConnection.set(null);
    }
    //获得Oracle数据库连接
    private static Connection getOracleConnection(){
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();    //加载驱动
            conn= DriverManager.getConnection("jdbc:oracle:thin:@"+ip+":1521:"+dbName,username,password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Oracle驱动没找到");
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
    //获得MySql数据库连接
    private static Connection getMySqlConnection(){
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();    //加载驱动
            String url = "jdbc:mysql://"+ip+":3306/"+dbName+"?useUnicode=true&characterEncoding=utf8";
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("MySql驱动没找到");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }        
        return conn;
    }
    //获取Derby数据库连接
    private static Connection getDerbyConnection(){
        Connection conn = null;
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();    //加载驱动
            String url = "jdbc:derby://"+ip+":1527/"+dbName+";create=true";
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Derby驱动没找到");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }        
        return conn;
    }
    //获得Access数据库连接
    private static Connection getAccessConnection(){
        Connection conn = null;
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");    //加载驱动
            String url ="jdbc:odbc:driver={Microsoft Access Driver (*.mdb)};DBQ=";
            String dataFile = new File(dataPath).getAbsolutePath();
            conn = DriverManager.getConnection(url+dataFile,username,password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Access驱动没找到");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
    //获取连接池连接
    private static Connection getConnectionByPool(){        
        try {
            Context ctx = new InitialContext();
            Context subContext = (Context)ctx.lookup("java:comp/env");
            String dsName="";
            dsName = resourceName;
            
            DataSource dataSource = (DataSource)subContext.lookup(dsName);
            //上面两句可以合写成下边这句
            //ctx.lookup("java:comp/env/jdbc/oracle");// java:comp/env/ 规定：加前缀指定资源
            return dataSource.getConnection();
        }
          catch (NamingException e) {e.printStackTrace();}
          catch (SQLException e) {e.printStackTrace();}
        return null;        
    }
}
 

