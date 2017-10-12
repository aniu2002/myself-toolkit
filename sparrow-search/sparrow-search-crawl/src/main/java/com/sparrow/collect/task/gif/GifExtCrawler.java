package com.sparrow.collect.task.gif;

import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.dom.CrawlerDom;
import com.sparrow.collect.crawler.dom.CrawlerNode;
import com.sparrow.collect.utils.PathResolver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class GifExtCrawler extends GifCrawler {
    static final Logger log = LoggerFactory.getLogger(GifExtCrawler.class);

    public GifExtCrawler(File rootDir) {
        super(rootDir);
    }

    @Override
    protected EntryData generatePageEntry(SiteEntry siteEntry, int num) {
        String url = siteEntry.getUrl();
        EntryData entryData = new EntryData();
        String realUrl = num == 1 ? url : url + "/page/" + String.valueOf(num);
        entryData.setTitle(siteEntry.getTitle());
        entryData.setUrl(realUrl);
        return entryData;
    }

    boolean isImgNode(CrawlerNode node) {
        return StringUtils.equalsIgnoreCase("img", node.nodeName());
    }

    protected void handleCrawlerData(CrawlerData crawlerData,
                                     SiteEntry siteEntry,
                                     EntryData pageEntry) {
        if (StringUtils.isEmpty(crawlerData.getHtml()))
            return;
        CrawlerDom dom = this.createCrawlerDom(crawlerData);
        crawlerData.setDom(dom);
        List<CrawlerNode> items = dom.selectNodes(this.getGifItemSelect());
        if (items == null || items.size() < 2)
            return;
        items.remove(0);
        this.handleOneGif(items.iterator(), crawlerData);
    }

    void handleOneGif(Iterator<CrawlerNode> iterator, CrawlerData crawlerData) {
        CacheWrap wrap = new CacheWrap(this, crawlerData);
        while (iterator.hasNext()) {
            CrawlerNode imgNode = iterator.next();
            CrawlerNode parentNode = imgNode.parent();
            int size = parentNode.childSize();
            CrawlerNode fNode = imgNode;
            if (!this.isImgNode(fNode)) {
                continue;
            }
            String url = fNode.attr("src");
            String relativePath = "img" + File.separatorChar +
                    PathResolver.getFileName(url);
            File f = new File(this.rootDir, relativePath);
            wrap.appendIcon(relativePath, url);
            if (size == 1) {
                wrap.setTitle(parentNode.next().text());
            } else if (size > 2) {
                wrap.setTitle(parentNode.childNode(2).text());
            }
            num.incrementAndGet();
            if (this.checkUrl(url) && f.length() > 0) {
                log.warn(" -- URL skip download - {}", url);
                continue;
            }
            this.downFile(url, f);
            this.cacheUrl(url);
        }
        wrap.end();
    }


    String correctTitle(String title) {
        if (StringUtils.isNotEmpty(title)) {
            int idx = title.indexOf('：');
            if (idx != -1)
                return title.substring(idx + 1);
            else
                return title;
        }
        return title;
    }

    @Override
    protected boolean isPageEnd(CrawlerData data, int num) {
        if (data != null && data.getStatus() != 200)
            return true;
        if (num > 100)
            return true;
        return false;
    }

    protected int sleepSeconds() {
        return 5;
    }

    SiteEntry generateEntry(SiteEntry siteEntry) {
        SiteEntry se = new SiteEntry();
        se.setPageExpress(siteEntry.getPageExpress());
        se.setContentExpress(siteEntry.getContentExpress());
        se.setSiteId(siteEntry.getSiteId());
        se.setSiteName(siteEntry.getSiteName());
        se.setPageType(siteEntry.getPageType());
        se.setRelativePath(siteEntry.getRelativePath());
        se.setPageStart(siteEntry.getPageStart());
        se.setPageEnd(siteEntry.getPageEnd());
        return se;
    }

    @Override
    public void exec(final SiteEntry siteEntry) {
        this.startCrawl(siteEntry);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
