package com.sparrow.collect.website.filter;

import com.sparrow.collect.website.query.Ranger;
import org.apache.lucene.search.Filter;

import java.util.List;
import java.util.Map;

/**
 * <B>Description</B>搜素过滤 <br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 * @createTime 2014年6月16日 下午4:51:02
 * @author zhanglin
 */
public interface SearchFilter {

    List<Filter> getFilter(String field, List<Ranger> ranger, Map<String, String> extend);
}
