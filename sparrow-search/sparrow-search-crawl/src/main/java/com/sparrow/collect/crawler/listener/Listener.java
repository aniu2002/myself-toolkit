package com.sparrow.collect.crawler.listener;

import com.sparrow.collect.crawler.conf.CrawlerConfig;
import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.SiteEntry;

/**
 * Created by Administrator on 2016/12/5.
 */
public interface Listener {
    void crawlerEnter(CrawlerConfig crawlerConfig);

    void siteEnter(SiteEntry siteEntry);

    void siteEnd(SiteEntry siteEntry);

    void pageEnter(EntryData entryData);

    void pageEnd(EntryData entryData);

    void detailEnter(EntryData itemEntry);

    void detailEnd(CrawlerData crawlerData);

    void formatEnter(CrawlerData crawlerData);

    void formatEnd(CrawlerData crawlerData, Object data);

    void storeEnter(CrawlerData crawlerData, Object data);

    void storeEnd(CrawlerData crawlerData, Object data, int effects);

    void crawlerEnd(CrawlerConfig crawlerConfig);
}
