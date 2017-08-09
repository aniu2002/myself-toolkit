package com.sparrow.transfer.target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;

import com.sparrow.transfer.constant.StatusCode;
import com.sparrow.transfer.exceptions.TaskException;
import com.sparrow.transfer.protocol.ProtocolMessage;
import com.sparrow.transfer.utils.PathResolver;


public class FileTarget extends AbstractTarget {
	private RandomAccessFile randomfile = null;
	private File file = null;
	private FileChannel fileChanel;
	private InputStream ins;
	private OutputStream os;

	public FileTarget(ProtocolMessage pmsg) throws TaskException {
		super(pmsg);
	}

	/**
	 * 
	 * <p>
	 * Description: check the random file has initialized
	 * </p>
	 * 
	 * @throws ActorTargetException
	 * @author Yzc
	 */
	private void checkRandomFile() throws TaskException {
		try {
			if (this.randomfile != null)
				return;
			this.randomfile = new RandomAccessFile(this.file,
					getMode(this.file));
			if (this.getOffset() > 0) {
				this.randomfile.seek(getOffset());
			}
		} catch (Exception e) {
			throw new TaskException(e);
		}
	}

	@Override
	public boolean createDir() throws TaskException {
		if (!this.initiated) {
			throw new TaskException(StatusCode.TARGET_NOT_INIT,
					"Can't initialize target ! ");
		}
		if (!this.file.exists()) {
			String dirPath = PathResolver.getFileDir(this.protocolMessage
					.getPath());
			File tmp = new File(dirPath);
			tmp.mkdirs();
		}
		return true;
	}

	public boolean isSupportChannel() {
		return true;
	}

	public boolean isSupportBufferMap() {
		return false;
	}

	public MappedByteBuffer getMapBuffer() throws TaskException {
		this.checkTarget();
		try {
			if (this.randomfile == null) {
				this.randomfile = new RandomAccessFile(this.file,
						getMode(this.file));
			}
			if (fileChanel == null)
				fileChanel = ((RandomAccessFile) this.randomfile).getChannel();
			if (fileChanel != null) {
				long pos = this.getOffset();
				if (pos < 0)
					pos = 0;
				return fileChanel.map(FileChannel.MapMode.READ_ONLY, pos, size);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new TaskException(StatusCode.FILE_MAPPING_ERROR,
					"Can't create file mapping buffer with the file channel!");
		}
		return null;
	}

	@Override
	public Channel getChannel() throws TaskException {
		// this.checkTarget();
		checkRandomFile();
		try {
			fileChanel = ((RandomAccessFile) this.randomfile).getChannel();
			return fileChanel;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InputStream getInputStream() throws TaskException {
		this.checkTarget();
		try {
			this.ins = new FileInputStream(this.file);
			if (this.getOffset() > 0) {
				this.ins.skip(getOffset());
			}
			return this.ins;
		} catch (Exception e) {
			throw new TaskException(e);
		}

	}

	@Override
	public OutputStream getOutputStream() throws TaskException {
		// checkTarget();
		if (!this.exist)
			this.createDir();
		try {
			this.os = new FileOutputStream(this.file);
			return this.os;
		} catch (Exception e) {
			throw new TaskException(e);
		}
	}

	@Override
	public void initialize() throws TaskException {
		try {
			this.file = new File(this.protocolMessage.getPath());
		} catch (Exception e) {
			this.initiated = false;
			return;
		}
		this.initiated = true;
		this.canRead = this.file.canRead();
		this.canWrite = this.file.canWrite();
		this.exist = this.file.exists();
		if (this.exist) {
			this.size = this.file.length();
		}
	}

	@Override
	public void release() throws TaskException {
		try {
			if (this.fileChanel != null && this.fileChanel.isOpen()) {
				this.fileChanel.close();
				this.randomfile.close();
			}
			if (this.ins != null)
				this.ins.close();
			if (this.os != null)
				this.os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 返回可用的文件访问模式 Returns the file access mode.
	 * 
	 * @param file
	 * @return
	 */
	private String getMode(File file) {
		if (!file.exists()) {
			return "rw";
		}
		String s = new String();
		if (file.canRead()) {
			s += "r";
		}
		if (file.canWrite()) {
			s += "w";
		}
		return s;
	}

	public String toString() {
		return "File target .. ";
	}
}
