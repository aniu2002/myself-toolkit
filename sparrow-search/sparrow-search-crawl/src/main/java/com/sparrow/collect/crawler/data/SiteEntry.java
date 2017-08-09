package com.sparrow.collect.crawler.data;

public class SiteEntry extends EntryData {
	private String siteId;
	private String siteName;
	private String pageExpress;
	private String contentExpress;
	private int pageStart = 1;
	private int pageEnd = -1;

	public String getContentExpress() {
		return contentExpress;
	}

	public void setContentExpress(String contentExpress) {
		this.contentExpress = contentExpress;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public int getPageStart() {
		return pageStart;
	}

	public void setPageStart(int pageStart) {
		this.pageStart = pageStart;
	}

	public int getPageEnd() {
		return pageEnd;
	}

	public void setPageEnd(int pageEnd) {
		this.pageEnd = pageEnd;
	}

	public String getPageExpress() {
		return pageExpress;
	}

	public void setPageExpress(String pageExpress) {
		this.pageExpress = pageExpress;
	}
}
