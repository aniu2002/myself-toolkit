package com.sparrow.common.statistic.data;

import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 12-12-19 Time: 下午1:49 To change
 * this template use File | Settings | File Templates.
 */
public class StatisticExtData {
	private String caption;
	private String xdLabel;
	private String ydLabel;

	private String[] categories;

	private Map<String, StatisticItemMulti> dataSet;

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getXdLabel() {
		return xdLabel;
	}

	public void setXdLabel(String xdLabel) {
		this.xdLabel = xdLabel;
	}

	public String getYdLabel() {
		return ydLabel;
	}

	public void setYdLabel(String ydLabel) {
		this.ydLabel = ydLabel;
	}

	public String[] getCategories() {
		return categories;
	}

	public void setCategories(String[] categories) {
		this.categories = categories;
	}

	public Map<String, StatisticItemMulti> getDataSet() {
		return dataSet;
	}

	public void setDataSet(Map<String, StatisticItemMulti> dataSet) {
		this.dataSet = dataSet;
	}
}
