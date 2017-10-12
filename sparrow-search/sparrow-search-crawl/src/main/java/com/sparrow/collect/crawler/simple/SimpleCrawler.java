package com.sparrow.collect.crawler.simple;

import com.sparrow.collect.crawler.check.DuplicateUrlCheck;
import com.sparrow.collect.crawler.check.UrlCheck;
import com.sparrow.collect.crawler.AbstractCrawler;
import com.sparrow.collect.crawler.conf.CrawlerConfig;
import com.sparrow.collect.crawler.conf.pool.PoolConfig;
import com.sparrow.collect.crawler.conf.pool.PoolFactory;
import com.sparrow.collect.crawler.conf.site.SelectorConfig;
import com.sparrow.collect.crawler.conf.site.SiteUrl;
import com.sparrow.collect.crawler.conf.store.StoreConfig;
import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.dom.CrawlerDom;
import com.sparrow.collect.crawler.dom.CrawlerNode;
import com.sparrow.collect.crawler.dom.impl.JsoupCrawlerDomImpl;
import com.sparrow.collect.crawler.httpclient.CrawlHttp;
import com.sparrow.collect.crawler.httpclient.CrawlKit;
import com.sparrow.collect.crawler.httpclient.HttpResp;
import com.sparrow.collect.crawler.listener.Listener;
import com.sparrow.collect.crawler.listener.ListenerSupport;
import com.sparrow.collect.crawler.selector.ConfiguredPageSelector;
import com.sparrow.collect.crawler.selector.DefaultPageSelector;
import com.sparrow.collect.crawler.selector.IPageSelector;
import com.sparrow.collect.log.CrawlerLog;
import com.sparrow.collect.store.*;
import com.sparrow.collect.utils.PathResolver;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Administrator on 2016/11/29.
 */
public class SimpleCrawler extends AbstractCrawler {
    private final CrawlKit kit = CrawlKit.KIT;
    private DataStore dataStore;
    private CrawlerConfig crawlerConfig;
    private ThreadPoolExecutor threadPool;
    private IPageSelector selector;
    private String tempDir;
    private ListenerSupport support;
    private UrlCheck urlCheck;
    private CrawlerLog crawlerLog;
    private String fetchType;
    private int retryNum = 3;
    private boolean singleThread = false;
    private boolean saveDetail = false;

    public SimpleCrawler() {
        this(null, null);
    }

    public SimpleCrawler(CrawlerConfig crawlerConfig) {
        this(crawlerConfig, new ListenerSupport());
    }

    public SimpleCrawler(CrawlerConfig crawlerConfig, ListenerSupport support) {
        this.crawlerConfig = crawlerConfig;
        this.support = support;
    }

    private void initialize() {
        if (this.crawlerConfig == null)
            this.crawlerConfig = CrawlerConfig.DEFAULT;
        this.crawlerConfig.checkConfig();
        this.tempDir = this.crawlerConfig.getTempDir(System.getProperty("java.io.tmpdir"));
        if (this.selector == null)
            this.threadPool = this.initPool(crawlerConfig.getPool());
        if (this.selector == null)
            this.selector = this.initSelector(crawlerConfig);
        if (this.support == null)
            this.support = new ListenerSupport();
        if (this.dataStore == null)
            this.dataStore = DataStoreFactory.createDataStore(crawlerConfig);
        if (this.urlCheck == null)
            this.urlCheck = crawlerConfig.isUrlFilter() ? DuplicateUrlCheck.getInstance(this.tempDir)
                    : DuplicateUrlCheck.DEFAULT_CHECK;
        if (this.crawlerLog == null)
            this.crawlerLog = new CrawlerLog(this.tempDir);
        this.retryNum = this.crawlerConfig.getRetryNum();
        this.singleThread = this.crawlerConfig.isSingleThread();
        this.saveDetail = this.crawlerConfig.getStore().isSaveFile();
        this.fetchType = this.crawlerConfig.getFetchType();
    }

