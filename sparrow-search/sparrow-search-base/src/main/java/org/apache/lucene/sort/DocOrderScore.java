package org.apache.lucene.sort;

/**
 * Created by yaobo on 2014/8/29.
 */
public class DocOrderScore {
    /**
     * 业务id
     */
    public String bid;
    /**
     * docid
     */
    public int doc;
    /**
     * 人工order
     */
    public int order;
    /**
     * 相关性score
     */
    public float score;

    @Override
    public String toString() {
        return "DocOrderScore{" +
                "bid='" + bid + '\'' +
                ", doc=" + doc +
                ", order=" + order +
                ", score=" + score +
                '}';
    }
}
