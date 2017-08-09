package com.sparrow.collect.task.btmeet;


import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.selector.AbstractPageSelector;

public class BtmeetSelector extends AbstractPageSelector {

	@Override
	protected boolean ignore(String url, String name) {
		return false;
	}

	@Override
	protected void correct(EntryData data,EntryData parentPage) {
		if (data.getUrl().startsWith("/wiki/"))
			data.setUrl("http://www.btmeet.org" + data.getUrl());
	}

}
