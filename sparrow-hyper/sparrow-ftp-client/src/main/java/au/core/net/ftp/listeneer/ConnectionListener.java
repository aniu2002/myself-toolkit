package au.core.net.ftp.listeneer;

import au.core.net.ftp.connection.FtpConnection;

/**
 * 
 * @author Yzc 2008-10-30
 * 
 */
public interface ConnectionListener {
	public void updateRemoteDirectory(String pwd);

	public void updateProgress(String file, String type, long bytes);

	public void connectionInitialized();

	public void connectionFailed(String why);

	public void actionFinished(FtpConnection connection);
}
