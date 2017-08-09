package com.sparrow.orm.shema.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
	private String primaryKeys;
	private String name;
	private String objName;
	private String desc;
	private String pakage;
	private String mapperPakage;
	private List<TableColumn> items;
	private Collection<String> imports;

	public Table() {

	}

	public Table(String name) {
		this.name = name;
	}

	public String getMapperPakage() {
		return mapperPakage;
	}

	public void setMapperPakage(String mapperPakage) {
		this.mapperPakage = mapperPakage;
	}

	public String getPakage() {
		return pakage;
	}

	public void setPakage(String pakage) {
		this.pakage = pakage;
	}

	public String getObjName() {
		return objName;
	}

	public void setObjName(String objName) {
		this.objName = objName;
	}

	public String getPrimaryKeys() {
		return primaryKeys;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setPrimaryKeys(String primaryKeys) {
		this.primaryKeys = primaryKeys;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<TableColumn> getItems() {
		return items;
	}

	public void setItems(List<TableColumn> items) {
		this.items = items;
	}

	public void addItem(TableColumn item) {
		if (this.items == null)
			this.items = new ArrayList<TableColumn>();
		this.items.add(item);

	}

	public void caculate() {
		if (this.items != null) {
			Map<String, String> c = new HashMap<String, String>();
			for (TableColumn tc : this.items)
				c.put(tc.getJavaType(), tc.getJavaType());
			this.imports = c.values();
		}
	}

	public Collection<String> getImports() {
		return imports;
	}

	public void setImports(Collection<String> imports) {
		this.imports = imports;
	}

	public void removeItem(TableColumn item) {
		if (this.items != null)
			this.items.remove(item);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\r\n  table  : \"").append(this.name).append("\",\r\n")
				.append("  object : \"").append(this.objName).append("\",\r\n")
				.append("  desc   : \"").append(this.desc).append("\",\r\n")
				.append("  primaryKeys : \"").append(this.primaryKeys).append(
						"\",\r\n").append("  items:[ \r\n");
		boolean isf = true;
		for (TableColumn tc : this.items) {
			if (!isf) {
				sb.append(",\r\n");
			} else
				isf = false;
			sb.append("    ").append(tc);
		}
		sb.append("\r\n  ]\r\n}");
		return sb.toString();
	}

}
