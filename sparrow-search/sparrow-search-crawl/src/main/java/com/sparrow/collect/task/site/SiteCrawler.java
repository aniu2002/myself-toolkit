package com.sparrow.collect.task.site;

import com.sparrow.collect.crawler.check.DuplicateUrlCheck;
import com.sparrow.collect.crawler.check.UrlCheck;
import com.sparrow.collect.crawler.AbstractCrawler;
import com.sparrow.collect.crawler.conf.pool.PoolFactory;
import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.dom.CrawlerDom;
import com.sparrow.collect.crawler.httpclient.CrawlKit;
import com.sparrow.collect.crawler.httpclient.HttpResp;
import com.sparrow.collect.crawler.selector.IPageSelector;
import com.sparrow.collect.crawler.selector.MultiExpressSelector;
import com.sparrow.collect.crawler.selector.NormalPageSelector;
import com.sparrow.collect.utils.CollectionUtils;
import com.sparrow.collect.utils.FileIOUtil;
import com.sparrow.collect.utils.PathResolver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

public class SiteCrawler extends AbstractCrawler {
    final static Logger logger = LoggerFactory.getLogger(SiteCrawler.class);
    final CrawlKit kit = CrawlKit.KIT;
    final File rootDir;
    final UrlCheck urlCheck;

    private String ignoreList[];
    private AtomicLong counter = new AtomicLong(0);

    private boolean singleThread = true;
    private SiteEntry siteEntry;
    private String host;

    IPageSelector selector;
    final Map<String, String> headers;

