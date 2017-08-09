package com.sparrow.collect.crawler.selector;

import com.sparrow.collect.crawler.data.EntryData;


public class NormalPageSelector extends AbstractPageSelector {
    private SelectType selectType;

    public NormalPageSelector() {

    }

    public NormalPageSelector(SelectType type) {
        this.pageItemSelectExpress = type.tag;
        this.urlSelectExpress = type.attr;
        this.selectType = type;
    }

    @Override
    protected boolean ignore(String url, String name) {
        return false;
    }

    @Override
    protected void correct(EntryData data,EntryData parentPage) {

    }

    @Override
    public String getType() {
        return this.selectType.type;
    }
}