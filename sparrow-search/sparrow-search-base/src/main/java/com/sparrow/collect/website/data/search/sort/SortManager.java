package com.sparrow.collect.website.data.search.sort;

import com.sparrow.collect.website.BeansFactory;
import com.sparrow.collect.website.Configs;
import com.sparrow.collect.website.cache.StoreSalesCache;
import com.sparrow.collect.website.data.search.SearchBean;
import com.sparrow.collect.website.score.ExternalDataComparatorSource;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <B>Description</B> 排序工厂 <br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 *
 * @author zhanglin
 * @createTime 2014年6月12日 下午5:58:15
 */
public class SortManager {

    private static SortManager instance = new SortManager();

    // <searchId+fieldName,SortField.Type>
    private Map<String, SortField.Type> sortTypeInfs = new ConcurrentHashMap<String, SortField.Type>();

    private Map<String, FieldComparatorSource> fieldComparatorSources = new ConcurrentHashMap<>();

    public static SortManager getInstance() {
        return instance;
    }


    private static String GOODS_SALES = "GOODS_SALES";
    private static String PNR_STORE_SALES = "PNR_STORE_SALES";
    private static String PLC_GOODS_SALES = "PLC_GOODS_SALES";

    // searchbaean,isthrowException,configname,
    public void init() throws Exception {

        Map<String, String> fieldsSortFieldTypeValue = getFieldsSortFieldTypeValue();
        for (String fieldsSortFieldTypeValueKey : fieldsSortFieldTypeValue.keySet()) {
            String enumValue = fieldsSortFieldTypeValue.get(fieldsSortFieldTypeValueKey);
            if (GOODS_SALES.equals(enumValue)) {
                StoreSalesCache sc = BeansFactory.getInstance().getStoreSalesCache();
                ExternalDataComparatorSource source = new ExternalDataComparatorSource("id", sc, new ExternalDataComparatorSource.KeyTypeCast<String, Long>() {
                    @Override
                    public Long cast(String s) {
                        if (NumberUtils.isNumber(s)) {
                            return Long.valueOf(s);
                        } else {
                            return null;
                        }
                    }
                });
                fieldComparatorSources.put(fieldsSortFieldTypeValueKey, source);
            } else if (PNR_STORE_SALES.equals(enumValue)) {
                StoreSalesCache storeSalesCache = BeansFactory.getInstance().getStoreSalesCache();
                ExternalDataComparatorSource source = new ExternalDataComparatorSource("id", storeSalesCache, new ExternalDataComparatorSource.KeyTypeCast<String, Long>() {
                    @Override
                    public Long cast(String s) {
                        if (NumberUtils.isNumber(s)) {
                            return Long.valueOf(s);
                        } else {
                            return null;
                        }
                    }
                });
                fieldComparatorSources.put(fieldsSortFieldTypeValueKey, source);
            } else if (PLC_GOODS_SALES.equals(enumValue)) {
                StoreSalesCache salesCache = BeansFactory.getInstance().getStoreSalesCache();
                ExternalDataComparatorSource source = new ExternalDataComparatorSource("pid", salesCache, new ExternalDataComparatorSource.KeyTypeCast<String, Long>() {
                    @Override
                    public Long cast(String s) {
                        if (NumberUtils.isNumber(s)) {
                            return Long.valueOf(s);
                        } else {
                            return null;
                        }
                    }
                });
                fieldComparatorSources.put(fieldsSortFieldTypeValueKey, source);
            } else {
                sortTypeInfs.put(fieldsSortFieldTypeValueKey, Enum.valueOf(SortField.Type.class, enumValue));
            }
        }
    }

    private Map<String, String> getFieldsSortFieldTypeValue() {

        Map<String, String> ret = new HashMap<>();
        String[] searchIds = Configs.get("searcher.basesearch.sort.searchId.list").split(",");

        if (null != searchIds) {
            for (String searchId : searchIds) {
                String[] fields = Configs.get(String.format("searcher.basesearch.sort.%s.field.list", searchId)).split(",");
                if (null != fields) {
                    for (String fieldName : fields) {
                        String keyStr = searchId + fieldName;
                        String value = Configs.get(String.format("searcher.basesearch.sort.%s.%s", searchId, fieldName));
                        ret.put(keyStr, value);
                    }
                }
            }
        }
        return ret;
    }

    /*
     * this method is 获取 排序信息
     * @param searchBean
     * @return
     * @createTime 2014年6月12日 下午6:06:45
     * @author zhanglin
     */
    public Sort getSort(SearchBean searchBean, String searchId) throws Exception {

        Sort sort = new Sort();
        List<SortField> sortFields = new ArrayList<>();
        List<SortBean> sortBeans = searchBean.getSortBeans();
        if (null == sortBeans)
            return sort;
        for (SortBean sortBean : sortBeans) {
            String sortFieldName = sortBean.getSortField();
            String key = searchId + sortFieldName;
            SortField.Type type = sortTypeInfs.get(key);
            if (type != null) {
                Boolean reverseInf = sortBean.getSortReverse();
                SortField sortField = new SortField(sortFieldName, type, reverseInf);
                sortFields.add(sortField);
            } else {
                FieldComparatorSource source = fieldComparatorSources.get(key);
                if (source != null) {
                    Boolean reverseInf = sortBean.getSortReverse();
                    SortField sortField = new SortField(sortFieldName, source, reverseInf);
                    sortFields.add(sortField);
                }
            }
        }
        if (null != sortFields && sortFields.size() > 0) {
            SortField[] sortFieldArray = new SortField[sortFields.size()];
            sort.setSort(sortFields.toArray(sortFieldArray));
        }
        return sort;
    }

}
