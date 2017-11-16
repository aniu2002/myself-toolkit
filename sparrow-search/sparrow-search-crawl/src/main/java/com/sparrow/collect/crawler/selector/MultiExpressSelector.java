package com.sparrow.collect.crawler.selector;

import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.dom.CrawlerDom;
import com.sparrow.collect.crawler.dom.CrawlerNode;

import java.util.ArrayList;
import java.util.List;


public class MultiExpressSelector implements IPageSelector {
    private List<AbstractPageSelector> selectors = new ArrayList<AbstractPageSelector>();

    public void addSelector(AbstractPageSelector selector) {
        this.selectors.add(selector);
    }

    public List<EntryData> selectPageEntries(CrawlerDom dom, EntryData parentPage) {
        List<EntryData> entries = new ArrayList<EntryData>();
        for (AbstractPageSelector selector : this.selectors) {
            this.selectPageEntries(entries, dom, selector, parentPage);
        }
        return entries;
    }

    public List<EntryData> selectPageEntries(List<EntryData> entries, CrawlerDom dom, AbstractPageSelector selector, EntryData parentPage) {
        List<CrawlerNode> nodes = dom.selectNodes(selector.getPageItemSelectExpress());
        if (nodes == null || nodes.isEmpty())
            return null;
        for (CrawlerNode node : nodes) {
            String url = node.attr(selector.getUrlSelectExpress());
            String title = node.text();
            if (selector.ignore(url, title))
                continue;
            EntryData entryData = new EntryData();
            entryData.setTitle(title);
            entryData.setUrl(url);
            entryData.setPageType(selector.getType());
            selector.correctDom(node, entryData, parentPage);
            selector.correct(entryData, parentPage);
            entries.add(entryData);
        }
        return entries;
    }

    @Override
    public String getType() {
        return null;
    }
}