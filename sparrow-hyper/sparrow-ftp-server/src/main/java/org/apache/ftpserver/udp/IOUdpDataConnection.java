package org.apache.ftpserver.udp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;

import org.apache.ftpserver.ftplet.DataConnection;
import org.apache.ftpserver.ftplet.FtpSession;

public class IOUdpDataConnection implements DataConnection {
	protected DatagramSocket dataConnectSocket;

	public void setDatagramSocket(DatagramSocket dataConnectSocket) {
		this.dataConnectSocket = dataConnectSocket;
	}

	public IOUdpDataConnection() {
	}

	public final long transferFromClient(FtpSession session,
			final OutputStream out) throws IOException {
		return 0;
	}

	public final long transferToClient(FtpSession session, final InputStream in)
			throws IOException {
		return 0;
	}

	public final void transferToClient(FtpSession session, final String str)
			throws IOException {
	}

}
