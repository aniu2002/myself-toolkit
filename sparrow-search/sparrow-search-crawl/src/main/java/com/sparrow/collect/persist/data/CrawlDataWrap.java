package com.sparrow.collect.persist.data;

import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.SiteEntry;

/**
 * Created by Administrator on 2017/7/29 0029.
 */
public class CrawlDataWrap {
    private CrawlerData detailData;
    private SiteEntry siteEntry;
    private EntryData entryData;

    public CrawlDataWrap() {

    }

    public CrawlDataWrap(CrawlerData detailData) {
        this.detailData = detailData;
    }

    public CrawlDataWrap(CrawlerData detailData, SiteEntry siteEntry) {
        this.detailData = detailData;
        this.siteEntry = siteEntry;
    }

    public CrawlDataWrap(CrawlerData detailData, EntryData entryData) {
        this.detailData = detailData;
        this.entryData = entryData;
    }

    public CrawlDataWrap(CrawlerData detailData, SiteEntry siteEntry, EntryData entryData) {
        this.detailData = detailData;
        this.siteEntry = siteEntry;
        this.entryData = entryData;
    }

    public CrawlerData getDetailData() {
        return detailData;
    }

    public void setDetailData(CrawlerData detailData) {
        this.detailData = detailData;
    }

    public SiteEntry getSiteEntry() {
        return siteEntry;
    }

    public void setSiteEntry(SiteEntry siteEntry) {
        this.siteEntry = siteEntry;
    }

    public EntryData getEntryData() {
        return entryData;
    }

    public void setEntryData(EntryData entryData) {
        this.entryData = entryData;
    }
}
