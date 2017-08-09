package com.sparrow.collect.crawler;

import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.SiteEntry;

public abstract class ConcurrentDetailCrawler extends ConcurrentPageCrawler {

    @Override
    protected void handleDetail(final EntryData itemEntry,
                                final SiteEntry siteEntry,
                                final EntryData pageEntry) {
        Runnable job = new Runnable() {
            public void run() {
                try {
                    ConcurrentDetailCrawler.this.doHandleDetail(itemEntry, siteEntry, pageEntry);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        };
        this.getThreadPool().execute(job);
    }
}
