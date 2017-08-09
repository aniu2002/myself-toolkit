package com.sparrow.collect.crawler.listener;

import com.sparrow.collect.crawler.conf.CrawlerConfig;
import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.SiteEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2016/12/5.
 */
public class DefaultListener implements Listener {
    static final Logger logger = LoggerFactory.getLogger(DefaultListener.class);
    final AtomicInteger crawlerCounter = new AtomicInteger(0);
    final AtomicInteger siteCounter = new AtomicInteger(0);
    final AtomicInteger pageCounter = new AtomicInteger(0);
    final AtomicInteger detailCounter = new AtomicInteger(0);
    final AtomicInteger formatCounter = new AtomicInteger(0);
    final AtomicInteger storeCounter = new AtomicInteger(0);

    @Override
    public void crawlerEnter(CrawlerConfig crawlerConfig) {
        crawlerCounter.incrementAndGet();
        if (logger.isInfoEnabled())
            logger.info("进入抓取站点: {}-{}", crawlerConfig.getSite().getId(), crawlerConfig.getSite().getName());
    }

    @Override
    public void crawlerEnd(CrawlerConfig crawlerConfig) {
        if (logger.isInfoEnabled())
            logger.info("退出抓取站点: {}-{}，完成 site={}个，page={}个，detail={}个",
                    new Object[]{
                            crawlerConfig.getSite().getId(),
                            crawlerConfig.getSite().getName(),
                            siteCounter.get(),
                            pageCounter.get(),
                            detailCounter.get()
                    });
    }

    @Override
    public void siteEnter(SiteEntry siteEntry) {
        if (logger.isDebugEnabled())
            logger.debug("开始抓取site入口url: {}-{} ", siteEntry.getTitle(), siteEntry.getUrl());
    }

    @Override
    public void siteEnd(SiteEntry siteEntry) {
        siteCounter.incrementAndGet();
        if (logger.isDebugEnabled())
            logger.debug("完成抓取site入口url: {}-{}，完成 site={}个，page={}个，detail={}个",
                    new Object[]{
                            siteEntry.getTitle(),
                            siteEntry.getUrl(),
                            siteCounter.get(),
                            pageCounter.get(),
                            detailCounter.get()
                    });
    }

    @Override
    public void pageEnter(EntryData entryData) {
        if (logger.isDebugEnabled())
            logger.debug("开始抓取page页面: {}-{}", entryData.getTitle(), entryData.getUrl());
    }

    @Override
    public void pageEnd(EntryData entryData) {
        pageCounter.incrementAndGet();
        if (logger.isDebugEnabled())
            logger.debug("完成抓取page页面: {}-{}", entryData.getTitle(), entryData.getUrl());
    }

    @Override
    public void detailEnter(EntryData itemEntry) {
        if (logger.isDebugEnabled())
            logger.debug("开始抓取detail页面: {}-{}", itemEntry.getTitle(), itemEntry.getUrl());
    }

    @Override
    public void detailEnd(CrawlerData crawlerData) {
        detailCounter.incrementAndGet();
        if (logger.isDebugEnabled()) {
            if (crawlerData != null)
                logger.debug("完成抓取detail页面:{} - {}", crawlerData.getTitle(), crawlerData.getUrl());
            else
                logger.debug("完成抓取detail页面");
        }
    }


    @Override
    public void formatEnter(CrawlerData crawlerData) {
        if (logger.isDebugEnabled())
            logger.debug("开始数据格式化: {} - {}", crawlerData.getUrl(), crawlerData);
    }

    @Override
    public void formatEnd(CrawlerData crawlerData, Object data) {
        formatCounter.incrementAndGet();
        if (logger.isDebugEnabled())
            logger.debug("完成数据格式化: {} - {}", crawlerData.getUrl(), data.getClass().getName());
    }

    @Override
    public void storeEnter(CrawlerData crawlerData, Object data) {
        if (logger.isDebugEnabled())
            logger.debug("开始数据存储: {} - {}", crawlerData.getUrl(), data.getClass().getName());
    }

    @Override
    public void storeEnd(CrawlerData crawlerData, Object data, int effects) {
        storeCounter.incrementAndGet();
        if (logger.isDebugEnabled())
            logger.debug("完成数据存储: {} - {}", crawlerData.getUrl(), data.getClass().getName());
    }
}
