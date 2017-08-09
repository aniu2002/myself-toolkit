package au.core.net.ftp.basic;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class FtpUtils {
	private static int porta = 5; // 5 * 256 + 1 = 1281
	private static int portb = 1;

	public static int getPasvPort(String str) {
		int start = str.lastIndexOf(",");
		String lo = "";
		start++;
		while (start < str.length()) {
			char c = str.charAt(start);
			if (!Character.isDigit(c)) {
				break;
			}
			lo = lo + c;
			start++;
		}
		String hi = "";
		start = str.lastIndexOf(",");
		start--;
		while (true) {
			char c = str.charAt(start);

			if (!Character.isDigit(c)) {
				break;
			}
			hi = c + hi;
			start--;
		}
		return ((Integer.parseInt(hi) * 256) + Integer.parseInt(lo));
	}

	/**
	 * 
	 * <p>
	 * Description: set FTP client active port command
	 * </p>
	 * 
	 * @param ipaddr
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 * @author Yzc
	 */
	public static String getActivePortCmd(InetAddress ipaddr)
			throws UnknownHostException, IOException {
		String ip = ipaddr.getHostAddress().replace('.', ',');

		ServerSocket aSock = new ServerSocket(0);
		int availPort = aSock.getLocalPort();
		aSock.close();
		System.out.println(" [Active Port: " + availPort);
		porta = availPort / 256;
		portb = availPort % 256;
		String ret = "PORT " + ip + "," + porta + "," + portb;
		return ret;
	}

	/**
	 * 
	 * <p>
	 * Description: get client socket port open to FTP server
	 * </p>
	 * 
	 * @return
	 * @author Yzc
	 */
	public static int getActivePort() {
		return (porta * 256) + portb;
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @see 解析连接文件
	 */
	public static String parseSymlinkBack(String file) {
		if (file == null)
			return null;
		if (file.indexOf("->") >= 0) {
			file = file.substring(file.indexOf("->") + 2).trim();
		}
		return file;
	}

	/**
	 * 
	 * <p>
	 * Description: check the path is server path and build it
	 * </p>
	 * 
	 * @param tmp
	 * @return
	 * @author Yzc
	 */
	public static String checkPath(String tmp) {
		if (tmp == null) {
			return null;
		}
		String x1 = tmp;
		if (x1.indexOf("\"") >= 0) {
			x1 = tmp.substring(tmp.indexOf("\"") + 1);
			x1 = x1.substring(0, x1.indexOf("\""));
		}
		if (!x1.endsWith("/")) {
			x1 = x1 + "/";
		}
		return x1;
	}

	/**
	 * 
	 * <p>
	 * Description: according to user input , analyze the string
	 * </p>
	 * 
	 * @param p
	 * @param pwd
	 * @return
	 * @author Yzc
	 */
	public static String genPwdPath(String p, String pwd) {
		if (!p.startsWith("/") && !p.startsWith("~")) {
			p = pwd + p;
		}
		if (p.endsWith("..")) {
			boolean home = p.startsWith("~");
			StringTokenizer stok = new StringTokenizer(p, "/");
			if (stok.countTokens() > 2) {
				String pold1 = "";
				String pold2 = "";
				String pnew = "";
				while (stok.hasMoreTokens()) {
					pold1 = pold2;
					pold2 = pnew;
					pnew = pnew + "/" + stok.nextToken();
				}
				p = pold1; // 获取 cd .. 的上一级目录
			} else {
				p = "/";
			}
			if (home)
				p = p.substring(1);
		}
		return p;
	}

	/*
	 * Returns true if the string represents a relative filename, false
	 * otherwise
	 */
	public static boolean isRelative(String file) {
		// unix
		if (file.startsWith("/")) {
			return false;
		}
		// windows
		if ((file.length() > 2) && (file.charAt(1) == ':')) {
			return false;
		}
		return true;
	}
	public static boolean isFtpServerPath(String file) {
		// unix
		if (file.startsWith("/")) {
			return true;
		}
		// windows
		if ((file.length() > 2) && (file.charAt(1) == ':')) {
			return false;
		}
		return true;
	}
	/**
	 * Get a filename out of a full path string
	 */
	public static String getFileName(String file) {
		int x = file.lastIndexOf("/");
		// unix
		if (x >= 0) {
			file = file.substring(x + 1);
			return file;
		}
		// windows
		x = file.lastIndexOf("\\");
		if (x >= 0) {
			return file.substring(x + 1);
		}
		return file;
	}

	/**
	 * Returns a string representing a relative directory path. Examples:
	 * "/tmp/dir/" -> "dir/" and "/tmp/dir" -> "dir"
	 */
	public static String getPath(String file) {
		int x = file.lastIndexOf("/");
		// unix
		if (x >= 0) {
			file = file.substring(0, x);
			return file;
		}
		// windows
		x = file.lastIndexOf("\\");
		if (x >= 0) {
			file = file.substring(0, x);
			return file;
		} else
			return "";
	}

	/**
	 * Returns a string representing a relative directory path. Examples:
	 * "/tmp/dir/" -> "dir/" and "/tmp/dir" -> "dir"
	 */
	public static String getLastPath(String file) {
		String tmp;
		if (file == null || file.equals(""))
			return "";
		tmp = getSamplePath(file, '/');
		if (tmp == null)
			tmp = getSamplePath(file, '\\');
		return tmp == null ? file : tmp;
	}

	private static String getSamplePath(String file, char splitor) {
		String srcPath = file;
		int x = srcPath.lastIndexOf(splitor);
		boolean hasSplitor = false;
		if (x == srcPath.length() - 1) {
			hasSplitor = true;
			srcPath = srcPath.substring(0, x);
		}
		x = srcPath.lastIndexOf(splitor);
		if (x >= 0) {
			srcPath = srcPath.substring(x + 1);
			if (hasSplitor) {
				srcPath += splitor;
				return srcPath;
			}
		}
		return null;
	}

	public static void main(String args[]) {
		System.out.println(getLastPath("tmpd\\"));
	}
}
