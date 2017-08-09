package com.sparrow.transfer.transfer;

import com.sparrow.transfer.data.Task;

public interface IProgress {
	/**
	 * 
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param tid
	 * @param complete
	 * @param total
	 * @param offlen
	 * @param offsecond
	 * @author Yzc
	 */
	public void progress(long transfered);

	/**
	 * 
	 * <p>
	 * Description: started
	 * </p>
	 * 
	 * @param tid
	 * @param size
	 * @param time
	 * @author Yzc
	 */
	public void started(long size, long offset);

	public void finished();

	public void failure(String code, String why);

	/**
	 * 
	 * <p>
	 * Description: task stopped
	 * </p>
	 * 
	 * @author Yzc
	 */
	public void stopped();

	public void dropTask();
	
	public Task getTask();

}
