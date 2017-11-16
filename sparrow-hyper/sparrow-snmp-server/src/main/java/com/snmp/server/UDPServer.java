package com.snmp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 
 * <p>
 * Title: UDPServer
 * </p>
 * <p>
 * Description: com.snmp.server
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Sobey
 * </p>
 * 
 * @author Yzc
 * @version 3.0
 * @date 2009-8-10
 */
public class UDPServer implements Runnable {
	/** buffer size */
	public static int BUFFER_SIZE = 512;
	/** UDP server socket */
	private DatagramSocket server = null;
	/** thread reference */
	private Thread serverThread;
	/** thread pool provider */
	private ExecutorService executorService = null;
	/** services notice interface */
	private ProgressNotice notice;
	/** define thread pool size */
	private int maxRunning = 10;
	/** is initialize */
	private boolean isInitialized = false;
	/** define UDP port */
	private int port = 162;
	private volatile int counts = 0;
	private boolean isStop = false;

	public UDPServer() {

	}

	/**
	 * 
	 * <p>
	 * Description: plus 1
	 * </p>
	 * 
	 * @author Yzc
	 */
	public void plus() {
		this.counts++;
	}

	/**
	 * 
	 * <p>
	 * Description: minus 1
	 * </p>
	 * 
	 * @author Yzc
	 */
	public void minus() {
		this.counts--;
	}

	public UDPServer(int port) {
		this.port = port;
	}

	public void initialize() {
		System.out.println(" - Start the SNMP server ... on port(" + this.port
				+ ")");
		try {
			this.server = new DatagramSocket(this.port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.executorService = Executors.newFixedThreadPool(this.maxRunning);
		this.isInitialized = true;
	}

	public void run() {
		if (!this.isInitialized) {
			return;
		}
		while (!this.isStop) {
			DatagramPacket inPacket = new DatagramPacket(new byte[BUFFER_SIZE],
					BUFFER_SIZE);
			try {
				server.receive(inPacket);
				if (this.counts > 10)
					continue;
				// accept a UDP DatagramPacket,and plus one
				this.plus();
				byte[] encodedMessage = inPacket.getData();
				ProgressJob job = new ProgressJob(this, this.notice,
						encodedMessage);
				this.executorService.submit(job);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.clear();
	}

	/**
	 * 
	 * <p>
	 * Description: clear memory
	 * </p>
	 * 
	 * @author Yzc
	 */
	private void clear() {
		server.close();
		// 线程池不再接收新的任务，但是会继续执行完工作队列中现有的任务
		this.executorService.shutdown();
		// 等待关闭线程池，每次等待的超时时间为30秒
		while (!executorService.isTerminated()) {
			try {
				executorService.awaitTermination(30, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		server = null;
		executorService = null;
	}

	public void stopServer() {
		System.out.println(" - Stop the SNMP server ... ");
		this.isStop = true;
		InetAddress hostAddress;
		try {
			hostAddress = InetAddress.getByName("127.0.0.1");
			DatagramPacket outPacket = new DatagramPacket("z".getBytes(), 1,
					hostAddress, this.port);
			DatagramSocket dSocket = new DatagramSocket();
			dSocket.setSoTimeout(15000);
			dSocket.send(outPacket);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// this.serverThread.interrupt();
	}

	public void listen() {
		System.out.println(" - The SNMP server begin listen ... ");
		this.serverThread = new Thread(this);
		this.serverThread.start();
	}

	public static void main(String args[]) {
		UDPServer server = new UDPServer();
		TestMessageNotice notice = new TestMessageNotice();
		server.setNotice(notice);
		server.initialize();
		server.listen();
	}

	public ProgressNotice getNotice() {
		return notice;
	}

	public void setNotice(ProgressNotice notice) {
		this.notice = notice;
	}
}
