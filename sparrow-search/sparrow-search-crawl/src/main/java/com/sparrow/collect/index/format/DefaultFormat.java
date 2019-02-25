package com.sparrow.collect.index.format;

import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.SiteEntry;

/**
 * Project Name: sparrow-egg
 * Package Name: com.sparrow.collect.format
 * Author : YZC
 * Date: 2016/12/13
 * Time: 15:15
 */
public class DefaultFormat implements DataFormat {
    @Override
    public Object format(CrawlerData crawlerData, SiteEntry siteEntry, EntryData pageEntry) {
        return crawlerData;
    }
}
