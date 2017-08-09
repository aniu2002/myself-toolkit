package com.sparrow.collect.task.crawler;

import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.selector.AbstractPageSelector;
import com.sparrow.collect.task.AbstractTask;
import com.sparrow.collect.task.Context;

import java.io.File;

public class CrawlerTask extends AbstractTask {

	public void execute(Context ctx) {
		AbstractPageSelector selector = new PublishSelector();
		selector.setPageItemSelectExpress("#content>a");
		selector.setUrlSelectExpress("href");

		PublishCrawler crawler = new PublishCrawler(new File(
				"F:\\99bt\\extract"));

		crawler.setSelector(selector);

		SiteEntry entry = new SiteEntry();
		entry.setTitle("2014");
		entry.setUrl("http://we.99bitgongchang.net/00/04");
		entry.setPageStart(3);
		entry.setPageEnd(3);
		
		crawler.exec(entry);
		crawler.destroy();
	}
}
