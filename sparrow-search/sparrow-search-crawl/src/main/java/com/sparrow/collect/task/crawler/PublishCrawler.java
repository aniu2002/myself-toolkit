package com.sparrow.collect.task.crawler;


import com.sparrow.collect.crawler.ConcurrentDetailCrawler;
import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.httpclient.HttpResp;
import com.sparrow.collect.crawler.selector.IPageSelector;
import com.sparrow.collect.crawler.conf.pool.PoolFactory;
import com.sparrow.collect.crawler.httpclient.CrawlHttp;
import com.sparrow.collect.crawler.httpclient.CrawlKit;
import com.sparrow.collect.utils.FileIOUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.concurrent.ThreadPoolExecutor;

public class PublishCrawler extends ConcurrentDetailCrawler {
    final CrawlKit kit = CrawlKit.KIT;
    final File rootDir;
    protected IPageSelector selector;

    public PublishCrawler(File rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    public ThreadPoolExecutor getThreadPool() {
        return PoolFactory.getDefault();
    }

    @Override
    public IPageSelector getSelector() {
        return this.selector;
    }

    public void setSelector(IPageSelector selector) {
        this.selector = selector;
    }

    @Override
    protected CrawlerData doCrawlData(EntryData entry) {
        HttpResp resp = this.kit.getHtml(entry.getUrl(), null,
                CrawlHttp.headers, "GBK", false, 3);
        CrawlerData data = new CrawlerData();
        data.setHtml(resp.getHtml());
        data.setTitle(entry.getTitle());
        data.setUrl(entry.getUrl());
        return data;
    }

    public static void main(String args[]) {
        CrawlKit kit = CrawlKit.KIT;

        HttpResp resp = kit.getHtml("http://www.cnbeta.com/more.htm?"
                        + "jsoncallback=dd&type=all&page=28&_t=" + System.currentTimeMillis(), null, CrawlHttp.headers,
                "GBK", false, 3);
        System.out.println(resp.getHtml());
    }

    @Override
    protected EntryData generatePageEntry(SiteEntry siteEntry, int num) {
        String url = siteEntry.getUrl();
        EntryData entryData = new EntryData();
        String suffix = num > 9 ? String.valueOf(num) : "0"
                + String.valueOf(num);
        entryData.setTitle(siteEntry.getTitle() + '-' + suffix);
        entryData.setUrl(url + suffix + ".html");
        return entryData;
    }

    @Override
    protected void handleCrawlerData(CrawlerData crawlerData,
                                     SiteEntry siteEntry,
                                     EntryData pageEntry) {
        FileIOUtil.writeFile(
                new File(this.rootDir, pageEntry.getTitle()
                        + File.separatorChar
                        + this.correctTitle(crawlerData.getTitle()) + ".html"),
                crawlerData.getHtml(), "GBK"
        );
    }

    String correctTitle(String title) {
        if (StringUtils.isNotEmpty(title))
            return title.replace('/', '$').replace('\\', '$');
        return title;
    }

    @Override
    protected boolean isPageEnd(CrawlerData data, int num) {
        if (num > 16)
            return true;
        return false;
    }

}
