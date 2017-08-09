package com.sparrow.collect.task.site;

import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.selector.IPageSelector;
import com.sparrow.collect.crawler.selector.MultiExpressSelector;
import com.sparrow.collect.task.AbstractTask;
import com.sparrow.collect.task.Context;

import java.io.File;

public class SiteTask extends AbstractTask {

    public void execute(Context ctx) {

        /*
        AbstractPageSelector selector = new SiteSelector();
		selector.setPageItemSelectExpress("a");
		selector.setUrlSelectExpress("href");
      */

        MultiExpressSelector selector = new MultiExpressSelector();
        selector.addSelector(new SiteSelector(IPageSelector.HREF));
        selector.addSelector(new SiteSelector(IPageSelector.IMG));
        selector.addSelector(new SiteSelector(IPageSelector.CSS));
        selector.addSelector(new SiteSelector(IPageSelector.SCRIPT));

        SiteCrawler crawler = new SiteCrawlerEx(new File(
                "F:\\wiki\\icu"));

        crawler.setSelector(selector);
/*        crawler.setIgnoreList(new String[]{"http://www.icloudunion.com/modelExample",
                "http://www.icloudunion.com",
                "http://www.icloudunion.com/modelExample/金融/信用卡异常检测模型"});*/
        crawler.setIgnoreList(new String[]{"http://172.27.8.213:8081/doc",
                "http://172.27.8.213:8081/modelExample",
                "http://172.27.8.213:8081"});

        SiteEntry entry = new SiteEntry();
        entry.setTitle("icu");
        //entry.setPageExpress(".searchresults>ol>li>a");
        //entry.setContentExpress("#content");
        //entry.setUrl("http://www.icloudunion.com/modelExample/index.html");
        entry.setUrl("http://172.27.8.213:8081/index.html");
        entry.setPageStart(1);
        entry.setPageEnd(1);

        crawler.exec(entry);
        crawler.destroy();
    }
}
