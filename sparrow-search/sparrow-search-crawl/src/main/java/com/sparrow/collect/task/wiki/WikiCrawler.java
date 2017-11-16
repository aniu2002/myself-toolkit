package com.sparrow.collect.task.wiki;


import com.sparrow.collect.cache.bloom.DuplicateUrlCheck;
import com.sparrow.collect.cache.bloom.UrlCheck;
import com.sparrow.collect.crawler.AbstractCrawler;
import com.sparrow.collect.crawler.conf.pool.PoolFactory;
import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.httpclient.CrawlKit;
import com.sparrow.collect.crawler.httpclient.HttpResp;
import com.sparrow.collect.crawler.selector.IPageSelector;
import com.sparrow.collect.utils.FileIOUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public class WikiCrawler extends AbstractCrawler {
    final CrawlKit kit = CrawlKit.KIT;
    final File rootDir;
    protected IPageSelector selector;
    final Map<String, String> headers;
    private final UrlCheck urlCheck;

    {
        headers = new HashMap<String, String>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        // headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("Cookie", "MOIN_SESSION_80_ROOT_wiki=9daa33b4963de15b37dd2b8de222e6113013b5f0");
        // headers.put("Host", "www.tuicool.com");
        // headers.put("Referer", "http://www.tuicool.com/");
    }

    public WikiCrawler(File rootDir) {
        this.rootDir = rootDir;
        this.urlCheck = (rootDir.isDirectory() && rootDir.exists()) ? DuplicateUrlCheck.getInstance(this.rootDir.getPath())
                : DuplicateUrlCheck.DEFAULT_CHECK;
    }

    @Override
    public ThreadPoolExecutor getThreadPool() {
        return PoolFactory.getDefault();
    }

    @Override
    protected boolean checkUrl(String url) {
        return urlCheck.check(url);
    }

    @Override
    protected void cacheUrl(String url) {
        urlCheck.add(url);
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
        HttpResp resp = this.kit.getHtml(entry.getUrl(), null, headers, "UTF-8", false);
        if (resp.getStatus() != 200) {
            this.writeErrorLog(String.valueOf(resp.getStatus()), entry.getUrl(), resp.getError());
            return null;
        }
        CrawlerData data = new CrawlerData();
        data.setHtml(resp.getHtml());
        data.setTitle(entry.getTitle());
        data.setUrl(entry.getUrl());
        return data;
    }

    @Override
    protected EntryData generatePageEntry(SiteEntry siteEntry, int num) {
        return siteEntry;
    }

    protected boolean ignoreErrorStatusData() {
        return true;
    }

    @Override
    protected void handleCrawlerData(CrawlerData crawlerData,
                                     SiteEntry siteEntry,
                                     EntryData pageEntry) {
        FileIOUtil.writeFile(
                new File(this.rootDir, pageEntry.getTitle()
                        + File.separatorChar + this.correctTitle(crawlerData.getTitle()) + ".md"),
                crawlerData.getContent(), "UTF-8"
        );

    /*    FileIOUtil.writeFile(
                new File(this.rootDir, pageEntry.getTitle()
                        + File.separatorChar + "html" + File.separatorChar
                        + this.correctTitle(crawlerData.getTitle()) + ".html"),
                crawlerData.getHtml(), "UTF-8"
        );*/
    }

    String correctTitle(String title) {
        if (StringUtils.isNotEmpty(title))
            return title.replace('/', '-').replace('\\', '-');
        return title;
    }

    @Override
    protected void handleDetail(final EntryData itemEntry,
                                final SiteEntry siteEntry,
                                final EntryData pageEntry) {
        this.doHandleDetail(itemEntry, siteEntry, pageEntry);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void handleDetailX(final EntryData itemEntry,
                                 final SiteEntry siteEntry,
                                 final EntryData pageEntry) {
        Runnable job = new Runnable() {
            public void run() {
                try {
                    WikiCrawler.this.doHandleDetail(itemEntry, siteEntry, pageEntry);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        };
        this.getThreadPool().execute(job);
    }

    @Override
    protected boolean isPageEnd(CrawlerData data, int num) {
        if (num > 1)
            return true;
        return false;
    }

    public void destroy() {
        PoolFactory.closeThreadPool(this.getThreadPool());
        this.urlCheck.close();
    }
}
