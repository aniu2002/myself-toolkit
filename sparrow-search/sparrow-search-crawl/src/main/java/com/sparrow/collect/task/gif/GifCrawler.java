package com.sparrow.collect.task.gif;


import com.sparrow.collect.crawler.check.DuplicateUrlCheck;
import com.sparrow.collect.crawler.check.UrlCheck;
import com.sparrow.collect.crawler.check.UrlCheck4Guava;
import com.sparrow.collect.crawler.AbstractCrawler;
import com.sparrow.collect.crawler.check.UrlCheck4HashSet;
import com.sparrow.collect.crawler.conf.pool.PoolFactory;
import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.dom.CrawlerDom;
import com.sparrow.collect.crawler.dom.CrawlerNode;
import com.sparrow.collect.crawler.httpclient.CrawlHttp;
import com.sparrow.collect.crawler.httpclient.CrawlKit;
import com.sparrow.collect.crawler.httpclient.HttpResp;
import com.sparrow.collect.crawler.selector.IPageSelector;
import com.sparrow.collect.log.CrawlerLog;
import com.sparrow.collect.utils.FileIOUtil;
import com.sparrow.collect.utils.PathResolver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

public class GifCrawler extends AbstractCrawler {
    static final Logger log = LoggerFactory.getLogger(GifCrawler.class);
    final CrawlKit kit = CrawlKit.KIT;
    final File rootDir;
    final UrlCheck urlCheck;
    protected IPageSelector selector;
    private boolean singleThread = true;
    private BufferedWriter writer;
    private CrawlerLog crawlerLog;
    private String gifItemSelect;
    private boolean pageCheck;
    private boolean siteCheck;
    private boolean detailCheck = true;
    private boolean useProxy = true;

