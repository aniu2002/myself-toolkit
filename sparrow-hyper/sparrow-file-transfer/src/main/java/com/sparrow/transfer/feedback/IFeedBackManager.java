package com.sparrow.transfer.feedback;

import com.sparrow.transfer.data.StatusInfo;
import com.sparrow.transfer.data.Task;

public interface IFeedBackManager {
	/**
	 * 
	 * <p>
	 * Description: task finished ,will activate task's status feed back
	 * </p>
	 * 
	 * @param tid
	 * @param status
	 * @param speed
	 * @author Yzc
	 */
	public void finishFeedback(String tid, int status, float speed,
			float seconds);

	/**
	 * 
	 * <p>
	 * Description: task error
	 * </p>
	 * 
	 * @param tid
	 * @param code
	 * @param errMsg
	 * @author Yzc
	 */
	public void errorFeedback(String tid, String code, String errMsg);

	/**
	 * 
	 * <p>
	 * Description: task progress feed back
	 * </p>
	 * 
	 * @param info
	 * @author Yzc
	 */
	public void progressFeedback(StatusInfo info);

	/**
	 * 
	 * <p>
	 * Description: a task started
	 * </p>
	 * 
	 * @param tid
	 * @author Yzc
	 */
	public void startTask(Task task);

	/**
	 * 
	 * <p>
	 * Description: a task started
	 * </p>
	 * 
	 * @param tid
	 * @author Yzc
	 */
	public void dropTask(Task task);

	/**
	 * 
	 * <p>
	 * Description: notice progress feed back by timer
	 * </p>
	 * 
	 * @author Yzc
	 */
	public void timerNotice();

	/**
	 * 
	 * <p>
	 * Description: start the feed back manager
	 * </p>
	 * 
	 * @author Yzc
	 */
	public void start();

	/**
	 * 
	 * <p>
	 * Description: stop the feed back manager
	 * </p>
	 * 
	 * @author Yzc
	 */
	public void stop();

	/**
	 * 
	 * <p>
	 * Description: stop the feed back manager
	 * </p>
	 * 
	 * @author Yzc
	 */
	public void taskStop(String tid);
	
	public void addListener(TaskListener listener);
}
