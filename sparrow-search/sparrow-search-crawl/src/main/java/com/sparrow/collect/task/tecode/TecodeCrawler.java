package com.sparrow.collect.task.tecode;

import com.sparrow.collect.crawler.conf.pool.PoolFactory;
import com.sparrow.collect.crawler.AbstractCrawler;
import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.httpclient.HttpResp;
import com.sparrow.collect.crawler.selector.IPageSelector;
import com.sparrow.collect.utils.FileIOUtil;
import com.sparrow.collect.crawler.httpclient.CrawlHttp;
import com.sparrow.collect.utils.PathResolver;
import com.sparrow.collect.crawler.httpclient.CrawlKit;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.concurrent.ThreadPoolExecutor;

public class TecodeCrawler extends AbstractCrawler {
    final CrawlKit kit =   CrawlKit.KIT;
    final File rootDir;
    protected IPageSelector selector;

    public TecodeCrawler(File rootDir) {
        this.rootDir = rootDir;
        if (!rootDir.exists())
            rootDir.mkdirs();
    }

    @Override
    public ThreadPoolExecutor getThreadPool() {
        return PoolFactory.getDefault();
    }

    @Override
    public IPageSelector getSelector() {
        return selector;
    }

    public void setSelector(IPageSelector selector) {
        this.selector = selector;
    }

    @Override
    protected CrawlerData doCrawlData(EntryData entry) {
        HttpResp resp = this.kit.getHtml(entry.getUrl(), null,
                CrawlHttp.headers, "UTF-8", false, 3);
        CrawlerData data = new CrawlerData();
        data.setHtml(resp.getHtml());
        data.setTitle(entry.getTitle());
        data.setUrl(entry.getUrl());
        return data;
    }

    @Override
    protected EntryData generatePageEntry(SiteEntry siteEntry, int num) {
        String url = siteEntry.getUrl();
        EntryData entryData = new EntryData();
        entryData.setTitle(siteEntry.getTitle());
        entryData.setUrl(url);
        return entryData;
    }

    protected final void doHandleDetail(EntryData entry, EntryData pageEntry) {
        String suffix = PathResolver.getExtension(PathResolver
                .getFileName(entry.getUrl()));
        File file = new File(this.rootDir, pageEntry.getTitle()
                + File.separatorChar + this.correctTitle(entry.getTitle())
                + "." + suffix);
        if (file.exists())
            return;
        System.out.println(pageEntry.getTitle());
        this.kit.downloadFile(entry.getUrl(), file, CrawlHttp.headers);
    }

    @Override
    protected void handleCrawlerData(CrawlerData crawlerData,
                                     SiteEntry siteEntry,
                                     EntryData pageEntry) {
        // System.out.println(pageEntry.getTitle() + " - " +
        // pageEntry.getUrl());
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
        if (num > 1)
            return true;
        return false;
    }

}
