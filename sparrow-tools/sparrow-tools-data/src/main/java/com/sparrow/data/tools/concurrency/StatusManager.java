package com.sparrow.data.tools.concurrency;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * 状态管理器，控制导入或者导出访问的计数器，避免导入导出的时候过多的资源竞争<br/>
 * 1)mark标记name标识一种场景，比如：import 那么最多同时有20个处理job，超过则抛出异常 <br/>
 * 2)unmark取消标记，当处理完导入job时应该取消标记，释放计数器<br/>
 * 3)hasName是否已经存在了这种导入或者导出场景，比如：条码模板导出，或许只需要一个导出
 * 
 * @author YZC
 * @version 1.0 (2014-3-27)
 * @modify
 */
public class StatusManager {
	/** 每个场景最大标记次数 */
	static final int PER_MAX_SIZE = 20;
	/** 处理job的最大限制数 */
	static final int TOTAL_SIZE = 100;
	final Map<String, HStatus> statusCache = new ConcurrentHashMap<String, HStatus>();
	final Object signal = new Object();
	final Object unSignal = new Object();
	private int maxSize = TOTAL_SIZE;
	private int importHandles = PER_MAX_SIZE;
	private int totals;

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public int getImportHandles() {
		return importHandles;
	}

	public void setImportHandles(int importHandles) {
		this.importHandles = importHandles;
	}

	/**
	 * 
	 * 对于可能处于资源竞争的场景，标记某种某种应用场景的计数器，如果超过计数则抛出系统异常。等待释放后方可以进入标记
	 * 
	 * @param name
	 *            场景名称标识，比如：import 或者 export
	 * @author YZC
	 */
	public final void mark(String name) {
		synchronized (signal) {
			HStatus status = this.statusCache.get(name);
			if (status == null) {
				status = new HStatus(this.importHandles);
				status.name = name;
				this.statusCache.put(name, status);
			}
			if (this.totals >= this.maxSize)
				throw new RuntimeException("所有操作超过最大(" + this.maxSize + ")限制");
			else if (status.hasMaxed())
				throw new RuntimeException("【" + name + "】超过最大("
						+ this.importHandles + ")限制");
			this.totals++;
			status.plus();
		}
	}

	/**
	 * 
	 * 对于可能处于资源竞争的场景，退出该场景后，刻意取消之前的标记。以便让其他的请求进入
	 * 
	 * @param name
	 *            场景名称标识，比如：import 或者 export
	 * @author YZC
	 */
	public final void unmark(String name) {
		synchronized (unSignal) {
			HStatus status = this.statusCache.get(name);
			if (status != null) {
				status.minus();
				if (status.isZero()) {
					this.statusCache.remove(name);
					status = null;
				}
				this.totals--;
			}
		}
	}

	/**
	 * 
	 * 是否已经存在了这种导入或者导出场景，比如：条码模板导出，或许只需要一个导出
	 * 
	 * @param name
	 *            场景名称标识，比如：import 或者 export
	 * @return 存在与否
	 * @author YZC
	 */
	public final boolean hasName(String name) {
		return this.statusCache.containsKey(name);
	}

	/**
	 * 
	 * 判断是否还能接受新的处理job
	 * 
	 * @param name
	 *            场景名称标识 ，比如：import 或者 export
	 * @return 存在与否
	 * @author YZC
	 */
	public final boolean canAccept(String name) {
		if (this.totals >= this.maxSize)
			return false;
		HStatus status = this.statusCache.get(name);
		if (status == null)
			return true;
		else if (status.hasMaxed())
			return false;
		return true;
	}
}

/**
 * 
 * 记录各种场景的操作状态计数信息
 * 
 * @author YZC
 * @version 1.0 (2014-3-27)
 * @modify
 */
class HStatus {
	final int max;
	String name;
	volatile int counter;

	HStatus(int max) {
		this.max = max;
	}

	void plus() {
		this.counter = this.counter + 1;
	}

	void minus() {
		this.counter = this.counter - 1;
	}

	boolean hasMaxed() {
		return this.counter >= this.max;
	}

	boolean isZero() {
		return this.counter == 0;
	}
}