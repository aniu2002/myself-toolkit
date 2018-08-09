package com.sparrow.collect.website.filter.support;

import com.sparrow.collect.website.cache.filter.InactivityShopCache;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.OpenBitSet;

import java.io.IOException;

/**
 * Created by yangtao on 2015/12/29.
 */
public class InactivityShopFilter extends Filter {
    private String idField;

    public InactivityShopFilter(String idField) {
        this.idField = idField;
    }

    @Override
    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        int maxDocs = context.reader().maxDoc();
        int docBase = context.docBase;
        //与每个reader中doc个数对应
        OpenBitSet bitSet = new OpenBitSet(maxDocs);
        //将所有docId对应位值从0改为1, 值为1才能被搜索到
        bitSet.set(0, maxDocs);
        //取出店铺id
        FieldCache.Longs shopIds = FieldCache.DEFAULT.getLongs(context.reader(), idField, false);
        if(shopIds == null) {
            return bitSet;
        }
        InactivityShopCache shopCache = InactivityShopCache.getInstance();
        for(int doc = 0; doc < maxDocs; doc++) {
            try {
                long shopId = shopIds.get(doc);
                if(shopCache.filter(shopId)) {
                    bitSet.fastClear(doc);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitSet;
    }
}
