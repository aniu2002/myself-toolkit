package com.sparrow.collect.score;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.FieldCache;

import java.io.IOException;

/**
 * Created by yangtao on 2015/7/23.
 */
public class GoodsScorer extends FieldScorer {
    private final static String PID_FIELD = "id";
    protected FieldCache.Longs pids;

    private ScoreCache cache;

    public GoodsScorer(AtomicReaderContext context) {
        super(context);
        try {
            pids = FieldCache.DEFAULT.getLongs(context.reader(), PID_FIELD, false);
        } catch (IOException e) {
            log.error("从FieldCache中获取pids异常:", e);
        }
        this.cache = GoodsScoreCache.getInstance();
    }

    @Override
    public float score(int doc, Long userId) {
        Long pid = pids.get(doc);
        if(pid == null) {
            return 0f;
        }
        float score = cache.get(pid, userId);
        score = weight * score;
        return score;
    }
}
