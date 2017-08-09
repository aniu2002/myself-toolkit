package au.server.ftpserver.main;

import java.io.File;
import java.util.Set;

import org.apache.ftpserver.FtpConstants;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.ServerFtpStatistics;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;

import au.server.ftpserver.cmd.DWUDPBUF;
import au.server.ftpserver.cmd.UDPPORT;
import au.server.ftpserver.utils.PathUtil;

public class FtpInstance implements FtpServer {
	private FtpServer server;
	private FtpServerFactory serverFactory;
	private PropertiesUserManagerFactory userManagerFactory;
	private static UserManager userManager;
	private ListenerFactory factory;
	private boolean isStarted;
	private Listener listenter;

	public boolean isStarted() {
		return isStarted;
	}

	public static UserManager getUserManager() {
		return userManager;
	}

	public void start() throws FtpException {
		serverFactory = new FtpServerFactory();
		userManagerFactory = new PropertiesUserManagerFactory();
		userManagerFactory.setFile(new File(PathUtil.getUserPath()));
		userManager = userManagerFactory.createUserManager();
		BaseUser user = (BaseUser) userManager.getUserByName("admin");
		System.out.println("set HomePath:"
				+ System.getProperty("admin.home.directory"));
		String home = System.getProperty("admin.home.directory");
		if (home != null && !home.equals(user.getHomeDirectory())) {
            user.setHomeDirectory(System.getProperty("admin.home.directory"));
            user.setPassword("admin");
			userManager.save(user);
		}
		serverFactory.setUserManager(userManager);
		serverFactory.getCommandFactory()
				.addCommand("DWUDPBUF", new DWUDPBUF());
		serverFactory.getCommandFactory().addCommand("UDPPORT", new UDPPORT());
		// register statistics objserver
		((ServerFtpStatistics) serverFactory.getFtpStatistics())
				.setObserver(new UserStatisticsObserver());

		server = serverFactory.createServer();
		serverFactory.getListeners().clear();
		factory = new ListenerFactory();
		factory.setPort(Integer.valueOf(System.getProperty(
				FtpConstants.ServerPort_Key, "21")));
		factory.setIdleTimeout(60 * 60);
		listenter = factory.createListener();
		serverFactory.getListeners().put("default", listenter);
		// start the server
		server.start();
		isStarted = true;
	}

	public Set<FtpIoSession> getActiveSessions() {
		return listenter != null ? listenter.getActiveSessions() : null;
	}

	public void setMaxLoginUser(int maxUser) {
		if (serverFactory != null)
			serverFactory.getConnectionConfig().setMaxLogins(maxUser);
	}

	public void setDataConnectTimeOut(int time) {
		if (factory != null)
			factory.getDataConnectionConfiguration().setIdleTime(time);
	}

	public void stop() {
		isStarted = false;
		server.stop();
		System.out.println("FTP server stoped");
	}

	public boolean isStopped() {
		return server.isStopped();
	}

	public void suspend() {
		server.suspend();
	}

	public void resume() {
		server.resume();
	}

	public boolean isSuspended() {
		return server.isSuspended();
	}

	public FtpServer getFtpServer() {
		return this.server;
	}
}
