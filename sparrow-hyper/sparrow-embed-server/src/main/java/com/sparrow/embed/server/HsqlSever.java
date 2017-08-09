package com.sparrow.embed.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl;
import org.hsqldb.server.ServerConfiguration;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-6-13
 * Time: 上午10:24
 * To change this template use File | Settings | File Templates.
 */
public class HsqlSever {

    public static void start(String[] args) {
        HsqlProperties props = new HsqlProperties();
        HsqlProperties stringProps = HsqlProperties.argArrayToProps(args,
                "server");
        if (stringProps != null) {
            if (stringProps.getErrorKeys().length != 0) {
                System.out.println("server.help");
                return;
            }
            props.addProperties(stringProps);
        }
        ServerConfiguration.translateDefaultDatabaseProperty(props);
        ServerConfiguration.translateDefaultNoSystemExitProperty(props);
        Server server = new Server();
        server.setPort(9999);
        try {
            server.setProperties(props);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServerAcl.AclFormatException e) {
            e.printStackTrace();
        }
        server.start();
    }

    public static void startServer() {
        int port = 9999;
        String dbName = "mydb", dbPath = System.getProperty("user.dir");
        Server server = new Server();// 它可是hsqldb.jar里面的类啊。
        server.setDatabaseName(0, dbName);
        server.setDatabasePath(0, dbPath + "/" + dbName);
        if (port != -1) {
            server.setPort(port);
        }
        server.setSilent(true);
        server.start();
        System.out.println("HSQLDB started...");
    }


    public static void close() {
        Connection conn = null;
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            conn = DriverManager.getConnection(
                    "jdbc:hsqldb:hsql://localhost:9999/mydb", "sa", "");
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("SHUTDOWN;");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void socketTest() throws ClassNotFoundException, SQLException {
        Class.forName("org.hsqldb.jdbcDriver");
        // hsqldb.write_delay=false; 和 shutdown=true 参数才能使用上次的数据
        Connection c = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:9999/mydb", "sa", "");
        Statement stmt = c.createStatement();
        //stmt.execute("create table test(a int,b varchar(30)) ");
        stmt.execute("insert into test(a,b) values(10,'测试') ");
        stmt.execute("insert into test(a,b) values(15,'data') ");
        ResultSet rs = stmt.executeQuery("select * from test");
        while (rs.next()) {
            System.out.println(rs.getString(1) + "," + rs.getString(2));
        }
        stmt.close();
        c.close();
        close();
    }

    public static void testStandnone() throws ClassNotFoundException, SQLException {
        startServer();
        socketTest();
    }

    public static void testSever() throws ClassNotFoundException, SQLException {
        String[] args = new String[]{"-database.0", "file:db/mydb", "-dbname.0", "mydb"};
        start(args);
        socketTest();
    }

    public void testFile() throws ClassNotFoundException, SQLException {
        Class.forName("org.hsqldb.jdbcDriver");
        // hsqldb.write_delay=false; 和 shutdown=true 参数才能使用上次的数据
        Connection c = DriverManager.getConnection("jdbc:hsqldb:file:db/testdb;shutdown=true;", "sa", "");
        Statement stmt = c.createStatement();
        //stmt.execute("create table test(a int,b varchar(30)) ");
        stmt.execute("insert into test(a,b) values(10,'测试') ");
        stmt.execute("insert into test(a,b) values(15,'data') ");
        ResultSet rs = stmt.executeQuery("select * from test");
        while (rs.next()) {
            System.out.println(rs.getString(1) + "," + rs.getString(2));
        }
        stmt.close();
        c.close();
    }

    public static void main(String args[]) {
        try {
            //testStandnone();
            testSever();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
