package com.sparrow.collect.task.gif;

import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.selector.AbstractPageSelector;
import com.sparrow.collect.task.AbstractTask;
import com.sparrow.collect.task.Context;

import java.io.File;

public class GifTask extends AbstractTask {

    public void execute(Context ctx) {
        AbstractPageSelector selector = new GifSelector();
        selector.setPageItemSelectExpress(".excerpt.excerpt-one>header>h2>a");
        selector.setUrlSelectExpress("href");

        GifCrawler crawler = new GifCrawler(new File(
                "D:\\fanhao\\extract"));
        crawler.setPageCheck(false);
        crawler.setSiteCheck(false);
        crawler.setDetailCheck(false);
        crawler.setSelector(selector);

        SiteEntry entry = new SiteEntry();
        entry.setTitle("2014");
        entry.setUrl("http://www.dongde.in/category");
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
