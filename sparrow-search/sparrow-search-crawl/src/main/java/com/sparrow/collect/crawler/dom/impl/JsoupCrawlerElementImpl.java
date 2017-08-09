package com.sparrow.collect.crawler.dom.impl;

import com.sparrow.collect.crawler.dom.CrawlerNode;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.List;

public class JsoupCrawlerElementImpl implements CrawlerNode {
    final Element element;

    JsoupCrawlerElementImpl(Element element) {
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
    public void attr(String key, String val) {
        if (StringUtils.isNotEmpty(val))
            this.element.attr(key, val);
    }

    @Override
    public int childSize() {
        return this.element.childNodeSize();
    }

    @Override
    public CrawlerNode childNode(int idx) {
        Node node = this.element.childNode(idx);
        if (node instanceof Element)
            return JsoupTools.covertToCrNode((Element) node);
        else
            return new JsoupCrawlerNodeImpl(node);
    }

    @Override
    public String nodeName() {
        return this.element.nodeName();
    }
}