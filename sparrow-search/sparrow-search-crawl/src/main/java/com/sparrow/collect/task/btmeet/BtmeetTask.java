package com.sparrow.collect.task.btmeet;

import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.selector.AbstractPageSelector;
import com.sparrow.collect.task.AbstractTask;
import com.sparrow.collect.task.Context;

import java.io.File;

public class BtmeetTask extends AbstractTask {

    public void execute(Context ctx) {
        AbstractPageSelector selector = new BtmeetSelector();
        selector.setPageItemSelectExpress(".item-title>h3>a");
        selector.setUrlSelectExpress("href");

        BtmeetCrawler crawler = new BtmeetCrawler(new File(
                "d:\\btmeet"));
        crawler.setSelector(selector);
        crawler.setSingleThread(false);

        SiteEntry entry = new SiteEntry();
        entry.setTitle("BT搜索");
        entry.setUrl("http://www.btmeet.org/search");
        entry.setPageStart(1);
        entry.setPageEnd(6);

        crawler.exec(entry);
        crawler.destroy();
    }

}
