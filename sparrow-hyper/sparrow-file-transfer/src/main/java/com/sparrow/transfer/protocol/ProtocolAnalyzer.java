package com.sparrow.transfer.protocol;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sparrow.transfer.exceptions.ProtocolAnalyzerException;
import com.sparrow.transfer.utils.StringUtils;


/**
 * 协议会析器，分析一个字符串来确定协议及相关的资源类型。
 * 
 * The protocol type analyzer will analyze a String for ensure the protocol and
 * associated resources types ,addresses specified by the string object.
 * 
 */
public class ProtocolAnalyzer {
	private static final String localPatterns = "^[a-zA-Z]{1}\\:";

	/**
	 * 
	 * <p>
	 * Description: 分析器 analyze
	 * </p>
	 * 
	 * @author Yzc
	 * @throws ProtocolAnalyzerException
	 */
	public static ProtocolMessage analyzeSpecialURI(String uri)
			throws ProtocolAnalyzerException {
		if (StringUtils.isNullOrEmpty(uri))
			throw new ProtocolAnalyzerException("URI is null ");
		if (checkFile(uri)) {
			ProtocolMessage pmsg = new ProtocolMessage();
			pmsg.setProtocol("file");
			pmsg.setUri("file:///" + uri);
			pmsg.setPath(uri);
			return pmsg;
		}
		try {
			URL u = new URL(uri);
			ProtocolMessage pmsg = new ProtocolMessage();
			pmsg.setProtocol(u.getProtocol());
			pmsg.setUri(uri);
			pmsg.setHost(u.getHost());
			pmsg.setPort(u.getPort());
			pmsg.setQuery(u.getQuery());
			pmsg.setPath(u.getPath());

			String userInfo = u.getUserInfo();
			if (userInfo != null) {
				int index = userInfo.indexOf(":");
				if (index != -1) {
					pmsg.setUsername(userInfo.substring(0, index));
					pmsg.setPassword(userInfo.substring(index + 1));
				}
			}
			return pmsg;
		} catch (MalformedURLException e) {
			throw new ProtocolAnalyzerException("Unknow protocol : " + uri);
		}
	}

	/**
	 * 检查是否是本地文件协议。 Check the local.
	 * 
	 * @param s
	 * @return
	 */
	private static boolean checkFile(String s) {
		if (StringUtils.isNullOrEmpty(s))
			return false;
		if (s.charAt(0) == '/') // UNIX
			return true;
		Pattern pattern = Pattern.compile(localPatterns);// WINDOWS
		Matcher m = pattern.matcher(s);
		if (m.find()) {
			return true;
		}
		return false;
	}

	public static void main(String args[]) {
		String url = "d:/localhost/ddd/aa.txt";
		System.out.println(checkFile(url));

		try {
			System.out.println(analyzeSpecialURI(url));
		} catch (ProtocolAnalyzerException e) {
			e.printStackTrace();
		}
	}
}
