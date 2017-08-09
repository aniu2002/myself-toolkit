package com.sparrow.collect.crawler.data;

/**
 * Created by Administrator on 2016/12/15 0015.
 */
public class EntryWrap {
    private SiteEntry site;
    private EntryData entry;

    public EntryWrap() {
    }

    public EntryWrap(SiteEntry site, EntryData entry) {
        this.site = site;
        this.entry = entry;
    }

    public SiteEntry getSite() {
        return site;
    }

    public void setSite(SiteEntry site) {
        this.site = site;
    }

    public EntryData getEntry() {
        return entry;
    }

    public void setEntry(EntryData entry) {
        this.entry = entry;
    }
}
