package com.sparrow.collect.task.tecode;


import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.selector.AbstractPageSelector;

public class TecodeSelector extends AbstractPageSelector {

	@Override
	protected boolean ignore(String url, String name) {
		return false;
	}

	@Override
	protected void correct(EntryData data,EntryData parentPage) {
		if (data.getUrl().indexOf("%E6") != -1)
			data.setUrl(data.getUrl()
					.substring(0, data.getUrl().indexOf("%E6")));
	}

}
