package com.sparrow.collect.crawler;

import com.sparrow.collect.cache.bloom.UrlCheck;
import com.sparrow.collect.cache.bloom.DuplicateUrlCheck;
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
import com.sparrow.collect.crawler.selector.IPageSelector;
import com.sparrow.collect.log.CrawlerLog;
import com.sparrow.collect.orm.ParsedSql;
import com.sparrow.collect.orm.utils.NamedParameterUtils;
import com.sparrow.collect.store.*;
import com.sparrow.collect.utils.BeanUtils;
import com.sparrow.collect.utils.PathResolver;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Administrator on 2016/11/29.
 */
public class ConfiguredCrawler extends AbstractCrawler {
    private final CrawlKit kit;
    private final DataStore dataStore;
    private final CrawlerConfig crawlerConfig;
    private final ThreadPoolExecutor threadPool;
    private final IPageSelector selector;
    private final String tempDir;
    private final ListenerSupport support;
    private final UrlCheck urlCheck;
    private final CrawlerLog crawlerLog;
    private String fetchType;
    private int retryNum = 3;
    private boolean singleThread = false;
    private boolean saveDetail = false;

    public ConfiguredCrawler(CrawlerConfig crawlerConfig) {
        this(crawlerConfig, new ListenerSupport());
    }

    public ConfiguredCrawler(CrawlerConfig crawlerConfig, ListenerSupport support) {
        if (crawlerConfig == null)
            throw new RuntimeException("Crawler config must be not null!");
        crawlerConfig.checkConfig();
        this.kit = CrawlKit.KIT;
        this.crawlerConfig = crawlerConfig;
        this.tempDir = this.crawlerConfig.getTempDir(System.getProperty("java.io.tmpdir"));

        this.threadPool = this.initPool(crawlerConfig.getPool());
        this.selector = this.initSelector(crawlerConfig);
        this.support = (support == null ? new ListenerSupport() : support);

        this.dataStore = this.initDataStore(crawlerConfig.getStore());
        this.urlCheck = crawlerConfig.isUrlFilter() ? DuplicateUrlCheck.getInstance(this.tempDir)
                : DuplicateUrlCheck.DEFAULT_CHECK;
        this.crawlerLog = new CrawlerLog(this.tempDir);
        this.retryNum = this.crawlerConfig.getRetryNum();
        this.singleThread = this.crawlerConfig.isSingleThread();
        this.saveDetail = this.crawlerConfig.getStore().isSaveFile();
        this.fetchType = this.crawlerConfig.getFetchType();
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
        ConfiguredPageSelector selector = new ConfiguredPageSelector();
        selector.configure(config);
        return selector;
    }

    protected DataStore initDataStore(StoreConfig config) {
        if (config == null || StringUtils.isEmpty(config.getClazz())) {
            throw new RuntimeException("Data store class is empty");
        }
        DataStore dataStore;
        Map<String, String> storeProps = config.getProps();
        if (storeProps != null)
            storeProps.put("temp.data.dir", this.tempDir);
        if (StringUtils.equals(config.getClazz(), "file")) {
            try {
                String path = this.getFileSaveDir(config.getPath());
                File file = new File(path);
                if (!file.exists())
                    file.mkdirs();
                file = new File(file, String.format("%s.store", config.getAlias()));
                dataStore = new FileDataStore(file, config.isGzip());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException("File not found : " + config.getPath());
            }
        } else if (StringUtils.equals(config.getClazz(), "db")) {
            dataStore = new DatabaseStore(storeProps);
        } else if (StringUtils.equals(config.getClazz(), "batch")) {
            dataStore = new BatchDatabaseStore(storeProps);
        } else if (StringUtils.equals(config.getClazz(), "sql")) {
            String sql = storeProps.get("data.insert.sql");
            if (StringUtils.isEmpty(sql)) {
                throw new RuntimeException("Sql template store has not find property for key 'data.insert.sql'");
            }
            ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
            if (parsedSql.hasNamedParas() && parsedSql.hasTraditionalParas())
                throw new RuntimeException("不能同时处理named参数和传统的'?'参数");
            //必须是cache，cache check 是 BDB ， 才能处理array的参数，仅仅是sql模板的时候
            //而其他db和batch是可以使用BDB的cache check的，并且还可以使用db主键check
            storeProps.put("data.check.type", "cache");
            dataStore = new SqlTemplateStore(storeProps, parsedSql.getActualSql());
            this.crawlerConfig.getFormat().setParaNameIndexes(this.wrapParaNameIndexes(parsedSql));
            this.crawlerConfig.getFormat().setFormatter("array");
            parsedSql = null;
        } else {
            Object object = BeanUtils.newInstance(config.getClazz(), storeProps);
            if (object == null)
                throw new RuntimeException("Unkown data store class: " + config.getClass());
            dataStore = BeanUtils.cast(object, DataStore.class);
        }
        return dataStore;
    }

    String[] wrapParaNameIndexes(ParsedSql parsedSql) {
        int parameterIndexes[] = parsedSql.getParaIndexes();
        if (parameterIndexes == null || parameterIndexes.length == 0)
            return null;
        int len = parameterIndexes.length, pos;
        String paraNameIndexes[] = new String[len];
        String paramName;
        for (int i = 0; i < len; i++) {
            pos = parameterIndexes[i];
            if (pos > 0) {
                paramName = parsedSql.getParameter(pos - 1);
                paraNameIndexes[i] = paramName;
            }
        }
        return paraNameIndexes;
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
            CrawlerDom dom = this.createCrDom(data);
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

    protected CrawlerDom createCrDom(CrawlerData data) {
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
                        ConfiguredCrawler.this.handleCrawlerData(crawlerData, siteEntry, pageEntry);
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
                        ConfiguredCrawler.this.pageExecute(entry);
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
                        ConfiguredCrawler.this.doPageExecute(crawlerData, siteEntry, pEntry);
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
                        ConfiguredCrawler.this.doHandleDetail(itemEntry, siteEntry, pageEntry);
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
