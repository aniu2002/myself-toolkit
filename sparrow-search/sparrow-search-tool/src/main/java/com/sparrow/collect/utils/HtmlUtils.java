package com.sparrow.collect.utils;

import au.id.jericho.lib.html.Source;

public class HtmlUtils {

	public static String getContentsFromHtml(String htmlcontent) {
		Source source = new Source(htmlcontent);
		return source.getRenderer().toString();
	}
}
