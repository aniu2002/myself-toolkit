package com.sparrow.collect.task.gif;

import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.utils.PathResolver;

/**
 * Created by Administrator on 2017/8/2 0002.
 */
public class CacheWrap {
    private GifCrawler gifCrawler;
    private CrawlerData crawlerData;
    private boolean titleFirst = false;
    private boolean iconFirst = false;
    private String title;
    private String imgUrl;
    private StringBuilder sb = new StringBuilder();

    public CacheWrap(GifCrawler gifCrawler, CrawlerData crawlerData) {
        this.gifCrawler = gifCrawler;
        this.crawlerData = crawlerData;
    }

    public void setTitle(String title) {
        if (this.titleFirst) {
            this.writeLog();
        } else if (this.iconFirst) {
            this.title = title;
            this.writeLog();
            return;
        }
        this.title = title;
        this.titleFirst = true;
    }

    public void appendIcon(String str) {
        this.appendIcon(str, null);
    }

    public void appendIcon(String str, String imgUrl) {
        this.imgUrl = imgUrl;
        sb.append(",").append(str);
        this.iconFirst = true;
    }

    void clear() {
        sb.delete(0, sb.length());
        this.titleFirst = false;
        this.title = null;
        this.imgUrl = null;
        this.iconFirst = false;
    }

    public void end() {
        if (this.titleFirst || this.iconFirst)
            this.writeLog();
    }

    public void writeLog() {
        // if (!gifCrawler.checkUrl(this.imgUrl))
        gifCrawler.writeLog(gifCrawler.correctTitle(title), sb.toString(),
                PathResolver.getFilePath(this.imgUrl), crawlerData);
        this.clear();
    }
}
