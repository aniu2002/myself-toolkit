package au.core.net.ftp.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import au.core.net.ftp.conf.Settings;

public class SocketConnection implements Runnable {
	private int timeout = Settings.TIME_OUT;
	private String host;
	private int port;
	private PrintWriter writer;
	private BufferedReader reader;
	private Socket s;
	private boolean isOk = false;
	private boolean doit = false;
	private Thread runner;
	private int localPort = -1;

	/**
	 * 
	 * @param host
	 * @param port
	 *  构函数,host和prot并启动一个socket连接线程,在控制端口上建立控制命令连接
	 */
	public SocketConnection(String host, int port) {
		this.host = host;
		this.port = port;

		runner = new Thread(this);
		runner.start();
	}

	public SocketConnection(String host, int port, int time) {
		this.host = host;
		this.port = port;
		this.timeout = time;
		runner = new Thread(this);
		runner.start();
	}

	public void run() {
		try {
			s = new Socket(host, port);
			/**
			 * 获取socket本地端口
			 */
			localPort = s.getLocalPort();
			writer = new PrintWriter(
					new OutputStreamWriter(s.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(
					s.getInputStream()), Settings.BUFFER_SIZE);
			isOk = true;
		} catch (Exception ex) {
			System.out.println("\n FTP主机连接错误(" + host + ":" + port + ")");
			isOk = false;
			try {
				if ((s != null) && !s.isClosed()) {
					s.close();
				}
				if (writer != null) {
					writer.close();
				}
				if (reader != null) {
					reader.close();
				}
			} catch (Exception ex2) {
				System.out.println("关闭套节字错");
			}
		}
		doit = true;
	}

	public void useUtf8Wrapper() {
		try {
			writer = null;
			reader = null;
			writer = new PrintWriter(new OutputStreamWriter(
					s.getOutputStream(), "UTF-8"));
			reader = new BufferedReader(new InputStreamReader(
					s.getInputStream(), "UTF-8"), Settings.BUFFER_SIZE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 *  是否已经连接,是否等待连接超时
	 */
	public boolean isThere() {
		int cnt = 0;

		while (!doit && (cnt < timeout)) {
			pause(100);
			cnt = cnt + 100;
		}
		return this.isOk;
	}

	/**
	 * 
	 * @param data
	 *  ftp命令发 接口,清空本地套接字缓冲区,全部发?
	 */
	protected void send(String data) {
		try {
			// out.print
			writer.println(data);
			writer.flush();
			if (data.startsWith("PASS")) {
				System.out.println("-PASS ****");
				// Log.debug("> PASS ****");
			} else {
				System.out.println("-" + data);
				// Log.debug("> " + data);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void close() {
		if (this.doit)
			try {
				this.reader.close();
				this.writer.close();
				this.s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	/**
	 * 
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author Yzc
	 * @return
	 * @throws IOException
	 */
	protected String readEchoLine() throws IOException {
		if (this.reader != null) {
			String tmp = this.reader.readLine();
			System.out.println("  " + tmp);
			return tmp;
		}
		return null;
	}

	/**
	 * 
	 * @return
	 *  获取socket的本地端
	 */
	public int getLocalPort() {
		return localPort;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 *  获取socket服务器地
	 */
	public InetAddress getLocalAddress() throws IOException {
		return s.getLocalAddress();
	}

	/**
	 * 
	 * @param time
	 *  等待计时
	 */
	private void pause(int time) {
		try {
			Thread.sleep(time);
		} catch (Exception ex) {
		}
	}

	/**
	 * 
	 * @return
	 *  获取socket输入reader
	 */
	public BufferedReader getIn() {
		return reader;
	}

	/**
	 * 
	 * @return
	 *  设置socket输入reader
	 */
	public void setReader(BufferedReader in) {
		this.reader = in;
	}

	/**
	 * 
	 * @return
	 *  获取soket输出
	 */
	public PrintWriter getOut() {
		return writer;
	}

	public boolean isClosed() {
		if (this.s == null)
			return true;
		return this.s.isClosed();
	}
}