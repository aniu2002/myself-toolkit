package com.sparrow.collect.log;

import com.sparrow.collect.crawler.data.EntryData;

/**
 * Created by Administrator on 2016/12/14 0014.
 */
public interface UrlReadCallback {
    boolean handle(EntryData data);
}
