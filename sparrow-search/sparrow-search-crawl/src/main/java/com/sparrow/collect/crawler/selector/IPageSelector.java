package com.sparrow.collect.crawler.selector;

import java.util.List;

import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.dom.CrawlerDom;

public interface IPageSelector {
    SelectType HREF = new SelectType("html", "a", "href"),
            IMG = new SelectType("img", "img", "src"),
            SCRIPT = new SelectType("js", "script", "src"),
            CSS = new SelectType("css", "link", "href");

    List<EntryData> selectPageEntries(CrawlerDom dom, EntryData parentPage);

    String getType();
}
