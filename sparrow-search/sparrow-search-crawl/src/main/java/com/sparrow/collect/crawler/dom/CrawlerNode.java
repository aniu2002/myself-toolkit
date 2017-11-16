package com.sparrow.collect.crawler.dom;

import java.util.List;

public interface CrawlerNode {
    List<CrawlerNode> selectNodes(String path);

    CrawlerNode selectNode(String path);

    String text();

    String html();

    String attr(String attributeKey);

    void attr(String key,String val);
}
