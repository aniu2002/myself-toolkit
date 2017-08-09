package au.server.ftpserver.main;

import org.apache.ftpserver.ftplet.FtpException;

import au.server.ftpserver.log.LogLevelManager;
import au.server.ftpserver.utils.ConfigCopyUtil;
import au.server.ftpserver.utils.PathUtil;

public class Starter {
	public static void main(String args[]) {
		// System.setProperty("file.encoding", "GBK");
		ConfigCopyUtil.configureFileSetup(Starter.class);
		System.setProperty("log4j.configuration", PathUtil.getLogPath());
		LogLevelManager.getInstance().getLogLevel().startLog();
		FtpInstance ftpInstance = FtpServerManager.getInstance().getFtpServer();
		try {
			ftpInstance.start();
		} catch (FtpException e) {
			e.printStackTrace();
		}

		try {
			Thread.sleep(50000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FtpServerManager.getInstance().stopFtpSession();
		FtpServerManager.getInstance().getFtpServer().stop();
	}
}
