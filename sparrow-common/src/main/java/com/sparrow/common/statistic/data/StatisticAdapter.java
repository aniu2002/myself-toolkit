package com.sparrow.common.statistic.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 12-12-28
 * Time: 下午1:05
 * To change this template use File | Settings | File Templates.
 */
public class StatisticAdapter {
    private Map<String, Integer> dataSetIdxMap;
    private Map<String, StatisticItemMulti> itemMapping;
    private String dataField;

    public StatisticAdapter(ParaItem[] dataItems, String[] dataSet, int maxSetLen) {
        this.init(dataItems, dataSet, maxSetLen);
    }

    void init(ParaItem[] dataItems, String[] dataSet, int maxSetLen) {
        this.dataSetIdxMap = new HashMap<String, Integer>();
        this.itemMapping = new HashMap<String, StatisticItemMulti>();
        int len = maxSetLen;
        for (int i = 0; i < dataItems.length; i++) {
            itemMapping.put(dataItems[i].field, new StatisticItemMulti(dataItems[i].label, len));
        }

        for (int i = 0; i < len; i++) {
            dataSetIdxMap.put(dataSet[i], i);
        }
    }

    public String getDataField() {
        return dataField;
    }

    public void setDataField(String dataField) {
        this.dataField = dataField;
    }

    public Map<String, StatisticItemMulti> getItemMapping() {
        return itemMapping;
    }

    public void setValue(String itemName, String idxName, int val) {
        StatisticItemMulti item = this.itemMapping.get(itemName);
        if (item != null) {
            Integer idx = this.dataSetIdxMap.get(idxName);
            if (idx != null) {
                item.setValue(idx, val);
            }
        }
    }

}
