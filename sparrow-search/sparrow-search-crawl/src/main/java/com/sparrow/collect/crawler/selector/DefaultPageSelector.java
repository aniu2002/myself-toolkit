package com.sparrow.collect.crawler.selector;

import com.sparrow.collect.crawler.data.EntryData;


public class DefaultPageSelector extends AbstractPageSelector {

    public DefaultPageSelector() {

    }

    public DefaultPageSelector(String pageItemSelectExpress, String urlSelectExpress) {
        this.pageItemSelectExpress = pageItemSelectExpress;
        this.urlSelectExpress = urlSelectExpress;
    }

    @Override
    protected boolean ignore(String url, String name) {
        return false;
    }

    @Override
    protected void correct(EntryData data,EntryData parentPage) {

    }
}