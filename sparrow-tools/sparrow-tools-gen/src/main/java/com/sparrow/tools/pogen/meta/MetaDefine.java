package com.sparrow.tools.pogen.meta;

import java.util.List;

public class MetaDefine {
	private String table;
    private String app;
	private String cols;
	private List<MetaField> setting;

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getCols() {
		return cols;
	}

	public void setCols(String cols) {
		this.cols = cols;
	}

	public List<MetaField> getSetting() {
		return setting;
	}

	public void setSetting(List<MetaField> setting) {
		this.setting = setting;
	}

	public MetaField findMetaField(String name) {
		for (MetaField m : this.setting) {
			if (name.equalsIgnoreCase(m.getName()))
				return m;
		}
		return null;
	}
}
