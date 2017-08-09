package com.sparrow.collect.task.wiki;


import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.selector.AbstractPageSelector;

public class WikiSelector extends AbstractPageSelector {

    @Override
    protected boolean ignore(String url, String name) {
        return false;
    }

    @Override
    protected void correct(EntryData data,EntryData parentPage) {
        if (data.getUrl().startsWith("/wiki/"))
            data.setUrl("http://172.27.9.194" + data.getUrl()+"?action=edit&editor=text");
        //+"?action=edit&editor=text"
    }

}
