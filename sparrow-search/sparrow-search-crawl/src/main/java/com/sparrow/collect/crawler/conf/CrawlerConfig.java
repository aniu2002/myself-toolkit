package com.sparrow.collect.crawler.conf;

import com.sparrow.collect.crawler.conf.format.FormatConfig;
import com.sparrow.collect.crawler.conf.pool.PoolConfig;
import com.sparrow.collect.crawler.conf.site.PageConfig;
import com.sparrow.collect.crawler.conf.site.SelectorConfig;
import com.sparrow.collect.crawler.conf.site.SiteConfig;
import com.sparrow.collect.crawler.conf.site.SiteUrl;
import com.sparrow.collect.crawler.conf.store.StoreConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/2.
 */
public class CrawlerConfig extends SelectorConfig {
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
    private List<SiteUrl> siteUrls;
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
    /**
     * 数据存储时，获取类型 ，是 html ，content 还是format的pojo方式
     */
    private String fetchType;

    public String getFetchType() {
        return fetchType;
    }

    public void setFetchType(String fetchType) {
        this.fetchType = fetchType;
    }

    public boolean isSingleThread() {
        return singleThread;
    }

    void setSingleThread(boolean singleThread) {
        this.singleThread = singleThread;
    }

    public int getRetryNum() {
        return retryNum;
    }

    void setRetryNum(int retryNum) {
        this.retryNum = retryNum;
    }

    public boolean isUrlFilter() {
        return urlFilter;
    }

    void setUrlFilter(boolean urlFilter) {
        this.urlFilter = urlFilter;
    }

    public void addSiteUrl(String siteTitle, String siteUrl) {
        if (StringUtils.isEmpty(siteUrl)) return;
        this.addSiteUrl(new SiteUrl(siteTitle, siteUrl));
    }

    void setSiteUrls(List<SiteUrl> siteUrls) {
        this.siteUrls = siteUrls;
    }

    public void addSiteUrl(SiteUrl siteUrl) {
        if (siteUrl == null) return;
        if (this.siteUrls == null)
            this.siteUrls = new ArrayList<SiteUrl>();
        this.siteUrls.add(siteUrl);
    }

    public void addSiteUrl(String siteTitle, String siteUrl, String pageExpress, int pageStart, int pageEnd) {
        if (StringUtils.isEmpty(siteUrl)) return;
        this.addSiteUrl(new SiteUrl(siteTitle, siteUrl, pageExpress, pageStart, pageEnd));
    }

    public void addSiteUrl(String siteTitle, String siteUrl, String pageExpress) {
        this.addSiteUrl(siteTitle, siteUrl, pageExpress, 1, -1);
    }

    public PoolConfig getPool() {
        return pool;
    }

    void setPool(PoolConfig pool) {
        this.pool = pool;
    }

    public void configurePool(PoolConfig pool) {
        if (pool == null)
            return;
        if (this.pool == null)
            this.pool = new PoolConfig();
        this.pool.configure(pool);
    }

    public SiteConfig getSite() {
        return site;
    }

    void setSite(SiteConfig site) {
        this.site = site;
    }

    public void configureSite(SiteConfig site) {
        if (site == null)
            return;
        if (this.site == null)
            this.site = new SiteConfig();
        this.site.configure(site);
    }

    public PageConfig getPage() {
        return page;
    }

    void setPage(PageConfig page) {
        this.page = page;
    }

    public void configurePage(PageConfig page) {
        if (page == null)
            return;
        if (this.page == null)
            this.page = new PageConfig();
        this.page.configure(page);
    }

    public FormatConfig getFormat() {
        return format;
    }

    void setFormat(FormatConfig format) {
        this.format = format;
    }

    public void configureFormat(FormatConfig format) {
        if (format == null)
            return;
        if (this.format == null)
            this.format = new FormatConfig();
        this.format.configure(format);
    }

    public StoreConfig getStore() {
        return store;
    }

    void setStore(StoreConfig store) {
        this.store = store;
    }

    public void configureStore(StoreConfig store) {
        if (store == null)
            return;
        if (this.store == null)
            this.store = new StoreConfig();
        this.store.configure(store);
    }

    public String getTempDir(String defaultDir) {
        if (this.store != null)
            return this.store.getTempDir(defaultDir);
        return defaultDir;
    }

    public List<SiteUrl> getSiteUrls() {
        return siteUrls;
    }

    public void checkConfig(){
        if (this.page == null)
            this.page = new PageConfig();
        if (this.store == null)
            this.store = new StoreConfig();
        if (this.format == null)
            this.format = new FormatConfig();
        if (this.site == null)
            this.site = new SiteConfig();
        if (this.pool == null)
            this.pool = new PoolConfig();
    }
}
