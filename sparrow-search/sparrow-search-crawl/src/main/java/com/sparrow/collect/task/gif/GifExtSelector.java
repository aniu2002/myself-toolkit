package com.sparrow.collect.task.gif;


import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.selector.AbstractPageSelector;

public class GifExtSelector extends AbstractPageSelector {

    @Override
    protected boolean ignore(String url, String name) {
        return "http://www.alegev.com/avfuli.htm".equals(url);
    }

    @Override
    protected void correct(EntryData data, EntryData parentPage) {
        if (data.getUrl().startsWith("/p2p/"))
            data.setUrl("http://we.99bitgongchang.net" + data.getUrl());
    }

}
