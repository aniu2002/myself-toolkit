package com.sparrow.collect.task.wiki;

import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.selector.AbstractPageSelector;
import com.sparrow.collect.task.AbstractTask;
import com.sparrow.collect.task.Context;

import java.io.File;

public class WikiTask extends AbstractTask {

	public void execute(Context ctx) {
		AbstractPageSelector selector = new WikiSelector();
		selector.setPageItemSelectExpress(".searchresults>ol>li>a");
		selector.setUrlSelectExpress("href");

		WikiCrawler crawler = new WikiCrawler(new File(
				"F:\\wiki\\extract"));

		crawler.setSelector(selector);

		SiteEntry entry = new SiteEntry();
		entry.setTitle("markdown");
		//entry.setPageExpress(".searchresults>ol>li>a");
		entry.setContentExpress("#content");
		entry.setUrl("http://172.27.9.194/wiki/ICU%E4%B8%BB%E9%A1%B5");
		entry.setPageStart(1);
		entry.setPageEnd(1);
		
		crawler.exec(entry);
		crawler.destroy();
	}
}
