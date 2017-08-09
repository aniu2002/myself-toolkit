package com.sparrow.app.store.berkeley;

import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-4-9
 * Time: 下午7:44
 * To change this template use File | Settings | File Templates.
 */
public class TupleBinding extends com.sleepycat.bind.tuple.TupleBinding<UrlData> {

    @Override
    public UrlData entryToObject(TupleInput input) {
        UrlData webURL = new UrlData();
        webURL.setUrl(input.readString());
        webURL.setSiteId(input.readInt());
        return webURL;
    }

    @Override
    public void objectToEntry(UrlData url, TupleOutput output) {
        output.writeString(url.getUrl());
        output.writeInt(url.getSiteId());
    }
}
