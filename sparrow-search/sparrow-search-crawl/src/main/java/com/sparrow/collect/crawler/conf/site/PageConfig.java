package com.sparrow.collect.crawler.conf.site;

import com.sparrow.collect.crawler.conf.AbstractConfigured;

/**
 * Created by Administrator on 2016/12/6.
 */
public class PageConfig extends AbstractConfigured {
    // 判断分页已经结束的标签表达式（css selector）
    private String endExpress;
    // 与主站siteUrl拼接的分页url表达式，${page}替换成pageNo
    private String entryExpress = "${page}.html";
    // 是否在no前补零
    private boolean fillZero = false;
    // pageNo的占位数，根据情况补多少个0,小于10补一个0
    private int placeholders = 2;
    // page抓取起始页
    private int start = 0;
    // page抓取的结束页，默认-1，65535为上限
    private int end = -1;
    // 是否构建page页的 忽略掉第一页，有些站点存在  第一页不带分页参数
    private boolean ignoreFirst;

    public boolean isIgnoreFirst() {
        return ignoreFirst;
    }

    public void setIgnoreFirst(boolean ignoreFirst) {
        this.ignoreFirst = ignoreFirst;
    }

    public String getEndExpress() {
        return endExpress;
    }

    public void setEndExpress(String endExpress) {
        this.endExpress = endExpress;
    }

    public String getEntryExpress() {
        return entryExpress;
    }

    public void setEntryExpress(String entryExpress) {
        this.entryExpress = entryExpress;
    }

    public boolean isFillZero() {
        return fillZero;
    }

    public void setFillZero(boolean fillZero) {
        this.fillZero = fillZero;
    }

    public int getPlaceholders() {
        return placeholders;
    }

    public void setPlaceholders(int placeholders) {
        this.placeholders = placeholders;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
