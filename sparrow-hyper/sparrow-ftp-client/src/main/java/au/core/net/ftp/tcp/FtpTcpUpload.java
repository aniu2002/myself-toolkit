package au.core.net.ftp.tcp;

import au.core.net.ftp.basic.FtpConstants;
import au.core.net.ftp.connection.FtpConnection;
import au.core.net.ftp.listeneer.DefaultListener;
import au.core.net.ftp.listeneer.ListenerSupport;

public class FtpTcpUpload {
	public static void main(String args[]) {
		// File dir = new File("D:\\test\\test\\temp");
		// for (int i = 500; i < 1500; i++) {
		// File file = new File(dir, "file" + i + ".txt");
		// try {
		// file.delete();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		FtpConnection con = new FtpConnection("127.0.0.1", 21);
		ListenerSupport sup = new ListenerSupport();
		sup.addConnectionListener(new DefaultListener());
		con.setListenerSupport(sup);
		con.login("admin", "admin");
		while (!con.isConnected()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		con.mkdir("/test1213/");
		con.sendCmd("TRANSTASK test1 0 1");
		// con.sendCmd("UPUDPBUF 0");
		// con.sendCmd("LIST");
		// con.chdir("/test/"); // con.send("ALLO", "/test/");
		// con.size("/aniu/test.mtv");
		// int nam = con.download("/tes00t/local-lib.rar",
		// "E:\\test\\local-lib.rar", false);
		int nam = con.upload("f:\\local-lib.rar", "/tes00t/local-lib.rar",
				false);
		con.sendCmd("TRANSTASK test1 -1");
		if (nam == FtpConstants.TRANSFER_STOPPED) {
			System.out.println("file is not exist");
		}
		// con.disconnect();
	}
}
