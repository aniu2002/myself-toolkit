package com.sparrow.collect.crawler;

import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.PageData;
import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.dom.CrawlerDom;
import com.sparrow.collect.crawler.dom.impl.JsoupCrawlerDomImpl;
import com.sparrow.collect.crawler.exceptions.NoMorePageException;
import com.sparrow.collect.crawler.listener.DefaultListener;
import com.sparrow.collect.crawler.listener.Listener;
import com.sparrow.collect.crawler.selector.DefaultPageSelector;
import com.sparrow.collect.crawler.selector.IPageSelector;
import com.sparrow.collect.utils.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

public abstract class AbstractCrawler {
    static final Listener defaultListener = new DefaultListener();
    static final IPageSelector defaultSelector = new DefaultPageSelector();
    static final Logger log = LoggerFactory.getLogger(AbstractCrawler.class);
    private boolean broken = false;

    protected abstract IPageSelector getSelector();

    public abstract ThreadPoolExecutor getThreadPool();

    public Listener getListener() {
        return defaultListener;
    }

    protected CrawlerData crawlDetail(EntryData entry) {
        CrawlerData data = this.doCrawlData(entry);
        return data;
    }

    protected void writeErrorLog(String status, String url, String reason) {
        // System.out.println(String.format("crawl -  status : %s , url : %s , reason : %s", status, url, reason));
    }

    protected abstract CrawlerData doCrawlData(EntryData entry);

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

    protected abstract void handleCrawlerData(CrawlerData crawlerData, SiteEntry siteEntry, EntryData pageEntry);

    protected void handlePageData(CrawlerData crawlerData, SiteEntry siteEntry, EntryData pageEntry) {

    }

    protected abstract boolean isPageEnd(CrawlerData data, int num);

    protected EntryData genPageEntry(SiteEntry siteEntry, int num) {
        if (this.needPageCut())
            return this.generatePageEntry(siteEntry, num);
        return siteEntry;
    }

    protected abstract EntryData generatePageEntry(SiteEntry siteEntry, int num);

    protected CrawlerDom createCrawlerDom(CrawlerData data) {
        JsoupCrawlerDomImpl dom = new JsoupCrawlerDomImpl();
        dom.parse(data.getHtml());
        return dom;
    }

    @Deprecated
    protected final void execute(EntryData entry) {
        this.handleCrawlerData(this.crawlDetail(entry), null, null);
    }

    protected boolean isNotOverMax(int num, int max) {
        if (max == -1)
            return true;
        if (num > max)
            return false;
        return true;
    }

    protected boolean needPageCut() {
        return true;
    }

    protected final boolean goOn(CrawlerData crawlerData, int num, int max) {
        if (this.broken)
            return false;
        return this.needPageCut() && this.isNotOverMax(num, max) && !this.isPageEnd(crawlerData, num);
    }

    protected boolean checkUrl(String url) {
        return false;
    }

    protected void cacheUrl(String url) {
    }

    protected boolean needSiteCheck() {
        return false;
    }

    protected boolean needPageCheck() {
        return false;
    }

    protected boolean needDetailCheck() {
        return true;
    }

