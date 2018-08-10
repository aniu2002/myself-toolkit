package com.sparrow.collect.website.filter;

import com.sparrow.collect.website.Configs;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.Filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <B>Description</B>filter工厂<br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 *
 * @author zhanglin
 * @createTime 2014年6月11日 下午4:21
 */
public class FilterFactory {

    private Log log = LogFactory.getLog(FilterFactory.class);

    private static FilterFactory instance = new FilterFactory();
    // (searchid+fieldName,filter)
    private Map<String, SearchFilter> fieldFilterInfos = new ConcurrentHashMap<>();

    public static FilterFactory getInstance() {
        return instance;
    }

    public void init() throws Exception {
        try {
            fieldFilterInfos = new ConcurrentHashMap<>();
            Map<String, String> filterNameInfos = parseAndGetFilterNameInfos();
            for (String identify : filterNameInfos.keySet()) {
                String className = filterNameInfos.get(identify);
                Class c = Class.forName(className);
                SearchFilter filter = (SearchFilter) c.newInstance();
                fieldFilterInfos.put(identify, filter);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            log.fatal(e);
            throw e;
        }
    }

    private Map<String, String> parseAndGetFilterNameInfos() {

        Map<String, String> filterNameInfos = new HashMap<String, String>();
        String[] searchIds = Configs.get("searcher.filter.searchId.list").split(",");
        if (null != searchIds && searchIds.length > 0) {

            for (String searchId : searchIds) {
                String keyStr = String.format("searcher.filter.%s.field.list", searchId);
                String[] fields = Configs.get(keyStr).split(",");
                if (null != fields && fields.length > 0) {
                    for (String field : fields) {
                        String filterNameKeyStr = String.format("searcher.filter.%s.%s.filter", searchId, field);
                        String filterName = Configs.get(filterNameKeyStr);
                        if (null == filterName) {
                            // use default
                            filterNameKeyStr = String.format("searcher.filter.%s.default.filter", searchId, field);
                            filterName = Configs.get(filterNameKeyStr);
                        }
                        filterNameInfos.put(searchId + field, filterName);
                    }
                }
            }
        }
        return filterNameInfos;
    }

    public List<Filter> getFilter(String field, FilterBean filterBean) throws Exception {
        String searchId = filterBean.getSerachId();
        SearchFilter searchFilter = fieldFilterInfos.get(searchId + field);
        if (searchFilter != null)
            return searchFilter.getFilter(field, filterBean.getFieldsRangeInfo().get(field), null);
        else
            return null;
    }


}
