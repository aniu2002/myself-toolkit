package com.sparrow.collect.lucene.data;

import java.util.Date;

public class IndexVo {
	private String itemid;

	private String title;

	// File index
	private String path;
	// file index
	private int pageNo;
	private String description;

	private Date createTime;

	private String author;

	private String ctime;

	private Integer typeId;

	private String typeName;

	private String content;
	// 索引类型 1是数据库 2是文件
	private String itype;

	public String getItype() {
		return itype;
	}

	public void setItype(String itype) {
		this.itype = itype;
	}

	public IndexVo() {

	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public IndexVo(String itemid, String title, String author, Integer typeId,
			String typeName, String content, Date createTime) {
		this.itemid = itemid;
		this.title = title;
		this.author = author;
		this.typeId = typeId;
		this.typeName = typeName;
		this.content = content;
		this.createTime = createTime;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