    public GifCrawler(File rootDir) {
        this.rootDir = rootDir;
        this.urlCheck = (rootDir.isDirectory() && rootDir.exists()) ? UrlCheck4HashSet.getInstance(this.rootDir.getPath())
                : DuplicateUrlCheck.DEFAULT_CHECK;
        if (!rootDir.exists())
            rootDir.mkdirs();
        try {
            this.writer = FileIOUtil.getWriter(new File(this.rootDir, "ad.txt"), FileIOUtil.DEFAULT_ENCODING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.crawlerLog = new CrawlerLog(rootDir.getPath());
    }

    public boolean isSingleThread() {
        return singleThread;
    }

    public boolean isUseProxy() {
        return useProxy;
    }

    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    public String getGifItemSelect() {
        return gifItemSelect;
    }

    public void setGifItemSelect(String gifItemSelect) {
        this.gifItemSelect = gifItemSelect;
    }

    public void setSiteCheck(boolean siteCheck) {
        this.siteCheck = siteCheck;
    }

    @Override
    protected boolean needSiteCheck() {
        return siteCheck;
    }

    public void setDetailCheck(boolean detailCheck) {
        this.detailCheck = detailCheck;
    }

    @Override
    protected boolean needDetailCheck() {
        return detailCheck;
    }

    public void setPageCheck(boolean pageCheck) {
        this.pageCheck = pageCheck;
    }

    protected boolean ignoreErrorStatusData() {
        return true;
    }

    @Override
    protected boolean needPageCheck() {
        return pageCheck;
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
    protected boolean checkUrl(String url) {
        return urlCheck.check(url);
    }

    @Override
    protected void cacheUrl(String url) {
        urlCheck.add(url);
    }

    protected int sleepSeconds() {
        return 0;
    }

    @Override
    protected CrawlerData doCrawlData(EntryData entry) {
        HttpResp resp = this.kit.getHtml(entry.getUrl(), null,
                CrawlHttp.headers, "UTF-8", this.useProxy, 1);
        CrawlerData data = new CrawlerData();
        data.setHtml(resp.getHtml());
        data.setTitle(entry.getTitle());
        data.setUrl(entry.getUrl());
        data.setStatus(resp.getStatus());
        if (this.sleepSeconds() > 0) {
            try {
                Thread.sleep(this.sleepSeconds() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    @Override
    protected EntryData generatePageEntry(SiteEntry siteEntry, int num) {
        String url = siteEntry.getUrl();
        EntryData entryData = new EntryData();
        String realUrl = num == 1 ? url : url + "/page/" + String.valueOf(num);
        entryData.setTitle(siteEntry.getTitle());
        entryData.setUrl(realUrl);
        return entryData;
    }

    protected void writeErrorLog(String status, String url, String reason) {
        this.crawlerLog.writeLine("crawl", status, url, reason);
    }

    boolean isImgNode(CrawlerNode node) {
        return StringUtils.equalsIgnoreCase("img", node.nodeName());
    }

    void writeLog(String title, String imgPath, String imgUrl, CrawlerData crawlerData) {
        if (StringUtils.isEmpty(imgPath))
            return;
        try {
            this.writer.write(title + " [" + imgPath.substring(1) + "] - "
                    + crawlerData.getUrl() + " > " + imgUrl
                    + FileIOUtil.LINE_SEPARATOR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    AtomicLong num = new AtomicLong(0);

    void handleOneGif(Iterator<CrawlerNode> iterator, CrawlerData crawlerData) {
        CacheWrap wrap = new CacheWrap(this, crawlerData);
        while (iterator.hasNext()) {
            CrawlerNode crawlerNode = iterator.next();
            String t = crawlerNode.attr("class");
            if (StringUtils.equalsIgnoreCase(t, "post-copyright"))
                return;
            CrawlerNode fNode = crawlerNode.childSize() > 0 ? crawlerNode.childNode(0) : crawlerNode;
            if (!this.isImgNode(fNode)) {
                wrap.setTitle(fNode.text());
            } else {
                String url = fNode.attr("src");
                String relativePath = "img" + File.separatorChar +
                        PathResolver.getFileName(url);
                File f = new File(this.rootDir, relativePath);
                wrap.appendIcon(relativePath, url);
                if (this.checkUrl(url) && f.length() > 0) {
                    log.warn(" -- URL skip download - {}", url);
                    num.incrementAndGet();
                    continue;
                }
                this.downFile(url, f);
                this.cacheUrl(url);
            }
        }
        wrap.end();
    }

    @Override
    protected void handleCrawlerData(CrawlerData crawlerData,
                                     SiteEntry siteEntry,
                                     EntryData pageEntry) {
        if (StringUtils.isEmpty(crawlerData.getHtml()))
            return;
        CrawlerDom dom = this.createCrawlerDom(crawlerData);
        crawlerData.setDom(dom);
        List<CrawlerNode> items = dom.selectNodes(this.getGifItemSelect());
        if (items == null || items.isEmpty())
            return;
        this.handleOneGif(items.iterator(), crawlerData);
    }

    String correctTitle(String title) {
        if (StringUtils.isNotEmpty(title)) {
            int idx = title.indexOf('：');
            if (idx != -1)
                return title.substring(idx + 1);
            else
                return title;
        }
        return title;
    }

    protected final void downFile(String url, File file) {
        boolean f = true;
        if (!file.exists())
            f = this.kit.saveStream(url, file, CrawlHttp.headers, true);
        else if (file.length() == 0)
            f = this.kit.saveStream(url, file, CrawlHttp.headers, true);
        if (!f)
            this.writeErrorLog("404", url, "un download");
    }

    @Override
    protected boolean isPageEnd(CrawlerData data, int num) {
        if (data == null || data.getStatus() != 200)
            return true;
        if (num > 100)
            return true;
        return false;
    }

    protected List<String> getKeywords() {
        return FileIOUtil.readLines("classpath:conf/gif-words.txt");
    }

    public void setSingleThread(boolean singleThread) {
        this.singleThread = singleThread;
    }

    SiteEntry generateEntry(SiteEntry siteEntry) {
        SiteEntry se = new SiteEntry();
        se.setPageExpress(siteEntry.getPageExpress());
        se.setContentExpress(siteEntry.getContentExpress());
        se.setSiteId(siteEntry.getSiteId());
        se.setSiteName(siteEntry.getSiteName());
        se.setPageType(siteEntry.getPageType());
        se.setRelativePath(siteEntry.getRelativePath());
        se.setPageStart(siteEntry.getPageStart());
        se.setPageEnd(siteEntry.getPageEnd());
        return se;
    }

    void startCrawl(final SiteEntry se) {
        if (this.isSingleThread())
            super.exec(se);
        else
            this.getThreadPool().submit(new Runnable() {
                @Override
                public void run() {
                    GifCrawler.super.exec(se);
                }
            });
    }

    @Override
    public void exec(final SiteEntry siteEntry) {
        SiteEntry se = this.generateEntry(siteEntry);
        se.setTitle("最新热门");
        se.setUrl("http://www.dongde.in/");
        this.startCrawl(se);
        List<String> list = this.getKeywords();
        for (String key : list) {
            se = this.generateEntry(siteEntry);
            se.setTitle(key);
            se.setUrl(siteEntry.getUrl() + "/" + key);
            this.startCrawl(se);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        PoolFactory.closeThreadPool(this.getThreadPool());
        this.urlCheck.close();
        try {
            this.writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.crawlerLog.close();
        System.out.println("exists imgs  : " + num.get());
    }
}
