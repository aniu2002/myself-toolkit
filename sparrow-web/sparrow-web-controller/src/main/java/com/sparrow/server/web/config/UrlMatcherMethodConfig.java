package com.sparrow.server.web.config;


public class UrlMatcherMethodConfig extends ControllerMethodConfig {
	private UrlMatcherItem item;

	public UrlMatcherMethodConfig() {
	}

	public UrlMatcherMethodConfig(UrlMatcherItem item) {
		this.item = item;
	}

	public UrlMatcherItem getItem() {
		return item;
	}

	public void setItem(UrlMatcherItem item) {
		this.item = item;
	}

	public MatcherResult match(String url) {
		return this.item.match(url);
	}
}
