package com.sparrow.collect.task.site;

import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.httpclient.CrawlKit;
import com.sparrow.collect.crawler.httpclient.HttpResp;
import com.sparrow.collect.crawler.selector.IPageSelector;
import com.sparrow.collect.crawler.selector.MultiExpressSelector;
import com.sparrow.collect.task.AbstractTask;
import com.sparrow.collect.task.Context;
import com.sparrow.collect.utils.PathResolver;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SiteTask extends AbstractTask {

    void crawl(SiteCrawlerEx crawlerEx, String url, String title) {
        SiteEntry entry = new SiteEntry();
        entry.setTitle("RaQ-" + title);
        // entry.setContentExpress(".entry");
        entry.setUrl(url);
        entry.setPageStart(1);
        entry.setPageEnd(1);
        crawlerEx.addCrawlSeed(entry);
    }

    SiteCrawlerEx createCrawler() {
        MultiExpressSelector selector = new MultiExpressSelector();
        selector.addSelector(new SiteSelector(IPageSelector.HREF));
        selector.addSelector(new SiteSelector(IPageSelector.IMG));
        selector.addSelector(new SiteSelector(IPageSelector.CSS));
        selector.addSelector(new SiteSelector(IPageSelector.SCRIPT));

        SiteCrawlerEx crawler = new SiteCrawlerEx(new File(
                "E:\\wiki\\raq"));

        crawler.setSelector(selector);
        crawler.setAcceptList(new String[]{"http://www.raqsoft.com.cn/wp-content"});
        crawler.setIgnoreList(new String[]{"http://www.raqsoft.com.cn/r/r-version",
                "http://www.raqsoft.com.cn",
                "http://esproc.raqsoft.com.cn/",
                "http://www.raqsoft.com.cn/about",
                "http://www.raqsoft.com.cn/about/",
                "http://www.raqsoft.com.cn/about/#aboutme",
                "http://www.raqsoft.com.cn/about/#cooperative",
                "http://www.raqsoft.com.cn/about/#blue",
                "http://www.raqsoft.com.cn/about/#honor",
                "http://www.raqsoft.com.cn/p",
                "http://www.raqsoft.com.cn/p-application",
                "http://www.raqsoft.com.cn/jsq/jsq-vs-1",
                "http://www.raqsoft.com.cn/download-jsq",
                "http://www.raqsoft.com.cn/r-function-query",
                "http://bbs.raqsoft.com.cn/",
                "http://www.peixun.net/view/1048.html",
                "http://blog.raqsoft.com.cn/",
                "http://www.raqsoft.com.cn/jiangs-datatalk",
                "http://www.raqsoft.com.cn/download/download-jsbb",
                "http://www.hellobi.com",
                "http://www.zetyun.com/",
                "https://bugclose.com/",
                "http://www.51cto.com/raqsoft/",
                "http://www.raqsoft.com",
                "http://www.raqsoft.com.cn/archives/category/news",
                "http://www.raqsoft.com.cn/jsq/wp-content/uploads",
                "http://escalc.raqsoft.com.cn/"});

        return crawler;
    }

    public void execute(Context ctx) {
        SiteCrawlerEx crawlerEx = this.createCrawler();
        HttpResp resp = CrawlKit.KIT.getHtml("http://www.raqsoft.com.cn/r/r-version");
        if (resp.getStatus() != 200) {
            System.out.println(resp.getHtml());
            return;
        }
        Document dom = Jsoup.parse(resp.getHtml());
        Elements elements = dom.select(".col1>a");
        Iterator<Element> iterator = elements.iterator();
        Set<String> set = new HashSet();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            String href = element.attr("href");
            if (StringUtils.isEmpty(href))
                continue;
            int idx = href.indexOf('#');
            if (idx != -1)
                href = href.substring(0, idx);
            if (set.add(href)) {
                String title = PathResolver.getFileName(href);
                //System.out.println(href);
                this.crawl(crawlerEx, href, title);
            }
        }
        SiteEntry entry = new SiteEntry();
        entry.setTitle("RaQ");
        entry.setUrl("http://www.raqsoft.com.cn");
        entry.setPageEnd(0);
        entry.setPageStart(0);
        //entry.setContentExpress(".entry");
        crawlerEx.execXl(entry);
        crawlerEx.destroy();
    }
}
