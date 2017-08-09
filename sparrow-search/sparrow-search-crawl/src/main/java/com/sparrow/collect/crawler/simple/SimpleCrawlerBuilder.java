package com.sparrow.collect.crawler.simple;

import com.sparrow.collect.crawler.conf.ConfigWrap;
import com.sparrow.collect.crawler.conf.CrawlerConfig;
import com.sparrow.collect.crawler.conf.format.FormatConfig;
import com.sparrow.collect.crawler.conf.pool.PoolConfig;
import com.sparrow.collect.crawler.conf.site.PageConfig;
import com.sparrow.collect.crawler.conf.site.SelectorConfig;
import com.sparrow.collect.crawler.conf.site.SiteConfig;
import com.sparrow.collect.crawler.conf.store.StoreConfig;
import com.sparrow.collect.crawler.listener.CrawlerCountListener;
import com.sparrow.collect.crawler.listener.Listener;
import com.sparrow.collect.crawler.listener.ListenerSupport;

/**
 * Created by Administrator on 2016/12/5.
 */
public class SimpleCrawlerBuilder {
    private CrawlerConfig crawlerConfig;
    private ListenerSupport listenerSupport;

    public SimpleCrawlerBuilder() {
        this(null, null);
    }

    public SimpleCrawlerBuilder(ConfigWrap configWrap) {
        this(configWrap.crawlerConfig(), null);
    }

    public SimpleCrawlerBuilder(CrawlerConfig crawlerConfig, ListenerSupport listenerSupport) {
        this.crawlerConfig = (crawlerConfig == null ? new CrawlerConfig() : crawlerConfig);
        this.listenerSupport = (listenerSupport == null ? new ListenerSupport() : listenerSupport);
    }

    public SimpleCrawlerBuilder configureSelectorConfig(SelectorConfig selectorConfig) {
        this.crawlerConfig.configure(selectorConfig);
        return this;
    }

    public SimpleCrawlerBuilder configureSelectorExpress(String itemExpress, String itemTitleExpress, String detailExpress) {
        this.crawlerConfig.addItemExpress(itemExpress);
        this.crawlerConfig.setNameExpress(itemTitleExpress);
        this.crawlerConfig.addUrlExpress(detailExpress);
        return this;
    }

    public SimpleCrawlerBuilder configurePool(PoolConfig poolConfig) {
        this.crawlerConfig.configurePool(poolConfig);
        return this;
    }

    public SimpleCrawlerBuilder configureSiteUrl(String title, String url) {
        this.crawlerConfig.addSiteUrl(title, url);
        return this;
    }

    // target 1=file 2=db
    public SimpleCrawlerBuilder configureStore(StoreConfig storeConfig) {
        this.crawlerConfig.configureStore(storeConfig);
        return this;
    }


    public SimpleCrawlerBuilder configurePage(PageConfig pageConfig) {
        this.crawlerConfig.configurePage(pageConfig);
        return this;
    }

    public SimpleCrawlerBuilder configureSite(SiteConfig siteConfig) {
        this.crawlerConfig.configureSite(siteConfig);
        return this;
    }

    public SimpleCrawlerBuilder configureFormat(FormatConfig formatConfig) {
        this.crawlerConfig.configureFormat(formatConfig);
        return this;
    }

    public SimpleCrawlerBuilder configureFormatField(String name, String express) {
        this.crawlerConfig.getFormat().addFieldMap(name, express, 1);
        return this;
    }

    public SimpleCrawlerBuilder configureFormatField(String name, String express, int type) {
        this.crawlerConfig.getFormat().addFieldMap(name, express, type);
        return this;
    }

    public SimpleCrawlerBuilder configureListener(Listener listener) {
        this.listenerSupport.addListener(listener);
        return this;
    }

    public SimpleCrawler build() {
        this.listenerSupport.addListener(new CrawlerCountListener());
        return new SimpleCrawler(this.crawlerConfig, this.listenerSupport);
    }
}
