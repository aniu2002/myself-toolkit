package com.sparrow.transfer.transfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;

import com.sparrow.transfer.constant.StatusCode;
import com.sparrow.transfer.data.Task;
import com.sparrow.transfer.data.TaskFile;
import com.sparrow.transfer.exceptions.TaskException;
import com.sparrow.transfer.target.AbstractTarget;
import com.sparrow.transfer.target.TargetBuilder;
import com.sparrow.transfer.utils.DateUtils;
import com.sparrow.transfer.utils.PathResolver;

public class Transfer implements Runnable {
	/** transfer interval bytes */
	private static final long TRANSFER_INTERVAL = 20 * 1024 * 1024;
	/** description task */
	private Task task;
	private List<TaskFile> files;
	/** transfer progress listener */
	private IProgress progress;
	/** transfer job identify */
	private String tranferId;
	/** the transfer source's URI */
	private String sourceUrl;
	/** the transfer destination's URI */
	private String destinationUrl;
	/** transfer bytes size */
	private long size;
	/** transfer offset for beginning */
	private long offset;
	/** transfer stop flag */
	private boolean isStop = false;
	private int state = 0; // 1-stop,2-drop
	/** if the target file is exist , need cover the file? */
	private boolean cover;

	public boolean isStop() {
		return this.state == 1;
	}

	public Transfer(String sourceUrl, String destinationUrl, IProgress progress)
			throws TaskException {
		this.sourceUrl = sourceUrl;
		this.destinationUrl = destinationUrl;
		this.progress = progress;
		// this.initialize();
	}

	public Transfer(Task tsk, IProgress progress) throws TaskException {
		if (tsk == null)
			throw new TaskException(StatusCode.TARGET_NOT_EXIST,
					"Task has null!");
		this.task = tsk;
		this.files = tsk.getFiles();
		this.progress = progress;
		// this.initialize();
	}

	/**
	 * 
	 * <p>
	 * Description: while the source or destination target has no initialize
	 * </p>
	 * 
	 * @throws TaskException
	 * @author Yzc
	 */
	private void initialize(TaskFile file) throws TaskException {
		AbstractTarget source, destination;
		if (file == null)
			throw new TaskException(StatusCode.TARGET_NOT_EXIST,
					"The file is null!");
		// source URI is directory
		if (PathResolver.isDirectory(file.getSource())) {
			throw new TaskException(StatusCode.TARGET_NOT_EXIST,
					" The source is directory -- " + this.sourceUrl);
		}
		// destination URI is directory
		if (PathResolver.isDirectory(file.getDestination())) {
			throw new TaskException(StatusCode.TARGET_NOT_EXIST,
					" The destination is directory -- " + this.destinationUrl);
		}
		// build source target
		source = TargetBuilder.getTarget(file.getSource());
		// build destination resource
		destination = TargetBuilder.getTarget(file.getDestination());
		// initialize source target
		source.initialize();
		// initialize destination target
		destination.initialize();

		// determine source is exist
		if (!source.isExist()) {
			throw new TaskException(StatusCode.TARGET_NOT_EXIST,
					" The source is not exist -- " + source.getUri());
		}
		long siz = source.getSize();
		// source file's length is 0
		if (siz == 0)
			throw new TaskException(StatusCode.TARGET_NOT_EXIST,
					" The source file length is 0 -- " + source.getUri());
		file.setSize(siz);
		this.size += siz;
		// judge destination is exist
		if (!destination.isExist()) {
			// create it directory
			destination.createDir();
			// boolean flag = destination.createDir();
			// if (!flag)
			// throw new TaskException(StatusCode.FILE_CREATE_ERROR, "文件不能创建");
		} else if (!this.cover) {
			long ofst = destination.getSize();
			if (ofst > 0) {
				source.setOffset(ofst);
				destination.setOffset(ofst);
				this.offset += ofst;
				file.setOffset(offset);
			}
		}
		file.setSourceTarget(source);
		file.setDestinationTarget(destination);
	}

