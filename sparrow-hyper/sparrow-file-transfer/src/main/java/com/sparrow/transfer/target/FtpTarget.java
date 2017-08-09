package com.sparrow.transfer.target;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channel;

import com.sparrow.transfer.constant.StatusCode;
import com.sparrow.transfer.exceptions.TaskException;
import com.sparrow.transfer.protocol.ProtocolMessage;
import com.sparrow.transfer.utils.StringUtils;

public class FtpTarget extends AbstractTarget {
	private String username;
	private String password;
	private String host;
	private int port;
	private String path;

	public FtpTarget(ProtocolMessage pmsg) throws TaskException {
		super(pmsg);
		this.username = pmsg.getUsername();
		this.password = pmsg.getPassword();
		this.host = pmsg.getHost();
		this.path = pmsg.getPath();
		this.port = pmsg.getPort();
	}

	private boolean checkExist() {
		return false;
	}

	@Override
	public boolean createDir() throws TaskException {
		// String ftpPath = PathResolver.getFileDir(this.path);
		return true;
	}

	@Override
	public Channel getChannel() throws TaskException {
		return null;
	}

	@Override
	public InputStream getInputStream() throws TaskException {
		this.checkTarget();
		try {
			setBinary();
			if (getOffset() > 0) {
			}
			return null;
		} catch (IOException e) {
			throw new TaskException(e);
		}
	}

	@Override
	public OutputStream getOutputStream() throws TaskException {
		try {
			setBinary();
			if (this.getOffset() > 0) {
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			throw new TaskException(e);
		}
	}

	@Override
	public boolean canRead() {
		return false;
	}

	@Override
	public boolean canWrite() {
		return false;
	}

	/**
	 * 设二进制模式
	 * 
	 * @throws IOException
	 */
	private void setBinary() throws IOException {
	}

	@Override
	public void initialize() throws TaskException {

		if (StringUtils.isNullOrEmpty(this.host)) {
			throw new TaskException(StatusCode.HOST_EMPTY,
					"The host can not be null!");
		}
		if (StringUtils.isNullOrEmpty(this.path)) {
			throw new TaskException(StatusCode.PATH_EMPTY,
					"The path can not be null!");
		}
		if (StringUtils.isNullOrEmpty(this.username)) {
			this.username = "ANONYMOUS";
			this.password = "hbyw618@hotmail.com";
		}
		if (this.port < 1) {
			this.port = 21;
		}

		this.initiated = true;
		this.canRead = true;
		this.canWrite = true;
		this.exist = this.checkExist();
	}

	@Override
	public void release() throws TaskException {
		if (true) {
			try {
				this.initiated = false;
			} catch (Exception e) {
				throw new TaskException(e);
			}
		}
	}

	public String toString() {
		return "Ftp target .. ";
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
