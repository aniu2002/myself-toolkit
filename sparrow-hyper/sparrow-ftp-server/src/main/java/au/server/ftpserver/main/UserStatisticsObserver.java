package au.server.ftpserver.main;

import java.net.InetAddress;

import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.ServerDataConnectionFactory;
import org.apache.ftpserver.impl.StatisticsObserver;
import org.apache.ftpserver.usermanager.impl.BaseUser;

public class UserStatisticsObserver implements StatisticsObserver {

	@Override
	public void notifyCloseConnection() {

	}

	@Override
	public void notifyDelete() {

	}

	@Override
	public void notifyDownload() {

	}

	@Override
	public void notifyLogin(FtpIoSession ftpSession) {
		BaseUser user = (BaseUser) ftpSession.getUser();
		System.out.println("SID : " + ftpSession.getId());
		System.out.println("USER: " + user.getName());
		System.out.println("IP  : " + ftpSession.getRemoteAddress().toString());
	}

	@Override
	public void notifyLogout(FtpIoSession ftpSession) {
		Object obj = ftpSession
				.getAttribute(FtpIoSession.ATTRIBUTE_UDP_DATA_CONNECTION);
		if (obj != null && obj instanceof ServerDataConnectionFactory) {
			((ServerDataConnectionFactory) obj).closeDataConnection();
		}
	}

	@Override
	public void notifyLoginFail(InetAddress address) {

	}

	@Override
	public void notifyMkdir() {

	}

	@Override
	public void notifyOpenConnection() {

	}

	@Override
	public void notifyRmdir() {

	}

	@Override
	public void notifyUpload() {

	}

}
