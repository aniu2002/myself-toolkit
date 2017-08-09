package com.sparrow.common.statistic.data;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 12-12-28
 * Time: 下午1:30
 * To change this template use File | Settings | File Templates.
 */
public class ParaItem {
    String label;
    String field;

    public ParaItem(String field, String label) {
        this.field = field;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
