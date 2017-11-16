package com.sparrow.collect.crawler.dom.impl;

import com.sparrow.collect.crawler.dom.CrawlerNode;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import java.util.List;

public class JsoupCrawlerNodeImpl implements CrawlerNode {
    final Element element;

    JsoupCrawlerNodeImpl(Element element) {
        this.element = element;
    }

    public String attr(String attributeKey) {
        return this.element.attr(attributeKey);
    }

    public CrawlerNode selectNode(String path) {
        return JsoupTools.covertToCrNode(this.element.select(path).first());
    }

    public List<CrawlerNode> selectNodes(String path) {
        return JsoupTools.covertToCrNodes(this.element.select(path));
    }

    public String text() {
        return this.element.text();
    }

    @Override
    public String html() {
        return this.element.html();
    }

    @Override
    public void attr(String key, String val) {
        if (StringUtils.isNotEmpty(val))
            this.element.attr(key, val);
    }
}