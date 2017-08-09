package com.sparrow.orm.dyna.common;

import com.sparrow.orm.dyna.sql.SqlBuilder;

public class ParamItem {
	String param;
	String operate = SqlBuilder.EQUAL;
	String filter = SqlBuilder.OP_AND;
	/** 参数占位数 */
	int valSigns = 1;
	int index;

	public String getParam() {
		return param;
	}

	public String getOperate() {
		return operate;
	}

	public int getValSigns() {
		return valSigns;
	}

	public String getFilter() {
		return filter;
	}

	public int getIndex() {
		return index;
	}
}
