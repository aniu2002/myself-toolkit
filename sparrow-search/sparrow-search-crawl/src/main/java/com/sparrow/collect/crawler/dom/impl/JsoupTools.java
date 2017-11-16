package com.sparrow.collect.crawler.dom.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sparrow.collect.crawler.dom.CrawlerNode;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupTools {
	public static List<CrawlerNode> covertToCrNodes(Elements eles) {
		Iterator<Element> iter = eles.iterator();
		List<CrawlerNode> nodes = new ArrayList<CrawlerNode>();
		while (iter.hasNext()) {
			nodes.add(new JsoupCrawlerNodeImpl(iter.next()));
		}
		return nodes;
	}

	public static CrawlerNode covertToCrNode(Element ele) {
		return new JsoupCrawlerNodeImpl(ele);
	}

}
