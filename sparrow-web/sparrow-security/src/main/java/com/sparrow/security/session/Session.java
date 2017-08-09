package com.sparrow.security.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * 
 */
public class Session implements Serializable {
	private static final long serialVersionUID = 7904505837545213624L;
	String id;
	String role;
	String permissions;
	Map<String, Object> attrs;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

	public void put(String key, Object value) {
		if (this.attrs == null)
			this.attrs = new HashMap<String, Object>();
		this.attrs.put(key, value);
	}

	public Object get(String key) {
		if (this.attrs != null)
			return this.attrs.get(key);
		return null;
	}
}
