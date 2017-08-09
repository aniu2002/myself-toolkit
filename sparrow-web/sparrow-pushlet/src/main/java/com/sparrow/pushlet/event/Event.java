package com.sparrow.pushlet.event;

/**
 * 
 * 定义事件节点信息
 * 
 * @author YZC
 * @version 1.0 (2014-5-13)
 * @modify
 */
public interface Event {

	public String getType();

	public void setField(String key, String value);

	public String getField(String key);

	public void clear();

	public String toJsonString();

	public Object getData();

	public void setData(Object data);

	public void setType(String type);

	public Event clone();

	public void writeTo(StringBuilder sb);
}
