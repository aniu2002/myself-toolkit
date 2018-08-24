package com.sparrow.collect.top;

import com.sparrow.collect.score.FieldScorer;
import com.sparrow.collect.score.GoodsTopScorer;
import com.sparrow.collect.website.Configs;
import com.sparrow.collect.website.SearchConfig;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.search.Query;

import java.io.IOException;

/**
 * Created by yangtao on 2015/11/10.
 */
public class GoodsTopQuery extends CustomScoreQuery {
    private float weight = 1.0f;
    private Long userId;
    private GoodsTopOrderCache scoreCache;
    /**
     * 默认商品计算分数
     */
    private static final float DEFAULT_EXT_SCORE = Configs.getFloat("goods.calculate.score.default", 0.04f);


    public GoodsTopQuery(Query subQuery, String userId, TopOrderType topType, Number topTypeValue) {
        super(subQuery);
        if(NumberUtils.isNumber(userId)) {
            this.userId = NumberUtils.createLong(userId);
        } else {
            this.userId = 0L;
        }
        this.scoreCache = new GoodsTopOrderCache(topType, topTypeValue);
        this.scoreCache.initCache();
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    protected CustomScoreProvider getCustomScoreProvider(AtomicReaderContext context) throws IOException {
        return new GoodsTopScoreProvider(context);
    }

    private class GoodsTopScoreProvider extends CustomScoreProvider {
        FieldScorer scorer;
        FieldScorer compositeScorer;

        public GoodsTopScoreProvider(AtomicReaderContext context) {
            super(context);
            this.scorer = new GoodsTopScorer(context, scoreCache);
            this.compositeScorer = new GoodsCompositeScorer(context);
        }

        @Override
        public float customScore(int doc, float subQueryScore, float[] valSrcScore) throws IOException {
            float score = scorer.score(doc, userId);
            if(Float.isNaN(score)) {
                float extScore = compositeScorer.score(doc, userId);
                if(extScore <= 0) {
                    extScore = DEFAULT_EXT_SCORE;
                }
                subQueryScore = subQueryScore * extScore;
                return super.customScore(doc, subQueryScore, valSrcScore);
            }
            return score;
        }
    }
}
