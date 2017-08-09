package org.apache.lucene.search;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.sort.DocOrderScore;

import java.io.IOException;
import java.util.*;

/**
 * 人工干预评分collector.
 * 需要和ManualOrderScoreComparator配合使用.
 * ManualOrderScoreComparator将数据分为2块. 第一块是满足条件的人工干预顺序的doc, 第二块是按score排序的doc. 2块数据分别按order和score排序.
 * exp: order1, order2... ordern, scoren...score2, score1. order越小越靠前, score越大越靠前.
 * ManualOrderTopDocsCollector将ManualOrderScoreComparator返回的数据重新排序, 将人工干预排序的doc放到指定的位置.
 * Created by yaobo on 2014/8/28.
 */
public class ManualOrderTopDocsCollector extends TopDocsCollector {
    private TopFieldCollector collector = null;

    /**
     * key:doc, value:doc对应的bid, 人工order, score.
     */
    private Map<Integer, DocOrderScore> docOrderScores = new HashMap<Integer, DocOrderScore>();

    public ManualOrderTopDocsCollector(Sort sort, int numHits) throws IOException {
        super(null);
        collector = TopFieldCollector.create(sort, numHits, true, true, true, false);
    }

    public Map<Integer, DocOrderScore> getDocOrderMap() {
        return docOrderScores;
    }

    @Override
    public void setScorer(Scorer scorer) throws IOException {
        //缓存score, 比较时会多次调用scorer.score()
        if (!(scorer instanceof ScoreCachingWrappingScorer)) {
            scorer = new ScoreCachingWrappingScorer(scorer);
        }
        collector.setScorer(scorer);
    }

    @Override
    public void collect(int doc) throws IOException {
        collector.collect(doc);
    }

    @Override
    public void setNextReader(AtomicReaderContext context) throws IOException {
        collector.setNextReader(context);
    }

    @Override
    public boolean acceptsDocsOutOfOrder() {
        return false;
    }

    @Override
    protected void populateResults(ScoreDoc[] results, int howMany) {
        collector.populateResults(results, howMany);
    }

    @Override
    protected TopDocs newTopDocs(ScoreDoc[] results, int start) {
        return collector.newTopDocs(results, start);
    }

    @Override
    public int getTotalHits() {
        return collector.getTotalHits();
    }

    @Override
    protected int topDocsSize() {
        return collector.topDocsSize();
    }

    @Override
    public TopDocs topDocs() {
        TopDocs topDocs = collector.topDocs();
        return sortByManualOrder(topDocs);
    }

    @Override
    public TopDocs topDocs(int start) {
        TopDocs topDocs = collector.topDocs(start);
        return sortByManualOrder(topDocs);
    }

    @Override
    public TopDocs topDocs(int start, int howMany) {
        TopDocs topDocs = collector.topDocs(start, howMany);
        return sortByManualOrder(topDocs);
    }

