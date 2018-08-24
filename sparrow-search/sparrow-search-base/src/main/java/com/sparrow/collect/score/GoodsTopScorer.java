package com.sparrow.collect.score;

import com.sparrow.collect.top.GoodsTopOrderCache;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.FieldCache;

import java.io.IOException;

/**
 * Created by yangtao on 2015/11/9.
 */
public class GoodsTopScorer extends FieldScorer {
    //基础分数
    public static final float BASE_SCORE = 10000000;
    //商品id域名称
    private final static String PID_FIELD = "id";
    //商品id缓存
    protected FieldCache.Longs pids;
    //商品id:排序号映射关系
    private GoodsTopOrderCache scoreCache;

    public GoodsTopScorer(AtomicReaderContext context, GoodsTopOrderCache scoreCache) {
        super(context);
        try {
            this.pids = FieldCache.DEFAULT.getLongs(context.reader(), PID_FIELD, false);
        } catch (IOException e) {
            log.error("从FieldCache中获取pid异常:", e);
        }
        this.scoreCache = scoreCache;
    }

    @Override
    public float score(int doc, Long userId) {
        Long pid = pids.get(doc);
        if(pid == null) {
            return Float.NaN;
        }
        Integer order = scoreCache.getOrder(pid);
        if(order == null) {
            return Float.NaN;
        }
        return BASE_SCORE - order;
    }
}
