package au.core.net.ftp.listeneer;

import java.util.Vector;

import au.core.net.ftp.connection.FtpConnection;

public class ListenerSupport {
    private Vector<ConnectionListener> listeners = new Vector<ConnectionListener>();

    /**
     * @param l 给connection增加监视器
     */
    public void addConnectionListener(ConnectionListener l) {
        listeners.add(l);
    }

    /**
     * @param l 设置监视器
     */
    public void setConnectionListeners(Vector l) {
        listeners = l;
    }

    /**
     * @param pwd 通知路径更改
     */
    public void fireDirectoryUpdate(String pwd) {
        if (listeners == null) {
            return;
        } else {
            for (int i = 0; i < listeners.size(); i++) {
                ((ConnectionListener) listeners.elementAt(i))
                        .updateRemoteDirectory(pwd);
            }
        }
    }

    /**
     * @param file
     * @param type
     * @param bytes 通知文件传输字节数
     */
    public void fireProgressUpdate(String file, String type, long bytes) {
        if (listeners == null) {
            return;
        } else {
            for (int i = 0; i < listeners.size(); i++) {
                ConnectionListener listener = (ConnectionListener) listeners
                        .elementAt(i);
                listener.updateProgress(file, type, bytes);
            }
        }
    }

    /**
     * 通知ftp连接已经初始化
     */
    public void fireConnectionInitialized() {
        if (listeners == null) {
            return;
        } else {
            for (int i = 0; i < listeners.size(); i++) {
                ((ConnectionListener) listeners.elementAt(i))
                        .connectionInitialized();
            }
        }
    }

    /**
     * @param why 通知ftp连接失败
     */
    public void fireConnectionFailed(String why) {
        if (listeners == null) {
            return;
        } else {
            for (int i = 0; i < listeners.size(); i++) {
                ((ConnectionListener) listeners.elementAt(i))
                        .connectionFailed(why);
            }
        }
    }

    /**
     * @param connection 通知ftp文件传输完成
     */
    public void fireActionFinished(FtpConnection connection) {
        if (listeners == null) {
            return;
        } else {
            for (int i = 0; i < listeners.size(); i++) {
                ((ConnectionListener) listeners.elementAt(i))
                        .actionFinished(connection);
            }
        }
    }
}
