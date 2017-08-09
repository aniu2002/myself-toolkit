package com.sparrow.collect.store.check;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;

/**
 * Created by Administrator on 2017/8/8 0008.
 */
public class BloomFilterCheck {

    public void add() {
       BloomFilter filter= BloomFilter.create(new Funnel<String>() {
            @Override
            public void funnel(String string, PrimitiveSink primitiveSink) {
                primitiveSink.putBytes(string.getBytes());
            }
        },1000);

        //filter.writeTo();
    }

}