    protected void pageExecute(SiteEntry siteEntry) {
        if (this.needSiteCheck() && this.checkUrl(siteEntry.getUrl())) {
            log.warn("Site entry is downloaded - {}", siteEntry.getUrl());
            return;
        }
        CrawlerData crawlerData = null;
        int num = siteEntry.getPageStart();
        int max = siteEntry.getPageEnd();
        if (num < 1)
            num = 1;
        this.getListener().siteEnter(siteEntry);
        do {
            EntryData pEntry = this.genPageEntry(siteEntry, num);
            if (pEntry == null)
                break;
            if (this.needPageCheck() && this.checkUrl(pEntry.getUrl())) {
                log.warn("Page entry is downloaded - {}", pEntry.getUrl());
                num++;
                continue;
            }
            this.getListener().pageEnter(pEntry);
            crawlerData = this.doCrawlData(pEntry);
            //状态码为200返回的数据
            if (crawlerData != null) {
                this.pageExecute(crawlerData, siteEntry, pEntry);
                this.handlePageData(crawlerData, siteEntry, pEntry);
                if (this.needPageCheck())
                    this.cacheUrl(pEntry.getUrl());
            } else {
                this.broken = true;
                //不忽略掉非200状态的请求，下次就不请求
                if (this.needPageCheck() && !this.ignoreErrorStatusData())
                    this.cacheUrl(pEntry.getUrl());
            }
            this.getListener().pageEnd(pEntry);
            num++;
        } while (this.goOn(crawlerData, num, max));
        if (this.needSiteCheck())
            this.cacheUrl(siteEntry.getUrl());
        this.getListener().siteEnd(siteEntry);
    }

    public void exec(SiteEntry siteEntry) {
        try {
            this.pageExecute(siteEntry);
        } catch (NoMorePageException e) {
            log.error("No more page to crawl!");
        }
    }

    protected void pageExecute(CrawlerData crawlerData, SiteEntry siteEntry,
                               EntryData pEntry) {
        this.doPageExecute(crawlerData, siteEntry, pEntry);
    }

    protected void onPageExecute(PageData pageData) {

    }

    protected void doPageExecute(CrawlerData crawlerData, SiteEntry siteEntry, EntryData pEntry) {
        crawlerData.setDom(this.createCrawlerDom(crawlerData));
        PageData pageData = this.fetchPageEntries(siteEntry, crawlerData);
        this.onPageExecute(pageData);
        //没有page可抓取，已经结束
        if (pageData == null || CollectionUtils.isEmpty(pageData.getEntries())) {
            return;
        }
        for (EntryData entryData : pageData.getEntries()) {
            this.handleDetail(entryData, siteEntry, pEntry);
        }
    }

    protected void handleDetail(EntryData itemEntry, SiteEntry siteEntry, EntryData pageEntry) {
        this.doHandleDetail(itemEntry, siteEntry, pageEntry);
    }

    protected void doHandleDetail(EntryData itemEntry, SiteEntry siteEntry, EntryData pageEntry) {
        if (this.needDetailCheck() && this.checkUrl(itemEntry.getUrl())) {
            //log.warn("Detail entry is downloaded - {}", itemEntry.getUrl());
            return;
        }
        this.getListener().detailEnter(itemEntry);
        CrawlerData crawlerData = this.doCrawlData(itemEntry, siteEntry.getContentExpress());
        this.getListener().detailEnd(crawlerData);
        if (crawlerData != null) {
            this.handleCrawlerData(crawlerData, siteEntry, pageEntry);
            if (this.needDetailCheck())
                this.cacheUrl(itemEntry.getUrl());
        } else if (this.needDetailCheck() && !ignoreErrorStatusData()) {
            this.cacheUrl(itemEntry.getUrl());
        }
        if (crawlerData == null) {
            this.writeErrorLog("-1", itemEntry.getUrl(), "--- unkown");
        } else if (crawlerData.getStatus() != 200) {
            this.writeErrorLog(String.valueOf(crawlerData.getStatus()), itemEntry.getUrl(), crawlerData.getPageType());
        }
    }

    protected boolean ignoreErrorStatusData() {
        return false;
    }

    protected PageData fetchPageEntries(EntryData entry, CrawlerData crawlerData) {
        PageData pageData = new PageData();
        pageData.setUrl(entry.getUrl());
        pageData.setTitle(entry.getTitle());
        pageData.setText(crawlerData.getHtml());
        IPageSelector curSelector = this.getSelector();
        if (curSelector == null)
            curSelector = defaultSelector;
        pageData.setEntries(curSelector.selectPageEntries(crawlerData.getDom(), entry));
        return pageData;
    }

    public void destroy() {

    }
}
