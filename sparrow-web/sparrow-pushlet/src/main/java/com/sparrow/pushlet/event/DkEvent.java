package com.sparrow.pushlet.event;

import com.sparrow.core.json.JsonMapper;
import com.sparrow.pushlet.Protocol;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-3-18 Time: 下午3:03 To change this
 * template use File | Settings | File Templates.
 */
public class DkEvent implements Event {
	private Map<String, String> attrs;
	private String type;
	private String jsonStr;
	private volatile boolean isDirty = true;
	private Object data;

	public DkEvent(String type) {
		this.type = type;
		this.setField("event", type);
	}

	public DkEvent(Map<String, String> attrs) {
		if (attrs != null)
			this.type = attrs.get(Protocol.P_EVENT);
		this.attrs = attrs;
	}

	public DkEvent(String type, Map<String, String> attrs) {
		this.type = type;
		this.attrs = attrs;
		this.setField("event", type);
	}

	private DkEvent(String type, Map<String, String> attrs, Object data) {
		this.type = type;
		this.attrs = attrs;
		this.data = data;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		this.setField("event", type);
	}

	public void setField(String key, String value) {
		if (this.attrs == null) {
			this.attrs = new ConcurrentHashMap<String, String>();
		}
		if (key == null || value == null)
			return;
		this.attrs.put(key, value);
		this.isDirty = true;
	}

	public String getField(String key) {
		return this.attrs == null ? null : this.attrs.get(key);
	}

	public void clear() {
		this.attrs.clear();
		this.isDirty = true;
	}

	public Event clone() {
		return new DkEvent(this.type, this.attrs, this.data);
	}

	public Iterator<String> getFieldNames() {
		return this.attrs.keySet().iterator();
	}

	@Override
	public String toString() {
		String queryString = "";
		String amp = "";
		for (Iterator<String> iter = getFieldNames(); iter.hasNext();) {
			String nextAttrName = (String) iter.next();
			String nextAttrValue = getField(nextAttrName);
			queryString = queryString + amp + nextAttrName + "="
					+ nextAttrValue;
			amp = "&";
		}
		return queryString;
	}

	public void writeTo(StringBuilder sb) {
		Iterator<Map.Entry<String, String>> iterator = this.attrs.entrySet()
				.iterator();
		boolean fg = false;
		sb.append("{");
		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = iterator.next();
			if (fg)
				sb.append(",");
			else
				fg = true;
			sb.append("\"").append(entry.getKey()).append("\":\"")
					.append(entry.getValue()).append("\"");
		}
		if (this.data != null) {
			if (fg)
				sb.append(",");
			sb.append("\"data\":");
			this.writeToBuf(this.data, sb);
		}
		sb.append("}");

	}

	protected void writeToBuf(Object data, StringBuilder sb) {
		Object st = data;
		boolean isObj = false;
		if (!(data instanceof String)) {
			try {
				st = JsonMapper.mapper.writeValueAsString(data);
				isObj = true;
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (isObj)
			sb.append(st);
		else
			sb.append("\"").append(st).append("\"");
	}

	public String toJsonString() {
		if (StringUtils.equals(this.type, Protocol.E_DATA)) {
			if (this.data != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("{\"").append(Protocol.P_EVENT).append("\":\"")
						.append(Protocol.E_DATA).append("\",\"data\":");
				this.writeToBuf(this.data, sb);
				sb.append("}");
				return sb.toString();
			}
		}
		if (this.isDirty) {
			StringBuilder sb = new StringBuilder();
			Iterator<Map.Entry<String, String>> iterator = this.attrs
					.entrySet().iterator();
			boolean fg = false;
			// sb.append("{").append(Protocol.P_EVENT).append(":\"").append(this.type).append("\",data:");
			sb.append("{");
			while (iterator.hasNext()) {
				Map.Entry<String, String> entry = iterator.next();
				if (fg)
					sb.append(",");
				else
					fg = true;
				sb.append("\"").append(entry.getKey()).append("\":\"")
						.append(entry.getValue()).append("\"");
			}
			sb.append("}");
			// sb.append("}");
			this.jsonStr = sb.toString();
			this.isDirty = false;
		}
		return this.jsonStr;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
