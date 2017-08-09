package com.sparrow.transfer.feedback;

import com.sparrow.transfer.data.StatusInfo;
import com.sparrow.transfer.data.Task;

public interface TaskListener {
	/**
	 * @see 刷新所有任务
	 */
	// public void refresh();
	/**
	 * 
	 * @param tasks
	 * @see 初始化的任务信息
	 */
	public void initTasks(Task[] tasks);

	/**
	 * 
	 * @param taskId
	 * @see 开始执行taskId的任务
	 */
	public void beginTask(Task task);

	/**
	 * 
	 * @param taskId
	 * @see 开始执行taskId的任务
	 */
	public void waitTask(Task task);

	/**
	 * 
	 * @param taskId
	 * @see 停止taskId的任务,更新UI信息
	 */
	public void taskStopped(String taskId);

	/**
	 * 
	 * @param taskId
	 * @see 通知监听器,任务被删除,通知UI
	 */
	public void deleteTask(String taskId);

	/**
	 * 
	 * @param taskId
	 * @see 通知监听器,任务被删除,通知UI
	 */
	public void errorTask(String taskId, String msg);

	/**
	 * 
	 * @param taskId
	 * @see 通知监听器,任务被删除,通知UI
	 */
	public void finishTask(String taskId, float seconds, float speed);

	/**
	 * 
	 * @param taskId
	 * @see 通知监听器,任务被删除,通知UI
	 */
	public void progress(String taskId, int percent);

	/**
	 * 
	 * @param progressInfos
	 * @see 通知监听器,任务的进度
	 */
	public void progressUpdate(StatusInfo info);
}
