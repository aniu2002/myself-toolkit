package com.sparrow.common.statistic.data;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 12-12-28
 * Time: 下午1:05
 * To change this template use File | Settings | File Templates.
 */
public class StatisticItem {
    private String label;
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
