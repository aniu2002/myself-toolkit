package com.sparrow.collect.crawler.dom.impl;

import com.sparrow.collect.crawler.dom.CrawlerNode;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.Collections;
import java.util.List;

public class JsoupCrawlerNodeImpl implements CrawlerNode {
    final Node node;

    JsoupCrawlerNodeImpl(Node node) {
        this.node = node;
    }

    public String attr(String attributeKey) {
        return this.node.attr(attributeKey);
    }

    public CrawlerNode selectNode(String path) {
        return null;
    }

    public List<CrawlerNode> selectNodes(String path) {
        return Collections.emptyList();
    }

    public String text() {
        if (this.node instanceof TextNode) {
            TextNode element = (TextNode) node;
            return element.getWholeText();
        }
        return null;
    }

    @Override
    public void attr(String key, String val) {
    }

    @Override
    public int childSize() {
        return 0;
    }

    @Override
    public CrawlerNode childNode(int idx) {
        return null;
    }

    @Override
    public String nodeName() {
        return this.node.nodeName();
    }
}