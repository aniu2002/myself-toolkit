package com.sparrow.common.statistic.data;

import org.joda.time.DateTime;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 12-12-19
 * Time: 下午1:49
 * To change this template use File | Settings | File Templates.
 */
public class StatisticParaVo {
    private String date;
    private DateTime start;
    private DateTime end;
    private String[] categories;
    private int maxSetLen = 0;

    public int getMaxSetLen() {
        return maxSetLen;
    }

    public void setMaxSetLen(int maxSetLen) {
        this.maxSetLen = maxSetLen;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public DateTime getStart() {
        return start;
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    public DateTime getEnd() {
        return end;
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }
}
