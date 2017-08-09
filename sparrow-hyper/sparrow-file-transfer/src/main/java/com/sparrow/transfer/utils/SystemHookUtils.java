package com.sparrow.transfer.utils;

public class SystemHookUtils {
	public void bindSystemHook() {
		Thread hook = new ShutdownHook(); // 增加一个系统进程的钩子,当进程被kill时,会调用该hook处理善后操作
		try {
			Runtime.getRuntime().addShutdownHook(hook);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class ShutdownHook extends Thread {

		public ShutdownHook() {
		}

		public void run() {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
