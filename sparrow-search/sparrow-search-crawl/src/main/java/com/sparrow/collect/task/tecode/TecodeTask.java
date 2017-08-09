package com.sparrow.collect.task.tecode;

import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.selector.AbstractPageSelector;
import com.sparrow.collect.task.AbstractTask;
import com.sparrow.collect.task.Context;

import java.io.File;

public class TecodeTask  extends AbstractTask {

    public void execute(Context ctx) {
        AbstractPageSelector selector = new TecodeSelector();
        selector.setPageItemSelectExpress("#nei>div>p>a");
        selector.setUrlSelectExpress("href");

        TecodeCrawler crawler = new TecodeCrawler(new File(
                "F:\\tc\\extract"));
        crawler.setSelector(selector);

        SiteEntry entry = new SiteEntry();
        entry.setTitle("阿里巴巴技术");
        entry.setUrl("http://www.tuicool.com/articles/IziMn2");
        entry.setPageStart(1);
        entry.setPageEnd(1);


        crawler.exec(entry);
        crawler.destroy();
    }

}