    /**
     * 人工干预排序. order从0开始
     * 1.将topDocs分为order和score两部分.
     * 2.order已按order,score排序.如果有相同order的, 将后面的order+1. 再依次类推.
     * 3.将order插入到score中. 插入时, 如果order>score.size, 则order=score.size.
     *
     * @param topDocs
     * @return
     */
    private TopDocs sortByManualOrder(TopDocs topDocs) {
        return topDocs;
//        List<TopDocsWapper> orderList = new ArrayList();
//        LinkedList<ScoreDoc> scoreList = new LinkedList<>();
//
//        //分成order,score2部分
//        int length = topDocs.scoreDocs.length;
//        for (int i = 0; i < length; i++) {
//            ScoreDoc scoreDoc = topDocs.scoreDocs[i];
//            DocOrderScore orderScore = this.docOrderScores.get(scoreDoc.doc);
//            if (orderScore != null) {
//                TopDocsWapper wapper = new TopDocsWapper();
//                wapper.scoreDoc = scoreDoc;
//                wapper.orderScore = orderScore;
//                orderList.add(wapper);
//            } else {
//                scoreList.add(scoreDoc);
//            }
//        }
//
//        //处理有相同order的情况
//        int orderSize = orderList.size();
//        for (int i = 0; i < orderSize; i++) {
//            TopDocsWapper wapper1 = orderList.get(i);
//            for (int j = i + 1; j < orderSize; j++) {
//                TopDocsWapper wapper2 = orderList.get(j);
//                if (wapper1.orderScore.order == wapper2.orderScore.order) {
//                    wapper2.orderScore.order++;
//                    int order = wapper2.orderScore.order;
//                    for (int k = j + 1; k < orderSize; k++){
//                        TopDocsWapper wapper3 = orderList.get(k);
//                        if (order == wapper3.orderScore.order) {
//                            wapper3.orderScore.order++;
//                            order = wapper3.orderScore.order;
//                        }
//                    }
//                }
//            }
//        }
//
//        //order按位置插入到score中. 如果order>score.size, order=score.size.
//        for (int i = 0; i < orderSize; i++) {
//            TopDocsWapper wapper = orderList.get(i);
//            int order = wapper.orderScore.order;
//            if (order > scoreList.size()) {
//                order = scoreList.size();
//            }
//            scoreList.add(order, wapper.scoreDoc);
//        }
//
//        topDocs.scoreDocs = scoreList.toArray(topDocs.scoreDocs);
//        return topDocs;
    }


    /**
     * Deprecated!!!排序有问题!!!!!
     * 收集完doc, 进行人工干预排序
     * 将scoreDoc包装一层, 用于排序
     *
     * @param topDocs
     * @return
     */
    @Deprecated
    private TopDocs sortByManualOrderWithComparable(TopDocs topDocs) {
        List<TopDocsWrapper> topDocsWrappers = new ArrayList<TopDocsWrapper>();
        int length = topDocs.scoreDocs.length;
        for (int i = 0; i < length; i++) {
            TopDocsWrapper wrapper = new TopDocsWrapper();
            wrapper.scoreDoc = topDocs.scoreDocs[i];
            wrapper.slot = i;
            wrapper.orderScore = this.docOrderScores.get(wrapper.scoreDoc.doc);
            topDocsWrappers.add(wrapper);
        }
        Collections.sort(topDocsWrappers);

        for (int i = 0; i < length; i++) {
            topDocs.scoreDocs[i] = topDocsWrappers.get(i).scoreDoc;
        }
        return topDocs;
    }

    /**
     * scoreDocWapper, 用于排序
     */
    public class TopDocsWrapper implements Comparable<TopDocsWrapper> {
        ScoreDoc scoreDoc;
        //排序前scoreDoc在topDoc中的位置
        int slot;
        //doc:order:score
        DocOrderScore orderScore;

        @Override
        public String toString() {
            return "TopDocsWapper{" +
                    "scoreDoc=" + scoreDoc +
                    ", slot=" + slot +
                    ", orderScore=" + orderScore +
                    '}';
        }

        /**
         * 重新排序
         * order越小值越大
         * score越大值越大
         * 按倒序排序, 返回-1放前面, 返回1放后面.
         *
         * @param o
         * @return
         */
        @Override
        public int compareTo(TopDocsWrapper o) {
            //都指定了order, 按order排序
            if (orderScore != null && o.orderScore != null) {
                return Integer.compare(orderScore.order, o.orderScore.order);
                //前一个指定了order, 后一个没指定, 按order和slot比较. 假如相等, 将指定了order的放前面.
            } else if (orderScore != null && o.orderScore == null) {
                int r = Integer.compare(orderScore.order - 1, o.slot);
                return r == 0 ? -1 : r;
                //前一个没指定了order, 后一个指定, slot和order比较. 假如相等, 将指定了order的放前面.
            } else if (orderScore == null && o.orderScore != null) {
                int r = Integer.compare(slot, o.orderScore.order - 1);
                return r == 0 ? 1 : r;
            } else {
                //没有指定order的, 按score
                return -Float.compare(scoreDoc.score, o.scoreDoc.score);
            }
        }
    }

}
