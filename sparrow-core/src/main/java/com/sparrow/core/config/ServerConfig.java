/**  
 * Project Name:scms-core  
 * File Name:ServerConfig.java  
 * Package Name:com.boco.scms.core.conf
 * Date:2013-11-26下午2:36:03  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.core.config;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 下面是服务器类型探测的类，原理就是用每个应用服务器自己独特的启动类来判断 <br/>
 * ${app.installed.path} Path where the application is installed <br/>
 * ${was.module.path} Path where the module is installed <br/>
 * ${current.cell.name} Current cell name <br/>
 * ${current.node.name} Current node name <br/>
 * ${current.server.name} Current server name <br/>
 * Attention: Do not use the ${was.module.path} in the ${application} entry. <br/>
 * 
 * @author YZC
 * 
 * @version 1.0 (2013-11-26)
 * @modify
 */
public class ServerConfig {
	private static Logger logger = LoggerFactory.getLogger(ServerConfig.class);
	public static final String JBOSS_CLASS = "/org/jboss/Main.class";
	public static final String JETTY_CLASS = "/org/mortbay/jetty/Server.class";
	public static final String TOMCAT_CLASS = "/org/apache/catalina/startup/Bootstrap.class";
	public static final String WEBLOGIC_CLASS = "/weblogic/Server.class";
	public static final String WEBSPHERE_CLASS = "/com/ibm/websphere/product/VersionInfo.class";

	/** 判断是否是windows操作系统 */
	public static final boolean OS_WINDOWS = System.getProperties()
			.getProperty("os.name").toLowerCase().indexOf("windows") != -1;
	public static final int P_ID;

	static {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		if (logger.isInfoEnabled())
			logger.info(" Pid - Name : " + name);
		String pid = name.split("@")[0];
		int idx = pid.indexOf('[');
		if (idx != -1) {
			pid = pid.substring(idx + 1);
		}
		P_ID = Integer.parseInt(pid);
	}

	private static ServerConfig _instance = new ServerConfig();
	private String _serverName;
	private Boolean _jBoss;
	private Boolean _jetty;
	private Boolean _tomcat;
	private Boolean _webLogic;
	private Boolean _webSphere;

	private ServerConfig() {
	}

	/**
	 * 
	 * 获取当前所在服务器名称，如果是webSphere服务器，则需要获取node节点名称和应用名 <br/>
	 * 可以通过servlet来获取： String str1 = getServletContext().getServerInfo();
	 * 
	 * @return
	 * @author YZC
	 */
	public static String getServerName() {
		ServerConfig sd = _instance;
		if (sd._serverName == null) {
			if (ServerConfig.isJBoss()) {
				sd._serverName = getFullName(System.getProperty(
						"jboss.server.name", "jboss"));
			} else if (ServerConfig.isWebLogic()) {
				// ust tested the following excerpt in WebLogic 11g and worked
				// like a charm
				sd._serverName = getFullName(System.getProperty(
						"weblogic.Name", "weblogic"));
			} else if (ServerConfig.isWebSphere()) {
				sd._serverName = getFullName(getWebSphereServerName());
			} else if (ServerConfig.isJetty()) {
				if (sd._serverName == null) {
					sd._serverName = getFullName("jetty");
				} else
					sd._serverName += "-jetty";
			} else if (ServerConfig.isTomcat()) {
				if (sd._serverName == null)
					sd._serverName = getFullName("tomcat");
				else
					sd._serverName += "-tomcat";
			}
			// application 程序
			if (sd._serverName == null)
				sd._serverName = getLocalName();
		}
		return sd._serverName;
	}

	/**
	 * 
	 * 判断当前服务器是否为JBoss服务器
	 * 
	 * @return
	 * @author YZC
	 */
	public static boolean isJBoss() {
		ServerConfig sd = _instance;
		if (sd._jBoss == null) {
			Class<?> c = sd.getClass();
			if (c.getResource(JBOSS_CLASS) != null)
				sd._jBoss = Boolean.TRUE;
			else
				sd._jBoss = Boolean.FALSE;
		}
		return sd._jBoss.booleanValue();
	}

