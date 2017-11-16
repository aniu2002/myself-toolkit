package com.sparrow.collect.crawler.selector;

import java.util.ArrayList;
import java.util.List;

import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.dom.CrawlerDom;
import com.sparrow.collect.crawler.dom.CrawlerNode;

public abstract class AbstractPageSelector implements IPageSelector {
    protected String pageItemSelectExpress;
    protected String urlSelectExpress;
    protected String nameSelectExpress;

    public String getPageItemSelectExpress() {
        return pageItemSelectExpress;
    }

    public void setPageItemSelectExpress(String pageItemSelectExpress) {
        this.pageItemSelectExpress = pageItemSelectExpress;
    }

    public String getUrlSelectExpress() {
        return urlSelectExpress;
    }

    public void setUrlSelectExpress(String urlSelectExpress) {
        this.urlSelectExpress = urlSelectExpress;
    }

    public String getNameSelectExpress() {
        return nameSelectExpress;
    }

    public void setNameSelectExpress(String nameSelectExpress) {
        this.nameSelectExpress = nameSelectExpress;
    }

    public List<EntryData> selectPageEntries(CrawlerDom dom, EntryData parentPage) {
        List<CrawlerNode> nodes = dom.selectNodes(this.getPageItemSelectExpress());
        if (nodes == null || nodes.isEmpty())
            return null;
        List<EntryData> entries = new ArrayList<EntryData>();
        for (CrawlerNode node : nodes) {
            String url = node.attr(this.getUrlSelectExpress());
            String title = node.text();
            if (this.ignore(url, title))
                continue;
            EntryData entryData = new EntryData();
            entryData.setTitle(title);
            entryData.setUrl(url);
            entryData.setPageType(this.getType());
            this.correctDom(node, entryData, parentPage);
            this.correct(entryData, parentPage);
            entries.add(entryData);
        }
        return entries;
    }

    protected abstract boolean ignore(String url, String name);

    protected abstract void correct(EntryData data, EntryData parentPage);

    protected void correctDom(CrawlerNode node, EntryData data, EntryData parentPage) {

    }

    @Override
    public String getType() {
        return HREF.type;
    }
}
