package com.sparrow.app.store.berkeley;

/**
 * Created by IntelliJ IDEA. Date: 13-5-9 Time: 上午11:00 To change this template
 * use File | Settings | File Templates.
 */
public class PageRecordScanImpl implements PageRecordScan {
	public void scan(UrlData record) {
		System.out.println(record.getUrl());
	}
}
