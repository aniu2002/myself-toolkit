package com.sparrow.collect.crawler;

import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.conf.pool.PoolFactory;

public abstract class ConcurrentPageCrawler extends AbstractCrawler {

    @Override
    protected void pageExecute(final CrawlerData crawlerData,
                               final SiteEntry siteEntry,
                               final EntryData pEntry) {
        Runnable job = new Runnable() {
            public void run() {
                try {
                    ConcurrentPageCrawler.this.doPageExecute(crawlerData, siteEntry, pEntry);
                } catch (Throwable e) {
                   e.printStackTrace();
                }
            }
        };
        this.getThreadPool().execute(job);
    }


    @Override
    public void destroy() {
        PoolFactory.closeThreadPool(this.getThreadPool());
    }
}
