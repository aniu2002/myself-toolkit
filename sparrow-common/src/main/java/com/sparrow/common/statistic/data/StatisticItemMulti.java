package com.sparrow.common.statistic.data;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 12-12-28
 * Time: 下午1:05
 * To change this template use File | Settings | File Templates.
 */
public class StatisticItemMulti {
    private String label;
    private int[] values;

    public StatisticItemMulti(String label, int length) {
        this.label = label;
        this.values = new int[length];
    }

    public int[] getValues() {
        return values;
    }

    public void setValues(int[] values) {
        this.values = values;
    }

    public String getLabel() {
        return label;
    }

    public void setValue(int idx, int val) {
        this.values[idx] = val;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
