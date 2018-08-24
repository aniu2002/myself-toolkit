package com.sparrow.collect.score;

import org.apache.lucene.index.AtomicReaderContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangtao on 2015/7/22.
 */
public class GoodsCompositeScorer extends FieldScorer {

    List<FieldScorer> fieldScorers;

    public GoodsCompositeScorer(AtomicReaderContext context) {
        super(context);

        FieldScorer goodsScorer = new GoodsScorer(context);

        fieldScorers = new ArrayList();
        fieldScorers.add(goodsScorer);
    }

    @Override
    public float score(int doc, Long userId) {
        float score = 0f;
        for(FieldScorer scorer : fieldScorers) {
            score += scorer.score(doc, userId);
        }
        return score;
    }
}
