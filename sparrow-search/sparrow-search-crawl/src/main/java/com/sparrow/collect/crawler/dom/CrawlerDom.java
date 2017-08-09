package com.sparrow.collect.crawler.dom;

import java.util.List;

public interface CrawlerDom {
    void parse(String text);

    List<CrawlerNode> selectNodes(String path);

    CrawlerNode selectNode(String path);

    String value(String path);

    String text(String path);

    String attr(String path, String attrName);

    String toHtml();
}
