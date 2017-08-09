package com.sparrow.orm.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TableConfig {
	private String claz;
	private String name;
	private String beanName;
	private String comment;
	private List<TableItem> items = new ArrayList<TableItem>();
	private List<TableItem> keys = new ArrayList<TableItem>();
	private Map<String, TableItem> itemMap;
	private String columns[];
	private String alias[];
	private boolean initialize = false;

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getClaz() {
		return claz;
	}

	public void setClaz(String claz) {
		this.claz = claz;
	}

	public String getName() {
		return name;
	}

	public String[] getColumns() {
		if (!this.initialize)
			this.initializeColumns();
		return columns;
	}

	public String[] getAlias() {
		if (!this.initialize)
			this.initializeColumns();
		return alias;
	}

	public String getColumn(String alia) {
		TableItem item;
		Collection<TableItem> itms = this.getAllItems();
		if (itms != null && !itms.isEmpty()) {
			Iterator<TableItem> iterator = itms.iterator();
			while (iterator.hasNext()) {
				item = iterator.next();
				if (alia.equals(item.getProperty())
						|| alia.equals(item.getAlia()))
					return item.getColumn();
			}
		}
		return alia;
	}

	public void setName(String name) {
		this.name = name;
	}

	private void initializeColumns() {
		if (this.initialize)
			return;
		TableItem item;
		Collection<TableItem> itms = this.getAllItems();
		int siz = itms.size();
		this.alias = new String[siz];
		this.columns = new String[siz];
		if (itms != null && !itms.isEmpty()) {
			Iterator<TableItem> iterator = itms.iterator();
			int i = 0;
			while (iterator.hasNext()) {
				item = iterator.next();
				this.columns[i] = item.getColumn();
				this.alias[i] = item.getAlia();
				i++;
			}
		}
		this.initialize = true;
	}

	public Map<String, TableItem> getItemMap() {
		if (this.itemMap == null) {
			this.itemMap = new HashMap<String, TableItem>();
			Iterator<TableItem> ite;
			TableItem titem;
			if (this.keys != null && !this.keys.isEmpty()) {
				ite = this.keys.iterator();
				while (ite.hasNext()) {
					titem = ite.next();
					this.itemMap.put(titem.getColumn().toLowerCase(), titem);
				}
			}
			if (this.items != null && !this.items.isEmpty()) {
				ite = this.items.iterator();
				while (ite.hasNext()) {
					titem = ite.next();
					this.itemMap.put(titem.getColumn().toLowerCase(), titem);
				}
			}
		}
		return itemMap;
	}

	public Collection<TableItem> getAllItems() {
		if (this.itemMap == null) {
			this.getItemMap();
		}
		return this.itemMap.values();
	}

	public void addItem(TableItem item) {
		item.setKey(false);
		this.items.add(item);
	}

	public List<TableItem> getItems() {
		return items;
	}

	public List<TableItem> getKeys() {
		return keys;
	}

	public void addKey(TableItem item) {
		item.setKey(true);
		this.keys.add(item);
	}

	public String getSelectSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("select ");
		TableItem item;
		Collection<TableItem> itms = this.getAllItems();
		if (itms != null && !itms.isEmpty()) {
			Iterator<TableItem> iterator = itms.iterator();
			boolean needSign = false;
			while (iterator.hasNext()) {
				item = iterator.next();
				if (needSign)
					sb.append(",");
				else
					needSign = true;
				sb.append("a.").append(item.getColumn());
			}
		}

		sb.append(" from ").append(this.getName()).append(" a");
		return sb.toString();
	}

	public boolean hasKey() {
		return !this.keys.isEmpty();
	}

	public String getUpdateSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("update ").append(this.getName()).append(" set ");
		TableItem item;
		for (int i = 0; i < this.items.size(); i++) {
			item = this.items.get(i);
			if (i != 0)
				sb.append(",");
			sb.append(item.getColumn()).append("=?");
		}

		if (!this.keys.isEmpty()) {
			sb.append(" where ");
			for (int i = 0; i < this.keys.size(); i++) {
				item = this.keys.get(i);
				if (i != 0)
					sb.append(" and ");
				sb.append(item.getColumn()).append("=?");
			}
		}
		return sb.toString();
	}

	public String getDeleteSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("delete from ").append(this.getName()).append(" where ");
		if (!this.keys.isEmpty()) {
			TableItem item;
			for (int i = 0; i < this.keys.size(); i++) {
				item = this.keys.get(i);
				if (i != 0)
					sb.append(" and ");
				sb.append(item.getColumn()).append("=?");
			}
		} else {
			Collection<TableItem> itms = this.getAllItems();
			TableItem item;
			if (itms != null && !itms.isEmpty()) {
				Iterator<TableItem> iterator = itms.iterator();
				boolean needSign = false;
				while (iterator.hasNext()) {
					item = iterator.next();
					if (needSign)
						sb.append(" and ");
					else
						needSign = true;
					sb.append(item.getColumn()).append("=?");
				}
			}
		}
		return sb.toString();
	}

	Object lock = new Object();
	String insertSql = null;

	public String getInsertSql() {
		if (this.insertSql == null) {
			synchronized (lock) {
				if (this.insertSql == null) {
					StringBuffer sb = new StringBuffer();
					sb.append("insert into ").append(this.getName())
							.append("(");
					Collection<TableItem> itms = this.getAllItems();
					TableItem item;
					if (itms != null && !itms.isEmpty()) {
						Iterator<TableItem> iterator = itms.iterator();
						boolean needSign = false;
						while (iterator.hasNext()) {
							item = iterator.next();
							if (needSign)
								sb.append(",");
							else
								needSign = true;
							sb.append(item.getColumn());
						}
					}
					sb.append(") values(");
					for (int i = 0; i < itms.size(); i++) {
						if (i != 0) {
							sb.append(",");
						}
						sb.append("?");
					}
					sb.append(")");
					this.insertSql = sb.toString();
				}
			}
		}

		return this.insertSql;
	}

	public String getSelectSqlWhere() {
		StringBuffer sb = new StringBuffer();
		sb.append("select ");
		TableItem item;
		Collection<TableItem> itms = this.getAllItems();
		if (itms != null && !itms.isEmpty()) {
			Iterator<TableItem> iterator = itms.iterator();
			boolean needSign = false;
			while (iterator.hasNext()) {
				item = iterator.next();
				if (needSign)
					sb.append(",");
				else
					needSign = true;
				sb.append(item.getColumn());
			}
		}

		sb.append(" from ").append(this.getName());
		if (!this.keys.isEmpty()) {
			sb.append(" where ");
			for (int i = 0; i < this.keys.size(); i++) {
				item = this.keys.get(i);
				if (i != 0)
					sb.append(" and ");
				sb.append(item.getColumn()).append("=?");
			}
		}
		return sb.toString();
	}

	
	public String toString() {
		return " -- " + this.claz + " -> " + this.name;
	}
}
