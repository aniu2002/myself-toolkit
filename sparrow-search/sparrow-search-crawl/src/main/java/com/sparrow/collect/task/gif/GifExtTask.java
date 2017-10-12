package com.sparrow.collect.task.gif;

import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.selector.AbstractPageSelector;
import com.sparrow.collect.task.AbstractTask;
import com.sparrow.collect.task.Context;

import java.io.File;

public class GifExtTask extends AbstractTask {

    public void execute(Context ctx) {
        AbstractPageSelector selector = new GifExtSelector();
        selector.setPageItemSelectExpress(".article>h1>a");
        selector.setUrlSelectExpress("href");

        GifCrawler crawler = new GifExtCrawler(new File(
                "D:\\fanhao\\extract2"));
        crawler.setPageCheck(true);
        crawler.setSiteCheck(false);
        crawler.setDetailCheck(true);
        crawler.setUseProxy(false);
       // crawler.setGifItemSelect(".content>p:gt(3)");
        crawler.setGifItemSelect("p>img");
        crawler.setSelector(selector);

        SiteEntry entry = new SiteEntry();
        entry.setTitle("2017");
        entry.setUrl("https://www.fanhaojia.cc/fanhao/gif");
        entry.setPageStart(1);
        entry.setPageEnd(100);

        try {
            crawler.exec(entry);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        crawler.destroy();
    }
}
