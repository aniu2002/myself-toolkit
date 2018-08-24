package com.sparrow.collect.score;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.AtomicReaderContext;

/**
 * Created by yangtao on 2015/7/23.
 */
public abstract class FieldScorer {
    protected Log log = LogFactory.getLog(this.getClass());

    protected AtomicReaderContext context;

    protected float weight = 1.0f;

    public FieldScorer(AtomicReaderContext context) {
        this.context = context;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public abstract float score(int doc, Long userId);
}