	/**
	 * 
	 * 判断当前服务器是否为jetty服务器
	 * 
	 * @return
	 * @author YZC
	 */
	public static boolean isJetty() {
		ServerConfig sd = _instance;
		if (sd._jetty == null) {
			Class<?> c = sd.getClass();
			if (c.getResource(JETTY_CLASS) != null)
				sd._jetty = Boolean.TRUE;
			else
				sd._jetty = Boolean.FALSE;
		}
		return sd._jetty.booleanValue();
	}

	/**
	 * 
	 * 判断当前服务器是否为Tomcat
	 * 
	 * @return
	 * @author YZC
	 */
	public static boolean isTomcat() {
		ServerConfig sd = _instance;
		if (sd._tomcat == null) {
			Class<?> c = sd.getClass();
			if (c.getResource(TOMCAT_CLASS) != null)
				sd._tomcat = Boolean.TRUE;
			else
				sd._tomcat = Boolean.FALSE;
		}
		return sd._tomcat.booleanValue();
	}

	/**
	 * 判断当前服务器是否为webLogic
	 * 
	 * @return
	 * @author YZC
	 */
	public static boolean isWebLogic() {
		ServerConfig sd = _instance;
		if (sd._webLogic == null) {
			Class<?> c = sd.getClass();
			if (c.getResource(WEBLOGIC_CLASS) != null)
				sd._webLogic = Boolean.TRUE;
			else
				sd._webLogic = Boolean.FALSE;
		}
		return sd._webLogic.booleanValue();
	}

	/**
	 * 判断当前服务器是否为webSphere
	 * 
	 * @return
	 * @author YZC
	 */
	public static boolean isWebSphere() {
		ServerConfig sd = _instance;
		if (sd._webSphere == null) {
			Class<?> c = sd.getClass();
			if (c.getResource(WEBSPHERE_CLASS) != null)
				sd._webSphere = Boolean.TRUE;
			else
				sd._webSphere = Boolean.FALSE;
		}
		return sd._webSphere.booleanValue();
	}

	/**
	 * 根据websphere的runtime获取当前的serverName信息<br/>
	 * 另一种方式：<br/>
	 * InitialContext ic = new javax.naming.InitialContext(); <br/>
	 * String serverName = ic.lookup("servername").toString(); <br/>
	 * 
	 * @return
	 * @author YZC
	 */
	static String getWebSphereServerName() {
		String serverName;
		try {
			// getDisplayName : server1
			// getFullName : was7host01Node01Cell\was7host01Node01\server1
			Class<?> c = Class.forName("com.ibm.websphere.runtime.ServerName");
			Method m = c.getMethod("getFullName", new Class<?>[0]);
			Object o = m.invoke(ServerConfig.class, new Object[0]);
			serverName = o.toString();
		} catch (Exception ex) {
			serverName = "unknown - " + ex.getClass().getName() + ": "
					+ ex.getMessage();
		}
		return serverName;
	}

	/**
	 * 
	 * 获取本机机器名和IP地址
	 * 
	 * @return
	 * @author YZC
	 */
	static String getLocalName() {
		InetAddress address = getAddress();
		String serverName = "127.0.0.1";
		if (address != null)
			serverName = address.getHostAddress() + "(" + address.getHostName()
					+ ")@" + P_ID;
		return serverName;
	}

	static String getFullName(String container) {
		InetAddress address = getAddress();
		String serverName = "127.0.0.1";
		if (address != null)
			serverName = address.getHostAddress() + "(" + address.getHostName()
					+ ")@" + P_ID + ":" + container;
		return serverName;
	}

	/**
	 * 
	 * 获取网络地址信息
	 * 
	 * @return
	 * @author YZC
	 */
	static InetAddress getAddress() {
		try {
			InetAddress address = InetAddress.getLocalHost();
			return address;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		List<InetAddress> res = new ArrayList<InetAddress>();
		Enumeration<NetworkInterface> netInterfaces;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) netInterfaces
						.nextElement();
				Enumeration<InetAddress> nii = ni.getInetAddresses();
				while (nii.hasMoreElements()) {
					ip = (InetAddress) nii.nextElement();
					if (ip.getHostAddress().indexOf(":") == -1) {
						res.add(ip);
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		if (res.size() > 1)
			return res.get(1);
		else
			return null;
	}
}
