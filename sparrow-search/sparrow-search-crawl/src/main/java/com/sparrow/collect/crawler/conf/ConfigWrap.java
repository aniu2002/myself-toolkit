package com.sparrow.collect.crawler.conf;

import com.sparrow.collect.crawler.conf.format.FormatConfig;
import com.sparrow.collect.crawler.conf.pool.PoolConfig;
import com.sparrow.collect.crawler.conf.site.PageConfig;
import com.sparrow.collect.crawler.conf.site.SelectorConfig;
import com.sparrow.collect.crawler.conf.site.SiteConfig;
import com.sparrow.collect.crawler.conf.site.SiteUrl;
import com.sparrow.collect.crawler.conf.store.StoreConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/12/6.
 */
public class ConfigWrap {
    /**
     * 列表页-详情页 选择配置
     */
    private SelectorConfig selector;
    /**
     * 线程池配置
     */
    private PoolConfig pool;
    /**
     * 站点基础配置
     */
    private SiteConfig site;
    /**
     * 抓取分页配置
     */
    private PageConfig page;
    /**
     * 数据格式化配置 方便存储
     */
    private FormatConfig format;
    /**
     * 抓取数据存储配置
     */
    private StoreConfig store;
    /**
     * 抓取的网站urls
     */
    private List<SiteUrl> entries;
    /**
     * 是否需要url过滤(排重bloom)
     */
    private boolean urlFilter;
    /**
     * http抓取尝试次数
     */
    private int retryNum = 2;
    /**
     * 是否单线程抓取
     */
    private boolean singleThread = false;

    public boolean isUrlFilter() {
        return urlFilter;
    }

    public void setUrlFilter(boolean urlFilter) {
        this.urlFilter = urlFilter;
    }

    public int getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(int retryNum) {
        this.retryNum = retryNum;
    }

    public boolean isSingleThread() {
        return singleThread;
    }

    public void setSingleThread(boolean singleThread) {
        this.singleThread = singleThread;
    }

    public List<SiteUrl> getEntries() {
        return entries;
    }

    public void setEntries(List<SiteUrl> entries) {
        this.entries = entries;
    }

    public PageConfig getPage() {
        return page;
    }

    public void setPage(PageConfig page) {
        this.page = page;
    }

    public PoolConfig getPool() {
        return pool;
    }

    public void setPool(PoolConfig pool) {
        this.pool = pool;
    }

    public StoreConfig getStore() {
        return store;
    }

    public void setStore(StoreConfig store) {
        this.store = store;
    }

    public SiteConfig getSite() {
        return site;
    }

    public void setSite(SiteConfig site) {
        this.site = site;
    }

    public SelectorConfig getSelector() {
        return selector;
    }

    public void setSelector(SelectorConfig selector) {
        this.selector = selector;
    }

    public CrawlerConfig crawlerConfig() {
        CrawlerConfig config = new CrawlerConfig();
        if (this.store != null) {
            config.setStore(this.store);
        }
        if (this.selector != null) {
            config.configure(this.selector);
        }
        if (this.pool != null) {
            config.setPool(this.pool);
        }
        if (this.site != null) {
            config.setSite(this.site);
        }
        if (this.page != null) {
            config.setPage(this.page);
        }
        if (this.format != null) {
            config.setFormat(this.format);
            config.setFetchType(this.format.getFormatter());
        }
        config.setSiteUrls(this.getEntries());
        config.setSingleThread(this.isSingleThread());
        config.setUrlFilter(this.isUrlFilter());
        config.setRetryNum(this.getRetryNum());
        return config;
    }

    public FormatConfig getFormat() {
        return format;
    }

    public void setFormat(FormatConfig format) {
        this.format = format;
    }
}
