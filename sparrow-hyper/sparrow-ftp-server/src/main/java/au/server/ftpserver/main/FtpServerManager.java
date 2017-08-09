package au.server.ftpserver.main;

import java.util.Iterator;
import java.util.Set;

import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.ServerDataConnectionFactory;

public class FtpServerManager {

	private static FtpServerManager single = new FtpServerManager();
	private FtpInstance ftpServer;

	private FtpServerManager() {
		ftpServer = new FtpInstance();
	}

	public static FtpServerManager getInstance() {
		if (single == null)
			single = new FtpServerManager();
		return single;
	}

	public FtpInstance getFtpServer() {
		return ftpServer;
	}

	public void stopFtpSession() {
		if (ftpServer == null)
			return;

		Set<FtpIoSession> ioSession = ftpServer.getActiveSessions();
		if (ioSession != null) {
			for (Iterator<FtpIoSession> it = ioSession.iterator(); it.hasNext();) {
				FtpIoSession session = it.next();
				Object obj = session
						.getAttribute(FtpIoSession.ATTRIBUTE_UDP_DATA_CONNECTION);
				if (obj != null && obj instanceof ServerDataConnectionFactory) {// 关闭UDP数据链路
					((ServerDataConnectionFactory) obj).closeDataConnection();
				}
				session.getDataConnection().closeDataConnection();// 关闭TCP数据链路
				session.close(false).awaitUninterruptibly(1);
			}
		}
	}
}
