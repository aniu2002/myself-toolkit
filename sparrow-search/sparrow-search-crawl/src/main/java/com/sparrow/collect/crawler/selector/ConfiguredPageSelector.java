package com.sparrow.collect.crawler.selector;

import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.dom.CrawlerDom;
import com.sparrow.collect.crawler.dom.CrawlerNode;
import com.sparrow.collect.crawler.conf.site.SelectorConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ConfiguredPageSelector extends SelectorConfig implements IPageSelector {

    @Override
    public List<EntryData> selectPageEntries(CrawlerDom dom,EntryData parentPage) {
        List<EntryData> entries = new ArrayList<EntryData>();
        this.selectPageItem(dom, this.getItemExpress(), entries);
        return entries;
    }

    protected void selectPageItem(CrawlerDom dom, List<String> itemExpress, List<EntryData> entries) {
        for (String express : itemExpress) {
            this.selectPageItem(dom, express, entries);
        }
    }

    protected void selectPageItem(CrawlerDom dom, String itemExpress, List<EntryData> entries) {
        List<CrawlerNode> nodes = dom.selectNodes(itemExpress);
        if (nodes == null || nodes.isEmpty())
            return;
        for (CrawlerNode node : nodes) {
            this.selectPageUrl(node, this.getUrlExpress(), entries);
        }
    }

    protected void selectPageUrl(CrawlerNode node, List<String> itemExpress, List<EntryData> entries) {
        for (String express : itemExpress) {
            this.selectPageUrl(node, express, entries);
        }
    }

    protected void selectPageUrl(CrawlerNode node, String itemExpress, List<EntryData> entries) {
        String url = node.attr(itemExpress);
        String title = StringUtils.isNotEmpty(this.getNameExpress()) ? node.attr(this.getNameExpress()) : node.text();
        if (this.ignore(url, title))
            return;
        EntryData entryData = new EntryData();
        entryData.setTitle(title);
        entryData.setUrl(url);
        this.correct(entryData);
        entries.add(entryData);
    }

    protected boolean ignore(String url, String name) {
        return false;
    }

    protected void correct(EntryData data) {

    }

    @Override
    public String getType() {
        return HREF.type;
    }
}