	private void initialize() throws TaskException {
		if (this.files == null)
			throw new TaskException(StatusCode.TARGET_NOT_EXIST,
					"Task has no files to transfer!");
		try {
			for (int i = 0; i < this.files.size(); i++) {
				TaskFile file = this.files.get(i);
				this.initialize(file);
			}
		} catch (TaskException e) {
			if (this.progress != null) {
				this.task.setState(-1);
				this.task.setDesc(e.getErrorMessage());
				this.task.setStartTime(DateUtils.currentTime(null));
			}
			throw e;
		}
	}

	@Override
	public void run() {
		try {
			this.initialize();
			if (this.progress != null) {
				this.progress.started(this.size, this.offset);
			}
			for (int i = 0; i < this.files.size(); i++) {
				TaskFile file = this.files.get(i);
				AbstractTarget source, destination;
				source = file.getSourceTarget();
				destination = file.getDestinationTarget();
				if (source.isSupportChannel() && destination.isSupportChannel()) {
					WritableByteChannel writeChannel = (WritableByteChannel) destination
							.getChannel();
					ReadableByteChannel readChannel = (ReadableByteChannel) source
							.getChannel();
					this.doChannelTransfer(readChannel, writeChannel);
				} else {
					this.doStreamChannelTransfer(source.getInputStream(),
							destination.getOutputStream());
				}
				source.release();
				destination.release();
				if (this.isStop) {
					break;
				}
			}
			if (this.state == 1) {
				if (this.progress != null) {
					this.progress.stopped();
				}
			} else if (this.state == 2) {
				if (this.progress != null)
					this.progress.dropTask();
			} else {
				if (this.progress != null) {
					this.progress.finished();
				}
			}
		} catch (TaskException e) {
			if (this.progress != null) {
				this.progress.failure(e.getCode(), e.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * <p>
	 * Description: do transfer with block channel
	 * </p>
	 * 
	 * @param srcChannel
	 * @param tarChannel
	 * @throws TaskException
	 * @author Yzc
	 */
	private void doChannelTransfer(ReadableByteChannel srcChannel,
			WritableByteChannel tarChannel) throws TaskException {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8192); // allocate
		try {
			int writeBytes = -1;
			long transBytes = 0;
			while (!this.isStop && srcChannel.read(byteBuffer) != -1) {
				byteBuffer.flip();
				writeBytes = tarChannel.write(byteBuffer);
				byteBuffer.clear();
				transBytes += writeBytes;
				// byteBuffer.compact();
				if (transBytes > TRANSFER_INTERVAL) {
					if (this.progress != null) {
						this.progress.progress(transBytes);
					}
					transBytes = 0;
				}
			}
			srcChannel.close();
			tarChannel.close();
		} catch (IOException e) {
			throw new TaskException(StatusCode.TRANSFER_ERROR, "迁移错误:"
					+ e.getMessage());
		}
	}

	/**
	 * 
	 * <p>
	 * Description: wrapper stream to channel
	 * </p>
	 * 
	 * @param ins
	 * @param out
	 * @throws TaskException
	 * @author Yzc
	 */
	private void doStreamChannelTransfer(InputStream ins, OutputStream out)
			throws TaskException {
		ReadableByteChannel sourceChannel = Channels.newChannel(ins);
		WritableByteChannel targetChannel = Channels.newChannel(out);
		this.doChannelTransfer(sourceChannel, targetChannel);
	}

	public String getTranferId() {
		return tranferId;
	}

	public void setTranferId(String tranferId) {
		this.tranferId = tranferId;
	}

	public void stop() {
		this.isStop = true;
		this.state = 1;
	}

	public void drop() {
		this.isStop = true;
		if (this.state == 1) {
			if (this.progress != null)
				this.progress.dropTask();
		}
		this.state = 2;
	}

	public boolean isCover() {
		return cover;
	}

	public void setCover(boolean cover) {
		this.cover = cover;
	}

	public void reset() {
		this.isStop = false;
		this.state = 0;
	}

	public boolean isDrop() {
		return this.state == 2;
	}
}
