package com.sparrow.collect.task.gif;


import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.selector.AbstractPageSelector;

public class GifSelector extends AbstractPageSelector {

    @Override
    protected boolean ignore(String url, String name) {
        return false;
    }

    @Override
    protected void correct(EntryData data, EntryData parentPage) {
        if (data.getUrl().startsWith("/p2p/"))
            data.setUrl("http://we.99bitgongchang.net" + data.getUrl());
    }

}
