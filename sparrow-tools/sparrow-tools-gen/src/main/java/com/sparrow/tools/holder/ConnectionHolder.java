package com.sparrow.tools.holder;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Administrator on 2016/3/10 0010.
 */
public class ConnectionHolder {
    private static final ThreadLocal<Connection> local = new ThreadLocal<Connection>();

    public static void holdConnection(Connection connection) {
        if (connection == null)
            return;
        local.set(connection);
    }

    public static Connection getConnection(){
        return local.get();
    }

    public static void remove() {
        Connection connection = local.get();
        local.remove();
        local.set(null);
        if (connection != null)
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
}
