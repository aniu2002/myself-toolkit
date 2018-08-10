package com.sparrow.collect.website.score;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.search.*;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.Map;

/**
 * 人工干预排序.
 * 只对score类型的sort有效. 用在商品的默认排序上. order从0开始.
 * 如果指定了order, 按order排序; 没有指定按score排序; order相同按score排序;
 * 将数据分为2块. 第一块是满足条件的人工干预顺序的doc, 第二块是按score排序的doc. 2块数据分别按order和score排序.
 * 还需要配合ManualOrderTopDocsCollector对结果进行再次排序
 * order定义在ExternalDataSource中.
 * Created by yaobo on 2014/8/27.
 */
public class ManualOrderScoreComparatorSource<K> extends FieldComparatorSource {

    protected Log log = LogFactory.getLog(this.getClass());

    /**
     * lucene中的id FieldName, 对应外部数据源的key
     */
    private String keyFieldName;

    /**
     * 外部数据源
     */
    private ExternalDataSource<K, Integer> externalDataSource;

    /**
     * 封装doc,order,scores
     */
    private Map<Integer, DocOrderScore> docOrderScores;

    /**
     * 类型转换器, 将lucene的field类型转成数据源的key类型
     */
    private KeyTypeCast<K> keyTypeCast;

    public ManualOrderScoreComparatorSource(String keyFieldName, ExternalDataSource<K, Integer> externalDataSource, KeyTypeCast<K> keyTypeCast) {
        this.keyFieldName = keyFieldName;
        this.externalDataSource = externalDataSource;
        this.keyTypeCast = keyTypeCast;
    }

    public void setDocOrderScores(Map<Integer, DocOrderScore> docOrderScores) {
        this.docOrderScores = docOrderScores;
    }

    @Override
    public FieldComparator<?> newComparator(String fieldname, int numHits, int sortPos, boolean reversed) throws IOException {
        return new ManualOrderScoreComparator(keyFieldName, numHits);
    }

    /**
     * lucene的field类型转成数据源的key类型
     *
     * @param <K>
     */
    public interface KeyTypeCast<K> {
        public K cast(String s);
    }

    @Override
    public String toString() {
        return "ManualOrderScoreComparatorSource{" +
                "keyFieldName='" + keyFieldName + '\'' +
                '}';
    }

    public final class ManualOrderScoreComparator extends FieldComparator<Float> {
        /**
         * 缓存当前reader中的terms
         */
        private BinaryDocValues terms;
        private String fieldName;

        //分值
        private final float[] scores;
        private float bottomScore;

        //序号
        private final int[] orders;
        private int bottomOrder;

        private Scorer scorer;

        private int NOT_MANUAL_ORDER = Integer.MIN_VALUE;

        ManualOrderScoreComparator(String fieldName, int numHits) {
            this.fieldName = fieldName;
            scores = new float[numHits];
            orders = new int[numHits];
        }

        @Override
        public int compare(int slot1, int slot2) {
            return compare(scores[slot1], scores[slot2], orders[slot1], orders[slot2]);
        }

        @Override
        public int compareBottom(int doc) throws IOException {
            float score1 = bottomScore;
            float score2 = scorer.score();

            String id = getIdFieldByDoc(doc);
            int order1 = bottomOrder;
            int order2 = getOrder(id);

            return compare(score1, score2, order1, order2);
        }

        @Override
        public void copy(int slot, int doc) throws IOException {
            scores[slot] = scorer.score();
            assert !Float.isNaN(scores[slot]);

            String id = getIdFieldByDoc(doc);
            int order = getOrder(id);
            orders[slot] = order;

            addOrderScores(id, doc, scores[slot], orders[slot]);
        }

        @Override
        public FieldComparator<Float> setNextReader(AtomicReaderContext context) throws IOException {
            terms = FieldCache.DEFAULT.getTerms(context.reader(), fieldName, false);
            return this;
        }

        @Override
        public void setBottom(final int bottom) {
            this.bottomScore = scores[bottom];
            this.bottomOrder = orders[bottom];
        }

        @Override
        public void setScorer(Scorer scorer) {
            if (!(scorer instanceof ScoreCachingWrappingScorer)) {
                this.scorer = new ScoreCachingWrappingScorer(scorer);
            } else {
                this.scorer = scorer;
            }
        }

        @Override
        public Float value(int slot) {
            return Float.valueOf(scores[slot]);
        }

        @Override
        public int compareDocToValue(int doc, Float valueObj) throws IOException {
            final float value = valueObj.floatValue();
            float docValue = scorer.score();
            assert !Float.isNaN(docValue);
            return Float.compare(value, docValue);
        }

        /**
         * 根据doc获取keyFieldName的值
         *
         * @param doc
         * @return
         */
        private String getIdFieldByDoc(int doc) {
            BytesRef byteRef = new BytesRef();
            terms.get(doc, byteRef);
            String id = byteRef.utf8ToString();
            return id;
        }

        /**
         * 根据bid查询人工干预order
         *
         * @param id
         * @return
         */
        private int getOrder(String id) {
            Integer order = NOT_MANUAL_ORDER;
            K k = keyTypeCast.cast(id);
            if (k == null){
                return order;
            }
            order = externalDataSource.getValue(k);
            order = (order == null ? NOT_MANUAL_ORDER : order);
            return order;
        }

        /**
         * 生成doc:bid:order:score对应关系
         *
         * @param bid
         * @param doc
         * @param score
         * @param order
         */
        private void addOrderScores(String bid, int doc, float score, int order) {
            if (docOrderScores != null && order != NOT_MANUAL_ORDER) {
                DocOrderScore orderScore = new DocOrderScore();
                orderScore.bid = bid;
                orderScore.doc = doc;
                orderScore.score = score;
                orderScore.order = order;
                docOrderScores.put(doc, orderScore);
            }
        }

        /**
         * compare
         * order越小值越大
         * score越大值越大
         * 按倒序排序
         *
         * @param score1
         * @param score2
         * @param order1
         * @param order2
         * @return 大于返回1, 小于返回-1. reversed=true,会对值取反.
         */
        private int compare(float score1, float score2, int order1, int order2) {
            //指定了人工排序, 按order排序, 相同按score排序
            if (order1 != NOT_MANUAL_ORDER && order2 != NOT_MANUAL_ORDER) {
                int result = Integer.compare(order1, order2);
                return result == 0 ? Float.compare(score1, score2) : -result;
                //1指定了人工排序, 2未指定, order在查询条数内放到前面
            } else if (order1 != NOT_MANUAL_ORDER && order2 == NOT_MANUAL_ORDER) {
                return order1 < scores.length ? 1 : -1;
                //1未指定了人工排序, 2指定了, order在查询条数内放到前面
            } else if (order1 == NOT_MANUAL_ORDER && order2 != NOT_MANUAL_ORDER) {
                return order2 < scores.length ? -1 : 1;
            } else {
                //没有order, 直接按score排序
                return Float.compare(score1, score2);
            }
        }

    }

}
