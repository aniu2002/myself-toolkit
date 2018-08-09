package com.sparrow.collect.website.filter;

import com.dili.dd.searcher.basesearch.search.beans.Ranger;

import java.util.List;
import java.util.Map;


/**
 * <B>Description</B>过滤信息 <br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 * 
 * @createTime 2014年6月11日 下午4:21
 * @author zhanglin
 */
public class FilterBean {
  
	/**
	 * 域筛选范围信息(域，范围)
	 */
	private Map<String, List<Ranger>> fieldsRangeInfo=null;

	/**
	 * 搜索类型
	 */
	private String serachId;

	/**
	 * @return the fieldsRangeInfo
	 */
	public Map<String, List<Ranger>> getFieldsRangeInfo() {
		return fieldsRangeInfo;
	}

	/**
	 * @param fieldsRangeInfo
	 *            the fieldsRangeInfo to set
	 */
	public void setFieldsRangeInfo(Map<String, List<Ranger>> fieldsRangeInfo) {
		this.fieldsRangeInfo = fieldsRangeInfo;
	}

	/**
	 * @return the serachId
	 */
	public String getSerachId() {
		return serachId;
	}

	/**
	 * @param serachId
	 *            the serachId to set
	 */
	public void setSerachId(String serachId) {
		this.serachId = serachId;
	}

}
