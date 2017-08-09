package com.sparrow.collect.crawler.listener;

import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.conf.CrawlerConfig;
import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.data.CrawlerData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/5.
 */
public class ListenerSupport implements Listener {
    private List<Listener> listeners = new ArrayList<Listener>();

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void crawlerEnter(CrawlerConfig crawlerConfig) {
        for (Listener listener : this.listeners) {
            listener.crawlerEnter(crawlerConfig);
        }
    }

    @Override
    public void crawlerEnd(CrawlerConfig crawlerConfig) {
        for (Listener listener : this.listeners) {
            listener.crawlerEnd(crawlerConfig);
        }
    }

    @Override
    public void siteEnter(SiteEntry siteEntry) {
        for (Listener listener : this.listeners) {
            listener.siteEnter(siteEntry);
        }
    }

    @Override
    public void siteEnd(SiteEntry siteEntry) {
        for (Listener listener : this.listeners) {
            listener.siteEnd(siteEntry);
        }
    }

    @Override
    public void pageEnter(EntryData entryData) {
        for (Listener listener : this.listeners) {
            listener.pageEnter(entryData);
        }
    }

    @Override
    public void pageEnd(EntryData entryData) {
        for (Listener listener : this.listeners) {
            listener.pageEnd(entryData);
        }
    }

    @Override
    public void detailEnter(EntryData itemEntry) {
        for (Listener listener : this.listeners) {
            listener.detailEnter(itemEntry);
        }
    }

    @Override
    public void detailEnd(CrawlerData crawlerData) {
        for (Listener listener : this.listeners) {
            listener.detailEnd(crawlerData);
        }
    }


    @Override
    public void formatEnter(CrawlerData crawlerData) {
        for (Listener listener : this.listeners) {
            listener.formatEnter(crawlerData);
        }
    }

    @Override
    public void formatEnd(CrawlerData crawlerData, Object data) {
        for (Listener listener : this.listeners) {
            listener.formatEnd(crawlerData, data);
        }
    }

    @Override
    public void storeEnter(CrawlerData crawlerData, Object data) {
        for (Listener listener : this.listeners) {
            listener.storeEnter(crawlerData, data);
        }
    }

    @Override
    public void storeEnd(CrawlerData crawlerData, Object data, int effects) {
        for (Listener listener : this.listeners) {
            listener.storeEnd(crawlerData, data, effects);
        }
    }
}
