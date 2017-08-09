package com.sparrow.collect.crawler.dom.impl;

import com.sparrow.collect.crawler.dom.CrawlerDom;
import com.sparrow.collect.crawler.dom.CrawlerNode;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;

public class JsoupCrawlerDomImpl implements CrawlerDom {
    Document doc;

    public Document getJsoupDocument() {
        return doc;
    }

    public void parse(String text) {
        if (StringUtils.isNotEmpty(text))
            this.doc = Jsoup.parse(text);
    }

    public CrawlerNode selectNode(String path) {
        if (this.doc == null)
            return null;
        Elements elements = this.doc.select(path);
        if (elements.isEmpty())
            return null;
        return JsoupTools.covertToCrNode(elements.first());
    }

    public List<CrawlerNode> selectNodes(String path) {
        if (this.doc == null)
            return null;
        Elements elements = this.doc.select(path);
        if (elements.isEmpty())
            return null;
        return JsoupTools.covertToCrNodes(elements);
    }

    @Override
    public String value(String path) {
        if (this.doc == null)
            return null;
        Elements elements = this.doc.select(path);
        if (elements.isEmpty())
            return null;
        return elements.val();
    }

    @Override
    public String text(String path) {
        if (this.doc == null)
            return null;
        Elements elements = this.doc.select(path);
        if (elements.isEmpty())
            return null;
        return elements.text();
    }

    @Override
    public String attr(String path, String attrName) {
        if (this.doc == null)
            return null;
        Elements elements = this.doc.select(path);
        if (elements.isEmpty())
            return null;
        return elements.attr(attrName);
    }

    @Override
    public String toHtml() {
        if (this.doc == null)
            return null;
        return this.doc.html();
    }
}