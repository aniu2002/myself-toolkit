package com.sparrow.collect.crawler;

import com.sparrow.collect.crawler.conf.CrawlerConfig;
import com.sparrow.collect.crawler.conf.pool.PoolFactory;
import com.sparrow.collect.crawler.conf.site.SelectorConfig;
import com.sparrow.collect.crawler.conf.site.SiteConfig;
import com.sparrow.collect.crawler.conf.site.SiteUrl;
import com.sparrow.collect.crawler.conf.store.StoreConfig;
import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.dom.CrawlerDom;
import com.sparrow.collect.crawler.listener.ListenerSupport;
import com.sparrow.collect.crawler.selector.DefaultPageSelector;
import com.sparrow.collect.crawler.selector.IPageSelector;
import com.sparrow.collect.log.EntryLog;
import com.sparrow.collect.log.UrlReadCallback;
import com.sparrow.collect.store.DataStore;
import com.sparrow.collect.store.FileDataStore;
import com.sparrow.collect.utils.CollectionUtils;
import com.sparrow.collect.utils.PathResolver;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class ScopeCrawler extends ConfiguredCrawler {
    private volatile boolean stopped = false;
    private AtomicLong counter = new AtomicLong(0);
    private AtomicInteger cacheToDisk = new AtomicInteger(0);
    private AtomicBoolean readDiskFlag = new AtomicBoolean(true);
    private BlockingQueue<EntryData> queue = new ArrayBlockingQueue(500);
    private EntryLog entryLog;
    final ReentrantLock lock = new ReentrantLock();

    public ScopeCrawler(CrawlerConfig crawlerConfig, ListenerSupport support) {
        super(crawlerConfig, support);
        this.entryLog = new EntryLog(this.getTempDir());
    }

    public ScopeCrawler(CrawlerConfig crawlerConfig) {
        this(crawlerConfig, null);
    }

    protected IPageSelector initSelector(SelectorConfig config) {
        IPageSelector selector = new DefaultPageSelector("a", "href");
        return selector;
    }

    protected DataStore initDataStore(StoreConfig config) {
        DataStore dataStore;
        try {
            String path = this.getFileSaveDir(config.getPath());
            File f = new File(path);
            if (!f.exists())
                f.mkdirs();
            f = new File(f, String.format("%s.crawl", config.getAlias()));
            dataStore = new FileDataStore(f, config.isGzip());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("File directory is not exists : " + config.getPath());
        }
        return dataStore;
    }

    protected final void pageExecute(SiteEntry siteEntry) {
        this.addCrawlSeed(siteEntry);
    }

    protected void postPageExecute(final SiteEntry entry) {
        this.pageExecute(entry);
    }

    public void exec() {
        final SiteConfig siteConfig = this.getCrawlerConfig().getSite();
        this.getListener().crawlerEnter(this.getCrawlerConfig());
        if (CollectionUtils.isEmpty(this.getCrawlerConfig().getSiteUrls()))
            this.addCrawlSeed(new EntryData(siteConfig.getUrl(), siteConfig.getName()));
        else
            this.configureCrawlSeed(this.getCrawlerConfig(), siteConfig);
        if (this.isSingleThread()) {
            this.executeCrawl(siteConfig);
        } else {
            this.getThreadPool().submit(new Runnable() {
                @Override
                public void run() {
                    ScopeCrawler.this.executeCrawl(siteConfig);
                }
            });
            PoolFactory.waitActiveTasks(this.getThreadPool());
            PoolFactory.closeThreadPool(this.getThreadPool());
        }
        this.destroy();
        this.getListener().crawlerEnd(this.getCrawlerConfig());
    }

    protected void configureCrawlSeed(CrawlerConfig crawlerConfig, SiteConfig siteConfig) {
        SiteEntry entry;
        for (SiteUrl site : crawlerConfig.getSiteUrls()) {
            entry = new SiteEntry();
            entry.setSiteId(siteConfig.getId());
            entry.setSiteName(siteConfig.getName());
            //使用默认配置
            if (StringUtils.isEmpty(site.getContentExpress())) {
                entry.setContentExpress(this.getCrawlerConfig().getContentExpress());
            } else {
                entry.setContentExpress(site.getContentExpress());
            }
            entry.setUrl(site.getUrl());
            entry.setTitle(site.getTitle());
            this.postPageExecute(entry);
        }
    }

    public void addCrawlSeed(EntryData entry) {
        boolean flag = this.queue.offer(entry);
        if (!flag) {
            this.entryLog.writeUrl(entry);
            this.cacheToDisk.incrementAndGet();
        }
        this.counter.incrementAndGet();
    }

    public EntryData getCrawlSeed() {
        BlockingQueue<EntryData> queue = this.queue;
        EntryData entryData = null;
        try {
            entryData = queue.poll(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (entryData == null) {
            try {
                lock.lock();
                if (this.readDiskFlag.get() && this.cacheToDisk.get() > 0) {
                    this.readDiskFlag.set(false);
                    this.loadDiskEntry(queue);
                }
            } finally {
                lock.unlock();
            }
            try {
                return queue.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return entryData;
            }
        } else {
            return entryData;
        }
    }

    void loadDiskEntry(final BlockingQueue<EntryData> queue) {
        if (this.isSingleThread()) {
            new Thread() {
                @Override
                public void run() {
                    ScopeCrawler.this.doLoadDiskEntry(queue);
                }
            }.start();
        } else
            this.getThreadPool().submit(new Runnable() {
                @Override
                public void run() {
                    ScopeCrawler.this.doLoadDiskEntry(queue);
                }
            });
    }

    void doLoadDiskEntry(final BlockingQueue<EntryData> queue) {
        if (this.counter.get() > 0) {
            int rows = this.entryLog.readUrl(new UrlReadCallback() {
                @Override
                public boolean handle(EntryData data) {
                    try {
                        return queue.offer(data, 5, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            });
            this.cacheToDisk.set(this.cacheToDisk.get() - rows);
            this.readDiskFlag.set(true);
        } else {
            this.stopped = true;
        }
    }

    protected void executeCrawl(SiteConfig siteConfig) {
        SiteEntry entry = new SiteEntry();
        entry.setSiteId(siteConfig.getId());
        entry.setSiteName(siteConfig.getName());
        do {
            EntryData entryData = this.getCrawlSeed();
            if (entryData != null)
                this.handleDetail(entryData, entry, entryData);
        } while (!stopped && this.counter.get() > 0);
    }

    public void stop() {
        this.stopped = true;
    }

    protected void doHandleDetail(EntryData itemEntry, SiteEntry siteEntry, EntryData pageEntry) {
        if (this.checkUrl(itemEntry.getUrl())) {
            this.counter.getAndDecrement();
            return;
        }
        this.getListener().detailEnter(itemEntry);
        CrawlerData crawlerData = this.doCrawlData(itemEntry);
        this.getListener().detailEnd(crawlerData);
        this.cacheUrl(itemEntry.getUrl());
        if (crawlerData != null) {
            this.concurrentHandleCrawlerData(crawlerData, siteEntry, pageEntry);
        } else {
            this.counter.getAndDecrement();
        }
    }

    protected final void handleDetail(EntryData itemEntry, SiteEntry siteEntry, EntryData pageEntry) {
        super.handleDetail(itemEntry, siteEntry, pageEntry);
    }

    protected void concurrentHandleCrawlerData(CrawlerData crawlerData, SiteEntry siteEntry, EntryData pageEntry) {
        //crawlerData.setDom();
        try {
            if (StringUtils.isEmpty(crawlerData.getHtml()))
                return;
            CrawlerDom dom = this.createCrDom(crawlerData);
            if (StringUtils.isNotEmpty(siteEntry.getContentExpress())) {
                crawlerData.setContent(dom.text(siteEntry.getContentExpress()));
            }
            super.concurrentHandleCrawlerData(crawlerData, siteEntry, pageEntry);
            this.fillCrawlSeed(dom, pageEntry);
        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println(crawlerData.getUrl());
        } finally {
            this.counter.getAndDecrement();
        }
    }

    protected void fillCrawlSeed(CrawlerDom dom, EntryData crawlEntry) {
        if (crawlEntry.getDeep() > 2)
            return;
        IPageSelector curSelector = this.getSelector();
        if (curSelector == null)
            curSelector = defaultSelector;
        List<EntryData> entries = curSelector.selectPageEntries(dom, crawlEntry);
        //没有page可抓取，已经结束
        if (CollectionUtils.isEmpty(entries)) {
            return;
        }
        String siteHost = this.getCrawlerConfig().getSite().getHost();
        for (EntryData entryData : entries) {
            if (StringUtils.isEmpty(entryData.getUrl())) {
                continue;
            } else if (StringUtils.indexOf(entryData.getUrl(), siteHost) == -1) {
                continue;
            } else if ((StringUtils.startsWith(entryData.getUrl(), "http://")
                    || StringUtils.startsWith(entryData.getUrl(), "https://"))) {
                String suffix = PathResolver.getExtension(entryData.getUrl()).toLowerCase();
                if (StringUtils.equals("jpeg", suffix) || StringUtils.equals("jpg", suffix)
                        || StringUtils.equals("gif", suffix))
                    continue;
                entryData.setDeep(crawlEntry.getDeep() + 1);
                this.addCrawlSeed(entryData);
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        this.entryLog.close();
    }
}
