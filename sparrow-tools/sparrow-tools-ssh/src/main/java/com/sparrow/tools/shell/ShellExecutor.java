package com.sparrow.tools.shell;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * Created by yuanzc on 2015/9/18.
 */
public class ShellExecutor {
    static final Logger LOGGER = LoggerFactory.getLogger(ShellExecutor.class);

    public static void executeRemoteCmd(String ip, String cmd, String remoteUser, String remotePassword) {
        Connection connection = null;
        Session session = null;

        try {
            connection = new Connection(ip);
            connection.connect();
            connection.authenticateWithPassword(remoteUser, remotePassword);
            session = connection.openSession();
            session.execCommand(cmd);
            LOGGER.info("Execute remote command => " + cmd);
        } catch (IOException e) {
            LOGGER.error(e.toString(), e);
        } finally {
            if (connection != null) {
                connection.close();
            }

            if (session != null) {
                session.close();
            }
        }
    }
}
