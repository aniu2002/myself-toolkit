package com.sparrow.orm.id;

import java.net.InetAddress;

/**
 * uuid生成规则
 * 
 * @author YK
 */
public abstract class AbstractUUIDGenerator implements IdentifierGenerator {
	// ip地址
	private static final int IP;
	// 获取当前地址
	static {
		int ipadd;
		try {
			ipadd = toInt(InetAddress.getLocalHost().getAddress());
		} catch (Exception e) {
			ipadd = 0;
		}
		IP = ipadd;
	}

	/**
	 * byte转换成int
	 * 
	 * @return result int
	 */
	public static int toInt(byte[] bytes) {
		int result = 0;
		for (int i = 0; i < 4; i++) {
			result = (result << 8) - Byte.MIN_VALUE + (int) bytes[i];
		}
		return result;
	}

	// 计数器
	private static short counter = (short) 0;
	// jvm 虚拟机
	private static final int JVM = (int) (System.currentTimeMillis() >>> 8);

	public AbstractUUIDGenerator() {
	}

	/**
	 * JVM的启动时间（精确到1/4秒）
	 * 
	 * @return JVM
	 */
	protected int getJVM() {
		return JVM;
	}

	/**
	 * 计数器值（在JVM中唯一）
	 * 
	 * @return counter
	 */
	protected short getCount() {
		synchronized (AbstractUUIDGenerator.class) {
			if (counter < 0)
				counter = 0;
			return counter++;
		}
	}

	/**
	 * IP地址
	 * 
	 * @return IP
	 */
	protected int getIP() {
		return IP;
	}

	/**
	 * 系统时间
	 * 
	 * @return 当前时间 short
	 */
	protected short getHiTime() {
		return (short) (System.currentTimeMillis() >>> 32);
	}

	/**
	 * 系统时间
	 * 
	 * @return 当前时间 int
	 */
	protected int getLoTime() {
		return (int) System.currentTimeMillis();
	}
}
