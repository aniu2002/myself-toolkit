package com.sparrow.collect.lucene.data;

import java.io.File;

import com.sparrow.collect.utils.DateUtil;
import com.sparrow.collect.utils.PathResolver;


public class FileIndexItem {
	private String title;
	private String path;
	private String createDate;
	private int itype = 2; // file type
	private int pageNo = 1;
	private String content;
	private String author;
	private String typeName;
	private int fileType;

	public String getTypeName() {
		return typeName;
	}

	public int getFileType() {
		return fileType;
	}

	public FileIndexItem(File file) {
		this.path = file.getAbsolutePath();
		this.title = PathResolver.trimExtension(file.getName());
		this.typeName = PathResolver.getExtension(file.getName());
		this.fileType = FileType.EXTENSION_TYPE.get(this.typeName) == null ? 0
				: FileType.EXTENSION_TYPE.get(this.typeName);
		this.createDate = DateUtil.longToDateToString(file.lastModified());
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public int getItype() {
		return itype;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