    void setDataStore(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    void setCrawlerConfig(CrawlerConfig crawlerConfig) {
        this.crawlerConfig = crawlerConfig;
    }

    void setThreadPool(ThreadPoolExecutor threadPool) {
        this.threadPool = threadPool;
    }

    void setSelector(IPageSelector selector) {
        this.selector = selector;
    }

    void setTempDir(String tempDir) {
        this.tempDir = tempDir;
    }

    void setSupport(ListenerSupport support) {
        this.support = support;
    }

    void setUrlCheck(UrlCheck urlCheck) {
        this.urlCheck = urlCheck;
    }

    public void setCrawlerLog(CrawlerLog crawlerLog) {
        this.crawlerLog = crawlerLog;
    }

    void setFetchType(String fetchType) {
        this.fetchType = fetchType;
    }

    void setRetryNum(int retryNum) {
        this.retryNum = retryNum;
    }

    void setSingleThread(boolean singleThread) {
        this.singleThread = singleThread;
    }

    void setSaveDetail(boolean saveDetail) {
        this.saveDetail = saveDetail;
    }

    protected String getTempDir() {
        return tempDir;
    }

    protected UrlCheck getUrlCheck() {
        return urlCheck;
    }

    protected CrawlerLog getCrawlerLog() {
        return crawlerLog;
    }

    protected CrawlerConfig getCrawlerConfig() {
        return crawlerConfig;
    }

    public boolean isSingleThread() {
        return singleThread;
    }

    @Override
    public Listener getListener() {
        return support;
    }

    protected ThreadPoolExecutor initPool(PoolConfig config) {
        if (this.singleThread)
            return null;
        if (config != null && config.isUseDefault()) {
            PoolFactory.initializeDefaultPool(config);
            return PoolFactory.getDefault();
        } else
            return PoolFactory.newPool(config);
    }

    protected IPageSelector initSelector(SelectorConfig config) {
        if (config == null)
            return new DefaultPageSelector("a", "href");
        ConfiguredPageSelector selector = new ConfiguredPageSelector();
        selector.configure(config);
        return selector;
    }

    @Override
    public ThreadPoolExecutor getThreadPool() {
        return this.threadPool;
    }

    @Override
    public IPageSelector getSelector() {
        return this.selector;
    }


    @Override
    protected boolean checkUrl(String url) {
        return urlCheck.check(url);
    }

    @Override
    protected void cacheUrl(String url) {
        urlCheck.add(url);
    }

    protected void writeErrorLog(String status, String url, String reason) {
        this.crawlerLog.writeLine("crawl", status, url, reason);
    }

    @Override
    protected CrawlerData doCrawlData(EntryData entry) {
        HttpResp resp = this.kit.getHtml(entry.getUrl(), null, CrawlHttp.headers, "UTF-8", false, this.retryNum);
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
    protected CrawlerData doCrawlData(EntryData entry, String contentExpress) {
        CrawlerData data = this.doCrawlData(entry);
        if (StringUtils.isEmpty(contentExpress))
            return data;
        if (data != null) {
            CrawlerDom dom = this.createCrawlerDom(data);
            //Document doc = Jsoup.parse(data.getHtml());
            data.setContent(dom.text(contentExpress));
            // data.setContent(doc.select(contentExpress).text());
        }
        return data;
    }

    protected boolean isDownloadDetailFile() {
        return this.saveDetail;
    }

    @Override
    protected void doHandleDetail(EntryData itemEntry, SiteEntry siteEntry, EntryData pageEntry) {
        if (this.checkUrl(itemEntry.getUrl()))
            return;
        this.getListener().detailEnter(itemEntry);
        if (this.isDownloadDetailFile()) {
            this.downloadFile(itemEntry, pageEntry);
            this.cacheUrl(itemEntry.getUrl());
            this.getListener().detailEnd(null);
        } else {
            CrawlerData crawlerData = this.doCrawlData(itemEntry, siteEntry.getContentExpress());
            this.getListener().detailEnd(crawlerData);
            this.cacheUrl(itemEntry.getUrl());
            if (crawlerData != null)
                this.concurrentHandleCrawlerData(crawlerData, siteEntry, pageEntry);
        }
    }

    protected String getFileSaveDir(String dir) {
        if (StringUtils.isEmpty(dir))
            return this.tempDir;
        else
            return dir;
    }

    protected final void downloadFile(EntryData entry, EntryData pageEntry) {
        StoreConfig storeConfig = this.crawlerConfig.getStore();
        if (storeConfig == null) return;
        String suffix = StringUtils.isNotEmpty(storeConfig.getFileExt()) ?
                storeConfig.getFileExt() : PathResolver.getExtension(PathResolver.getFileName(entry.getUrl()));
        File file = new File(this.getFileSaveDir(storeConfig.getFileDir()),
                String.format("%s/%s.%s", pageEntry.getTitle(),
                        this.correctTitle(entry.getTitle()), suffix));
        if (file.exists())
            return;
        this.kit.downloadFile(entry.getUrl(), file, CrawlHttp.headers);
    }

    protected String correctTitle(String title) {
        if (StringUtils.isNotEmpty(title))
            return title.replace('/', '-').replace('\\', '-');
        return title;
    }

    private Object fetchData(CrawlerData crawlerData, SiteEntry siteEntry, EntryData pageEntry) {
        Object data;
        this.getListener().formatEnter(crawlerData);
        String type = this.fetchType;
        if ("html".equals(type))
            data = crawlerData.getHtml();
        else if ("content".equals(type))
            data = crawlerData.getContent();
        else if ("@self".equals(type)) {
            data = crawlerData;
        } else
            data = this.format(crawlerData, siteEntry, pageEntry);
        this.getListener().formatEnd(crawlerData, data);
        return data;
    }

    protected Object format(CrawlerData data, SiteEntry siteEntry, EntryData pageEntry) {
        return data;
    }

    @Override
    protected final void handleCrawlerData(CrawlerData crawlerData, SiteEntry siteEntry, EntryData pageEntry) {
        Object object = this.fetchData(crawlerData, siteEntry, pageEntry);
        if (object == null) {
            this.crawlerLog.writeLine("format",
                    String.format("Data format is null , type=%s", this.fetchType));
            return;
        }
        this.getListener().storeEnter(crawlerData, object);
        int effect = this.store(object);
        this.getListener().storeEnd(crawlerData, object, effect);
        crawlerData.clear();
    }

    protected int store(Object object) {
        DataStore dw = this.dataStore;
        if (dw != null)
            return dw.checkAndSave(object);
        return 0;
    }

    @Override
    protected boolean isPageEnd(CrawlerData data, int num) {
        if (data == null)
            return false;
        if (StringUtils.isNotEmpty(this.crawlerConfig.getPage().getEndExpress())) {
            CrawlerNode node = data.getDom().selectNode(this.crawlerConfig.getPage().getEndExpress());
            if (node == null)
                return true;
        }
        return false;
    }

    protected String buildPageNo(int num, int pageNoPlaces) {
        int places = pageNoPlaces;
        String pageNo = String.valueOf(num);
        int realNum = pageNo.length();
        StringBuilder sb = new StringBuilder();
        int over = places - realNum;
        if (over > 0)
            for (int i = 0; i < over; i++) sb.append('0');
        sb.append(pageNo);
        return sb.toString();
    }

    protected CrawlerDom createCrawlerDom(CrawlerData data) {
        JsoupCrawlerDomImpl dom = new JsoupCrawlerDomImpl();
        dom.parse(data.getHtml());
        return dom;
    }

    @Override
    protected EntryData generatePageEntry(SiteEntry siteEntry, int num) {
        boolean hasExpress = StringUtils.isNotEmpty(siteEntry.getPageExpress());
        EntryData entryData;
        if (!hasExpress) {
            if (num > 1)
                return null;
            else {
                entryData = new EntryData();
                entryData.setTitle(String.format("%s_(%s)", siteEntry.getTitle(), num));
                entryData.setUrl(siteEntry.getUrl());
                return entryData;
            }
        } else if (num == 1 && this.crawlerConfig.getPage().isIgnoreFirst()) {
            entryData = new EntryData();
            entryData.setTitle(String.format("%s_(%s)", siteEntry.getTitle(), num));
            entryData.setUrl(siteEntry.getUrl());
            return entryData;
        } else {
            entryData = new EntryData();
            String pageNo = (this.crawlerConfig.getPage().isFillZero()) ?
                    this.buildPageNo(num, this.crawlerConfig.getPage().getPlaceholders()) : String.valueOf(num);
            String suffix = siteEntry.getPageExpress().replace("${page}", pageNo);
            entryData.setTitle(String.format("%s_(%s)", siteEntry.getTitle(), pageNo));
            entryData.setUrl(siteEntry.getUrl() + suffix);
            return entryData;
        }
    }

    public void exec() {
        SiteEntry entry;
        this.getListener().crawlerEnter(this.crawlerConfig);
        for (SiteUrl site : this.crawlerConfig.getSiteUrls()) {
            entry = new SiteEntry();
            entry.setSiteId(this.crawlerConfig.getSite().getId());
            entry.setSiteName(this.crawlerConfig.getSite().getName());
            //使用默认配置
            if (StringUtils.isEmpty(site.getPageExpress())) {
                entry.setPageExpress(this.crawlerConfig.getPage().getEntryExpress());
                entry.setPageStart(this.crawlerConfig.getPage().getStart());
                entry.setPageEnd(this.crawlerConfig.getPage().getEnd());
            } else {
                entry.setPageExpress(site.getPageExpress());
                entry.setPageStart(site.getPageStart());
                entry.setPageEnd(site.getPageEnd());
            }
            if (StringUtils.isEmpty(site.getContentExpress())) {
                entry.setContentExpress(this.crawlerConfig.getContentExpress());
            } else {
                entry.setContentExpress(site.getContentExpress());
            }
            entry.setUrl(site.getUrl());
            entry.setTitle(site.getTitle());
            this.postPageExecute(entry);
        }
        if (!this.singleThread) {
            PoolFactory.waitActiveTasks(this.getThreadPool());
            PoolFactory.closeThreadPool(this.getThreadPool());
        }
        this.destroy();
        this.getListener().crawlerEnd(this.crawlerConfig);
    }

    protected void concurrentHandleCrawlerData(final CrawlerData crawlerData, final SiteEntry siteEntry, final EntryData pageEntry) {
        if (this.singleThread)
            this.handleCrawlerData(crawlerData, siteEntry, pageEntry);
        else
            this.getThreadPool().submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        SimpleCrawler.this.handleCrawlerData(crawlerData, siteEntry, pageEntry);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            });
    }


    protected void postPageExecute(final SiteEntry entry) {
        if (this.singleThread)
            this.pageExecute(entry);
        else
            this.getThreadPool().submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        SimpleCrawler.this.pageExecute(entry);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            });
    }

    @Override
    protected void pageExecute(final CrawlerData crawlerData,
                               final SiteEntry siteEntry,
                               final EntryData pEntry) {
        if (this.singleThread)
            this.doPageExecute(crawlerData, siteEntry, pEntry);
        else {
            Runnable job = new Runnable() {
                public void run() {
                    try {
                        SimpleCrawler.this.doPageExecute(crawlerData, siteEntry, pEntry);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            };
            this.getThreadPool().execute(job);
        }
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
                        SimpleCrawler.this.doHandleDetail(itemEntry, siteEntry, pageEntry);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            };
            this.getThreadPool().execute(job);
        }
    }

    @Override
    public void destroy() {
        // 使用默认的 Thread Pool 不需要关闭
        if (!this.singleThread && !this.crawlerConfig.getPool().isUseDefault()) {
            PoolFactory.closeThreadPool(this.getThreadPool());
        }
        this.urlCheck.close();
        this.crawlerLog.close();
        this.dataStore.close();
    }
}
