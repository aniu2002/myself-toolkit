package com.sparrow.collect.index.format;

import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.SiteEntry;

/**
 * Created by Administrator on 2016/12/2.
 */
public interface DataFormat {
    Object format(CrawlerData crawlerData, SiteEntry siteEntry, EntryData pageEntry);
}
