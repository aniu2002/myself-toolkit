package com.sparrow.transfer.transfer;

import com.sparrow.transfer.constant.StatusCode;
import com.sparrow.transfer.data.Task;
import com.sparrow.transfer.exceptions.TaskException;
import com.sparrow.transfer.feedback.EmbProgress;

/**
 * @author Yzc
 * @version 3.0
 * @date 2009-8-25
 */
public class TransferBuilder {
	/**
	 * 
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param tsk
	 * @return
	 * @throws TaskException
	 * @author Yzc
	 */
	public static Transfer generatorTransfer(Task tsk) throws TaskException {
		if (tsk == null)
			throw new TaskException(StatusCode.TARGET_NOT_EXIST,
					"Task has null!");
		EmbProgress progress = new EmbProgress(null, tsk);
		// create transfer job
		Transfer transfer = new Transfer(tsk, progress);
		transfer.setTranferId(tsk.getTaskId());
		transfer.setCover(tsk.isCover());
		return transfer;
	}
}
