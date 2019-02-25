package com.sparrow.collect.crawler;


import com.sparrow.collect.crawler.conf.CrawlerConfig;
import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.listener.ListenerSupport;
import com.sparrow.collect.index.format.DataFormat;
import com.sparrow.collect.index.format.DataFormatFactory;

/**
 * Created by Administrator on 2016/11/29.
 */
public class ConfiguredFormatCrawler extends ConfiguredCrawler {
    private final DataFormat dataFormat;

    public ConfiguredFormatCrawler(CrawlerConfig crawlerConfig) {
        this(crawlerConfig, null);
    }

    public ConfiguredFormatCrawler(CrawlerConfig crawlerConfig, ListenerSupport listenerSupport) {
        super(crawlerConfig, listenerSupport);
        this.dataFormat = DataFormatFactory.dataFormat(crawlerConfig.getFormat());
    }

    @Override
    protected Object format(CrawlerData data, SiteEntry siteEntry, EntryData pageEntry) {
        return this.dataFormat.format(data, siteEntry, pageEntry);
    }
}
