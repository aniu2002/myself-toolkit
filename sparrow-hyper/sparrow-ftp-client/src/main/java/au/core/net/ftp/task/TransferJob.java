package au.core.net.ftp.task;

import java.util.ArrayList;
import java.util.List;

import au.core.net.ftp.basic.FtpConstants;
import au.core.net.ftp.basic.FtpUtils;
import au.core.net.ftp.connection.FtpConnection;
import au.core.net.ftp.listeneer.DefaultListener;
import au.core.net.ftp.listeneer.ListenerSupport;

public class TransferJob implements Runnable {
	private TransTask task;
	private FtpConnection con;

	public TransferJob(TransTask task) {
		this.task = task;
	}

	FtpConnection getFtpConnection() {
		String host, user, password;
		int port;

		host = System.getProperty("ftp.host", "172.16.171.2");
		port = Integer.parseInt(System.getProperty("ftp.port", "21"));
		user = System.getProperty("ftp.user", "admin");
		password = System.getProperty("ftp.password", "admin");

		FtpConnection con = new FtpConnection(host, port);
		ListenerSupport sup = new ListenerSupport();
		if (this.task != null)
			sup.addConnectionListener(this.task);
		else
			sup.addConnectionListener(new DefaultListener());
		con.setListenerSupport(sup);
		con.login(user, password);
		while (!con.isConnected()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (con.isConnected())
			return con;
		else
			con.close();
		return null;
	}

	@Override
	public void run() {
		if (task == null)
			return;
		this.con = this.getFtpConnection();
		if (con == null || !con.isConnected())
			return;
		String path = this.task.getSrcPath();
		if (task.isUpload()) {
			String ftpPath = task.getDistPath();
			char ch = ftpPath.charAt(ftpPath.length() - 1);
			if (ch != '/')
				ftpPath = ftpPath + "/";
			String fileName = FtpUtils.getFileName(path);
			int nam = con.upload(path, ftpPath + fileName, false);
			if (nam == FtpConstants.TRANSFER_STOPPED) {
				System.out.println("file is not exist");
			}
		} else {
			String basePath = task.getDistPath();
			char ch = basePath.charAt(basePath.length() - 1);
			if (ch != '/')
				basePath = basePath + "/";
			String fileName = FtpUtils.getFileName(task.getSrcPath());
			long size = con.size(path);
			task.setTotal(size);
			int nam = con.download(path, basePath + fileName, false);
			if (nam == FtpConstants.TRANSFER_STOPPED) {
				System.out.println("file is not exist");
			}
		}
        con.disconnect();
	}

	public void stop() {
		if (this.con != null)
            con.disconnect();
	}

	public static void doJob(TransTask task) {
		TransferJob job = new TransferJob(task);
		tasks.add(job);
		new Thread(new TransferJob(task)).start();
	}

	public static void stopJob() {
		for (TransferJob job : tasks) {
			job.stop();
		}
	}

	private static List<TransferJob> tasks = new ArrayList<TransferJob>();
}
