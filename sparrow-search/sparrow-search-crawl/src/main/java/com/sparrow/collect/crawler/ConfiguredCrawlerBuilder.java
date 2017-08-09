package com.sparrow.collect.crawler;

import com.sparrow.collect.crawler.conf.ConfigWrap;
import com.sparrow.collect.crawler.conf.CrawlerConfig;
import com.sparrow.collect.crawler.conf.format.FormatConfig;
import com.sparrow.collect.crawler.conf.pool.PoolConfig;
import com.sparrow.collect.crawler.conf.site.PageConfig;
import com.sparrow.collect.crawler.conf.site.SelectorConfig;
import com.sparrow.collect.crawler.conf.site.SiteConfig;
import com.sparrow.collect.crawler.conf.site.SiteUrl;
import com.sparrow.collect.crawler.conf.store.StoreConfig;
import com.sparrow.collect.crawler.httpclient.CrawlHttp;
import com.sparrow.collect.crawler.httpclient.CrawlKit;
import com.sparrow.collect.crawler.httpclient.HttpResp;
import com.sparrow.collect.crawler.listener.CrawlerCountListener;
import com.sparrow.collect.crawler.listener.Listener;
import com.sparrow.collect.crawler.listener.ListenerSupport;
import com.sparrow.collect.utils.CollectionUtils;
import com.sparrow.collect.utils.FileIOUtil;
import com.sparrow.collect.utils.JsonMapper;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/5.
 */
public class ConfiguredCrawlerBuilder {
    private CrawlerConfig crawlerConfig;
    private ListenerSupport listenerSupport;
    private Map<String, String> sitePageExpMap = new HashMap<String, String>();

    public ConfiguredCrawlerBuilder() {
        this(new CrawlerConfig(), null);
    }

    public ConfiguredCrawlerBuilder(ConfigWrap configWrap) {
        this(configWrap.crawlerConfig(), null);
    }

    public ConfiguredCrawlerBuilder(CrawlerConfig crawlerConfig, ListenerSupport listenerSupport) {
        this.crawlerConfig = (crawlerConfig == null ? new CrawlerConfig() : crawlerConfig);
        this.listenerSupport = (listenerSupport == null ? new ListenerSupport() : listenerSupport);
    }

    public ConfiguredCrawlerBuilder configureSitePageExpress(String site, String pageExpress) {
        this.sitePageExpMap.put(site, pageExpress);
        return this;
    }

    public ConfiguredCrawlerBuilder configureSitePageExpress(Map<String, String> map) {
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        Map.Entry<String, String> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            this.sitePageExpMap.put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public ConfiguredCrawlerBuilder configureSelectorConfig(SelectorConfig selectorConfig) {
        this.crawlerConfig.configure(selectorConfig);
        return this;
    }

    public ConfiguredCrawlerBuilder configureSelectorExpress(String itemExpress, String itemTitleExpress, String detailExpress) {
        this.crawlerConfig.addItemExpress(itemExpress);
        this.crawlerConfig.setNameExpress(itemTitleExpress);
        this.crawlerConfig.addUrlExpress(detailExpress);
        return this;
    }

    public ConfiguredCrawlerBuilder configurePool(PoolConfig poolConfig) {
        this.crawlerConfig.configurePool(poolConfig);
        return this;
    }

    public ConfiguredCrawlerBuilder configureSiteUrl(String title, String url) {
        this.crawlerConfig.addSiteUrl(title, url);
        return this;
    }

    // target 1=file 2=db
    public ConfiguredCrawlerBuilder configureStore(StoreConfig storeConfig) {
        this.crawlerConfig.configureStore(storeConfig);
        return this;
    }


    public ConfiguredCrawlerBuilder configurePage(PageConfig pageConfig) {
        this.crawlerConfig.configurePage(pageConfig);
        return this;
    }

    public ConfiguredCrawlerBuilder configureSite(SiteConfig siteConfig) {
        this.crawlerConfig.configureSite(siteConfig);
        return this;
    }

    public ConfiguredCrawlerBuilder configureFormat(FormatConfig formatConfig) {
        this.crawlerConfig.configureFormat(formatConfig);
        return this;
    }

    public ConfiguredCrawlerBuilder configureFormatField(String name, String express) {
        this.crawlerConfig.getFormat().addFieldMap(name, express, 1);
        return this;
    }

    public ConfiguredCrawlerBuilder configureFormatField(String name, String express, int type) {
        this.crawlerConfig.getFormat().addFieldMap(name, express, type);
        return this;
    }

    public ConfiguredCrawlerBuilder configureListener(Listener listener) {
        this.listenerSupport.addListener(listener);
        return this;
    }

    void httpTextScanEntry(String url, List<String> links) {
        HttpResp resp = CrawlKit.KIT.getHtml(url, null, CrawlHttp.headers, "UTF-8", false, 2);
        if (resp.getStatus() == 200) {
            Document document = Jsoup.parse(resp.getHtml());
            for (String express : links) {
                Elements elements = document.select(express);
                Iterator<Element> ite = elements.iterator();
                while (ite.hasNext()) {
                    Element element = ite.next();
                    String title, href;
                    if (element.hasAttr("title"))
                        title = element.attr("title");
                    else
                        title = element.text();
                    href = element.attr("href");
                    String pageExp = this.sitePageExpMap.get(href);
                    this.crawlerConfig.addSiteUrl(title, href, pageExp);
                    System.out.println(String.format(" add site entry - { %s - %s , pageExp=%s}", title, href, pageExp));
                }
            }
        } else
            System.out.println("Site url access return " + resp.getStatus());
    }

    void initSiteEntries() throws IOException {
        if (this.crawlerConfig != null && this.crawlerConfig.getSite() != null) {
            SiteConfig site = this.crawlerConfig.getSite();
            if (StringUtils.isNotEmpty(site.getUrl())
                    && CollectionUtils.isNotEmpty(site.getLinks())) {
                this.httpTextScanEntry(site.getUrl(), site.getLinks());
            } else if (CollectionUtils.isNotEmpty(site.getEntryFiles())) {
                for (String entryFile : site.getEntryFiles()) {
                    String text = FileIOUtil.readString(entryFile);
                    List<SiteUrl> urls = JsonMapper.bean(text, new TypeReference<List<SiteUrl>>() {
                    });
                    for (SiteUrl su : urls) {
                        this.crawlerConfig.addSiteUrl(su);
                        System.out.println(String.format(" Add site entry setting - { %s - %s , pageExp=%s}", su.getTitle(), su.getUrl(), su.getPageExpress()));
                    }
                }
            }
        }

    }

    public ConfiguredCrawler build() {
        this.listenerSupport.addListener(new CrawlerCountListener());
        try {
            this.initSiteEntries();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ConfiguredFormatCrawler(this.crawlerConfig, this.listenerSupport);
    }

    public ConfiguredCrawler buildSimple() {
        this.listenerSupport.addListener(new CrawlerCountListener());
        try {
            this.initSiteEntries();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ConfiguredCrawler(this.crawlerConfig, this.listenerSupport);
    }

    public ConfiguredCrawler buildScope() {
        this.listenerSupport.addListener(new CrawlerCountListener());
        try {
            this.initSiteEntries();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ScopeCrawler(this.crawlerConfig, this.listenerSupport);
    }
}