    {
        headers = new HashMap<String, String>();
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflate");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        headers.put("Authorization", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5dWFuemhlbmdjaHUyMDAyQDE2My5jb20iLCJhdWRpZW5jZSI6IndlYiIsImNyZWF0ZWQiOjE0ODc5MDEyODQ5NzQsImV4cCI6MTQ4ODUwNjA4NH0\n" +
                ".-nV1wJHQzTokpVWD3LMXsWEcfPedT1CvK1RvZvkeMNBWln7x6PMy_87nxAx5ZGf9nJNfObxWH1j7kMzXY_c56w");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
        //headers.put("Upgrade-Insecure-Requests", "1");
        //headers.put("Cookie", "MOIN_SESSION_80_ROOT_wiki=9daa33b4963de15b37dd2b8de222e6113013b5f0");
    }

    public SiteCrawler(File rootDir) {
        this.rootDir = rootDir;
        this.urlCheck = (rootDir.isDirectory() && rootDir.exists()) ? DuplicateUrlCheck.getInstance(this.rootDir.getPath())
                : DuplicateUrlCheck.DEFAULT_CHECK;
    }

    public String[] getIgnoreList() {
        return ignoreList;
    }

    public void setIgnoreList(String[] ignoreList) {
        this.ignoreList = ignoreList;
    }

    public boolean isSingleThread() {
        return singleThread;
    }

    public void setSingleThread(boolean singleThread) {
        this.singleThread = singleThread;
    }

    public void setSelector(IPageSelector selector) {
        this.selector = selector;
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

    protected IPageSelector initSelector() {
        MultiExpressSelector selector = new MultiExpressSelector();
        selector.addSelector(new NormalPageSelector(IPageSelector.HREF));
        selector.addSelector(new NormalPageSelector(IPageSelector.IMG));
        selector.addSelector(new NormalPageSelector(IPageSelector.CSS));
        selector.addSelector(new NormalPageSelector(IPageSelector.SCRIPT));
        return selector;
    }

    @Override
    public IPageSelector getSelector() {
        return this.selector;
    }

    public void exec(final SiteEntry siteEntry) {
        this.siteEntry = siteEntry;
        this.host = PathResolver.getHttpHost(siteEntry.getUrl());
        if (this.isSingleThread()) {
            this.executeCrawl(siteEntry);
        } else {
            this.getThreadPool().submit(new Runnable() {
                @Override
                public void run() {
                    SiteCrawler.this.executeCrawl(siteEntry);
                }
            });
            PoolFactory.waitActiveTasks(this.getThreadPool());
            PoolFactory.closeThreadPool(this.getThreadPool());
        }
        this.destroy();
    }

    protected void executeCrawl(SiteEntry siteEntry) {
        if (siteEntry != null)
            this.handleDetail(siteEntry, siteEntry, siteEntry);
    }

    protected void doHandleDetail(EntryData itemEntry, SiteEntry siteEntry, EntryData pageEntry) {
        if (this.needPageCheck() && this.checkUrl(itemEntry.getUrl())) {
            return;
        }
        this.getListener().detailEnter(itemEntry);
        CrawlerData crawlerData = this.doCrawlData(itemEntry);
        this.cacheUrl(itemEntry.getUrl());
        this.getListener().detailEnd(crawlerData);
        if (crawlerData != null) {
            this.saveCrawlerHtmlData(crawlerData, siteEntry, pageEntry);
        } else {
            this.counter.getAndDecrement();
        }
       /* try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    protected void storeCrawlerData(final CrawlerData crawlerData, final SiteEntry siteEntry, final EntryData pageEntry) {
        if (this.singleThread)
            this.handleCrawlerData(crawlerData, siteEntry, pageEntry);
        else
            this.getThreadPool().submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        SiteCrawler.this.handleCrawlerData(crawlerData, siteEntry, pageEntry);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            });
    }

    protected void saveCrawlerHtmlData(CrawlerData crawlerData, SiteEntry siteEntry, EntryData pageEntry) {
        //crawlerData.setDom();
        try {
            if (StringUtils.isEmpty(crawlerData.getHtml()))
                return;
            CrawlerDom dom = this.createCrawlerDom(crawlerData);
            crawlerData.setDom(dom);
            if (StringUtils.isNotEmpty(siteEntry.getContentExpress())) {
                crawlerData.setContent(dom.text(siteEntry.getContentExpress()));
            }
            IPageSelector curSelector = this.getSelector();
            List<EntryData> entries = curSelector.selectPageEntries(dom, pageEntry);
            this.storeCrawlerData(crawlerData, siteEntry, pageEntry);
/*            System.out.println(" --- " + crawlerData.getUrl());
            for (EntryData en : entries)
                System.out.println(" \t - " + en.getUrl());*/
            this.execCrawlSeed(entries, pageEntry);
        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println("抓取异常:" + crawlerData.getUrl());
        } finally {
            this.counter.getAndDecrement();
        }
    }

    protected void execCrawlSeed(List<EntryData> entries, EntryData crawlEntry) {
        if (crawlEntry.getDeep() > 10)
            return;
        //没有page可抓取，已经结束
        if (CollectionUtils.isEmpty(entries)) {
            return;
        }
        String siteHost = this.host;
        for (EntryData en : entries) {
            if (StringUtils.isEmpty(en.getUrl())) {
                continue;
            } else if (StringUtils.indexOf(en.getUrl(), siteHost) == -1) {
                continue;
            } else if ((StringUtils.startsWith(en.getUrl(), "http://")
                    || StringUtils.startsWith(en.getUrl(), "https://"))) {
             /*   String suffix = PathResolver.getExtension(en.getUrl()).toLowerCase();
                if (StringUtils.equals("jpeg", suffix) || StringUtils.equals("jpg", suffix)
                        || StringUtils.equals("gif", suffix))
                    continue;*/
                String str = UrlKit.formatUrl(en.getUrl());
                if (StringUtils.isEmpty(str)) {
                    if (logger.isWarnEnabled())
                        logger.warn("url {} path is empty", en.getUrl());
                    continue;
                } else if (this.checkUrl(str)) {
                    if (logger.isWarnEnabled())
                        logger.warn("url {} has crawled", str);
                    continue;
                } else if (!this.hasFile(str)) {
                    if (logger.isWarnEnabled())
                        logger.warn("url {} has not file", str);
                    continue;
                }
                en.setUrl(str);
                en.setDeep(crawlEntry.getDeep() + 1);
                this.crawlNextSeed(en);
            }
        }
    }

    protected boolean hasFile(String url) {
        return PathResolver.hasFileExtension(url);
    }

    public static void main(String args[]) {
        System.out.println(new SiteCrawler(new File("d:/")).hasFile("http://www.icloudunion.com/modelExample/金融/客户不良贷款模型.html"));
    }

    public void crawlNextSeed(EntryData entryData) {
        if (this.skipCrawlUrl(entryData.getUrl()))
            return;
        this.handleDetail(entryData, this.siteEntry, entryData);
    }

    boolean skipCrawlUrl(String url) {
        String igl[] = this.ignoreList;
        if (igl == null || igl.length == 0)
            return false;
        for (String s : igl) {
            if (StringUtils.equals(s, url))
                return true;
        }
        return false;
    }

    boolean skipCrawlUrl1(String url) {
        return StringUtils.equals("http://www.icloudunion.com/modelExample", url)
                || StringUtils.equals("http://www.icloudunion.com", url)
                || StringUtils.equals("http://www.icloudunion.com/modelExample/金融/信用卡异常检测模型", url);
    }

    @Override
    protected boolean needPageCut() {
        return false;
    }

    @Override
    protected boolean needSiteCheck() {
        return false;
    }

    @Override
    protected boolean needPageCheck() {
        return true;
    }

    @Override
    protected CrawlerData doCrawlData(EntryData entry) {
        String suffix = entry.getPageType();
        if (StringUtils.isEmpty(suffix))
            suffix = PathResolver.getExtension(PathResolver.getFileName(UrlKit.getUrlPath(entry.getUrl())));
        if (StringUtils.isEmpty(suffix))
            suffix = "html";
        if (StringUtils.equals("html", suffix)) {
            HttpResp resp = this.kit.getHtml(entry.getUrl(), null, headers, "UTF-8", false);
            if (resp.getStatus() != 200) {
                this.writeErrorLog(String.valueOf(resp.getStatus()), entry.getUrl(), resp.getError());
                return null;
            }
            CrawlerData data = new CrawlerData();
            data.setHtml(resp.getHtml());
            data.setTitle(entry.getTitle());
            data.setUrl(entry.getUrl());
            data.setPageType(entry.getPageType());
            return data;
        } else {
            try {
                String path = this.getSavePath(entry.getUrl());
                this.kit.saveStream(entry.getUrl(),
                        new File(this.rootDir, path), headers);
                //System.out.println(String.format("--- %s -> %s", suffix, path));
            } catch (Throwable t) {
                System.out.println(" exception : " + entry.getUrl());
            }
            return null;
        }
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
        this.writeHtml(crawlerData, siteEntry, pageEntry);
        //PageData pageData = this.fetchPageEntries(pageEntry, crawlerData);
    }

    String getSavePath(String str) {
        try {
            URI uri = new URI(str);
            String path = uri.getPath();
            if (StringUtils.isEmpty(path))
                return null;
            path = path.substring(1);
           /* String fn = PathResolver.getFileName(path);
            if ("index.html".equals(fn))
                return fn;*/
            return path;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    void writeHtml(CrawlerData crawlerData,
                   SiteEntry siteEntry,
                   EntryData pageEntry) {
        String path = this.getSavePath(crawlerData.getUrl());
        if (StringUtils.isNotEmpty(path)) {
            // System.out.println(String.format("--- html -> %s", path));
            FileIOUtil.writeFile(
                    new File(this.rootDir, path),
                    crawlerData.getDom().toHtml(), "UTF-8"
            );
        }
    }

    @Override
    protected void handlePageData(CrawlerData crawlerData, SiteEntry siteEntry, EntryData pageEntry) {
        this.writeHtml(crawlerData, siteEntry, pageEntry);
    }

    @Override
    protected void handleDetail(final EntryData itemEntry,
                                final SiteEntry siteEntry,
                                final EntryData pageEntry) {
        if (this.singleThread)
            this.doHandleDetail(itemEntry, siteEntry, pageEntry);
        else {
            Runnable job = new Runnable() {
                public void run() {
                    try {
                        SiteCrawler.this.doHandleDetail(itemEntry, siteEntry, pageEntry);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            };
            this.getThreadPool().execute(job);
        }
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
