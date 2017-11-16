package com.sparrow.transfer.target;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channel;

import com.sparrow.transfer.constant.StatusCode;
import com.sparrow.transfer.exceptions.TaskException;
import com.sparrow.transfer.protocol.ProtocolMessage;


/**
 * 
 * An actor target represents a source or a target the actor will operating on.
 * 
 * 代表一个调度器最终操作的源或目标。
 */
public abstract class AbstractTarget {
	/** protocol services */
	protected ProtocolMessage protocolMessage;
	/**
	 * 是否该对象已经被初始化。 If the actor target was initialized.
	 */
	protected boolean initiated = false;
	/** enable resume transport */
	private boolean resume = false;
	/** file source can read */
	protected boolean canRead = false;
	/** file source can write */
	protected boolean canWrite = false;
	/** file exist */
	protected boolean exist = false;
	/** file size setting */
	protected long size = 0l;
	/**
	 * 与该目标相关的迁移器目标 The protocol result associated with this actor target.
	 */
	private String uri = null;
	/**
	 * 偏移量 The offset .
	 */
	private long offset = 0L;

	/**
	 * 构造器 Constructor
	 * 
	 * @param pmsg
	 * @throws TaskException
	 */
	public AbstractTarget(ProtocolMessage pmsg) throws TaskException {
		if (pmsg == null) {
			throw new TaskException(StatusCode.PROTOCOL_UNKOWN,
					"The protocol services is null .. ");
		}
		this.protocolMessage = pmsg;
		if (pmsg != null)
			this.uri = pmsg.getUri();
	}

	public boolean enableResume() {
		return this.resume;
	}

	public void setResume(boolean reum) {
		this.resume = reum;
	}

	public boolean canRead() {
		return this.canRead;
	}

	public boolean canWrite() {
		return this.canWrite;
	}

	public boolean delete() throws TaskException {
		return false;
	}

	public long getSize() {
		return this.size;
	}

	public boolean isExist() {
		return this.exist;
	}

	/**
	 * 
	 * <p>
	 * Description: 是否支持通道
	 * </p>
	 * 
	 * @return
	 * @author Yzc
	 */
	public boolean isSupportChannel() {
		return false;
	}

	public boolean isSupportStream() {
		return true;
	}

	/**
	 * 
	 * <p>
	 * Description: 支持 文件内存映象
	 * </p>
	 * 
	 * @return
	 * @author Yzc
	 */
	public boolean isSupportBufferMap() {
		return false;
	}

	public String getUri() {
		return this.uri;
	}

	public boolean isInitiated() {
		return this.initiated;
	}

	public long getOffset() {
		return this.offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	protected final void checkTarget() throws TaskException {
		if (!this.initiated) {
			throw new TaskException(StatusCode.TARGET_NOT_INIT,
					"Can't initialize target ! ");
		}
		if (!this.exist) {
			throw new TaskException(StatusCode.TARGET_NOT_EXIST,
					"File is not exist -- " + this.uri);
		}
	}

	/**
	 * 
	 * <p>Description: get memory map buffer </p>
	 * @return
	 * @throws TaskException
	 * @author Yzc
	 */
	public MappedByteBuffer getMapBuffer() throws TaskException {
		return null;
	}

	/**
	 * 
	 * <p>
	 * Description: initialize target template
	 * </p>
	 * 
	 * @author Yzc
	 */
	public abstract void initialize() throws TaskException;

	/**
	 * 
	 * <p>
	 * Description: create file ,if file not exist
	 * </p>
	 * 
	 * @return
	 * @throws TaskException
	 * @author Yzc
	 */
	public abstract boolean createDir() throws TaskException;

	/**
	 * 
	 * <p>
	 * Description: release memory
	 * </p>
	 * 
	 * @throws TaskException
	 * @author Yzc
	 */
	public abstract void release() throws TaskException;

	/**
	 * 
	 * <p>
	 * Description: get channel
	 * </p>
	 * 
	 * @return
	 * @throws TaskException
	 * @author Yzc
	 */
	public abstract Channel getChannel() throws TaskException;

	/**
	 * 
	 * <p>
	 * Description: can get stream from offset
	 * </p>
	 * 
	 * @return
	 * @throws TaskException
	 * @author Yzc
	 */
	public abstract InputStream getInputStream() throws TaskException;

	public abstract OutputStream getOutputStream() throws TaskException;
}
