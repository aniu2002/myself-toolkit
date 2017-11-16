package com.sparrow.collect.task.site;

import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.SiteEntry;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SiteCrawlerEx extends SiteCrawler {

    private BlockingQueue<EntryData> queue = new LinkedBlockingQueue<EntryData>(2000);
    private volatile boolean stopped = false;

    public SiteCrawlerEx(File rootDir) {
        super(rootDir);
    }

    public void stop() {
        this.stopped = true;
    }

    public void exec(final SiteEntry siteEntry) {
        this.addCrawlSeed(siteEntry);
        super.exec(siteEntry);
    }

    public void execXl(final SiteEntry siteEntry) {
        //this.addCrawlSeed(siteEntry);
        super.exec(siteEntry);
    }

    protected void executeCrawl() {

    }

    protected void executeCrawl(SiteEntry entry) {
        do {
            EntryData entryData = this.getCrawlSeed();
            if (entryData != null)
                this.handleDetail(entryData, entry, entryData);
            else
                break;
        } while (!stopped);
    }

    public void addCrawlSeed(EntryData entry) {
        logger.info(" --- Add crawl seed : {}", entry.getUrl());
        boolean flag = this.queue.offer(entry);
        if (!flag) {
            System.out.println(entry);
            throw new RuntimeException("Can't offer new entry");
        }
    }

    public EntryData getCrawlSeed() {
        BlockingQueue<EntryData> queue = this.queue;
        EntryData entryData = null;
        try {
            entryData = queue.poll(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return entryData;
    }

    public void crawlNextSeed(EntryData entryData) {
        if (this.ignoreEntry(entryData))
            return;
        this.addCrawlSeed(entryData);
    }
}
