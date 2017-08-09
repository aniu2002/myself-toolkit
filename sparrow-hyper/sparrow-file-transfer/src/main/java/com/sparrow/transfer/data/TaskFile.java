package com.sparrow.transfer.data;

import java.io.Serializable;

import com.sparrow.transfer.target.AbstractTarget;


/**
 * @author Yzc
 * @version 3.0
 * @date 2009-9-15
 */
public class TaskFile implements Serializable {
	private static final long serialVersionUID = -6768082010943944854L;
	private String fileID;
	private String source;
	private String destination;
	/** fell in this primary key , may not serialize this member of class */
	private transient AbstractTarget sourceTarget;
	private transient AbstractTarget destinationTarget;
	/** 写入的长度 The write length. */
	private long size;
	/** 写入的偏移量 The offset of file. */
	private long offset;
	/** 完成字节 The completed bytes. */
	private long completed;
	private String md5 = "";
	private int index = 0;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getFileID() {
		return fileID;
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public long getCompleted() {
		return completed;
	}

	public void setCompleted(long completed) {
		this.completed = completed;
	}

	public AbstractTarget getSourceTarget() {
		return sourceTarget;
	}

	public void setSourceTarget(AbstractTarget sourceTarget) {
		this.sourceTarget = sourceTarget;
	}

	public AbstractTarget getDestinationTarget() {
		return destinationTarget;
	}

	public void setDestinationTarget(AbstractTarget destinationTarget) {
		this.destinationTarget = destinationTarget;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}
}
