package com.sparrow.weixin.message.category;

import java.util.List;

public class TopCategory {

	private String id;
	private String icon;
	private String parentId;
	private String name;
	private List<SecondCategory> childList;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<SecondCategory> getChildList() {
		return childList;
	}
	public void setChildList(List<SecondCategory> childList) {
		this.childList = childList;
	}
	
	
}
