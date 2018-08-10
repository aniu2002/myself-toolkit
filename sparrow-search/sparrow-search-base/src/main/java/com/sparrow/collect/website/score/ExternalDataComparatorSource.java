package com.sparrow.collect.website.score;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;

/**
 * 按keyFieldName对应的数据源中的值排序. 真正要排序的字段fieldname, 其值不存储在lucene中, 存储在外部数据源中. 默认倒序
 * K:外部数据源的key类型
 * V:外部数据源的value类型
 * T:lucene中field的类型
 * <p>
 * exp: new Sort(new SortField("sales", new ExternalDataComparatorSource("id", new DataSource(), new ExternalDataComparatorSource.KeyTypeCast<String, Long>()));
 * 创建一个按sales排序的sort, 销量存储在DataSource中. lucene的id域对应着数据源的key, 通过id找到数据源中的sales, 对sales进行排序.
 * <p>
 * Created by yaobo on 2014/8/15.
 */
public class ExternalDataComparatorSource<K, V extends Comparable, T> extends FieldComparatorSource {

    protected Log log = LogFactory.getLog(this.getClass());

    /**
     * lucene中的id FieldName, 对应外部数据源的key
     */
    private String keyFieldName;

    /**
     * 外部数据源
     */
    private ExternalDataSource<K, V> externalDataSource;

    /**
     * 类型转换器, 将lucene的field类型转成数据源的key类型
     */
    private KeyTypeCast<T, K> keyTypeCast;

    /**
     * @param keyFieldName
     * @param externalDataSource
     * @param keyTypeCast
     */
    public ExternalDataComparatorSource(String keyFieldName, ExternalDataSource<K, V> externalDataSource, KeyTypeCast<T, K> keyTypeCast) {
        this.keyFieldName = keyFieldName;
        this.externalDataSource = externalDataSource;
        this.keyTypeCast = keyTypeCast;
    }

    /**
     * @param fieldname doc的id域, 对应datasource的key
     * @param numHits   min(查询年条数, 命中条数)
     * @param sortPos
     * @param reversed  true:asc, false:desc, 本类不需要对reversed做处理. 调用者会处理此参数
     * @return
     * @throws IOException
     */
    @Override
    public FieldComparator<Comparable> newComparator(String fieldname, int numHits, int sortPos, boolean reversed) throws IOException {
        return new ExternalDataComparator(keyFieldName, numHits);
    }

    /**
     * 通过lucene的field value, 查找数据源中的value.
     *
     * @param t
     * @return
     */
    private Comparable getDataSourceValue(T t) {
        K k = keyTypeCast.cast(t);
        if (k == null){
            return null;
        }
        return externalDataSource.getValue(k);
    }

    /**
     * lucene的field类型转成数据源的key类型
     *
     * @param <T>
     * @param <K>
     */
    public interface KeyTypeCast<T, K> {
        public K cast(T t);
    }

    @Override
    public String toString() {
        return "ExternalDataComparatorSource{" +
                "keyFieldName='" + keyFieldName + '\'' +
                '}';
    }

    private class ExternalDataComparator<V> extends FieldComparator {

        /**
         * 缓存当前reader中的terms
         */
        private BinaryDocValues terms;

        private String fieldName;

        //数组中的值是无序的. bottom指向最小的那个值. 不能通过values的pos判断val大小. 但外部list是有序的, 记录着values的pos和val
        private Comparable values[];

        private Comparable bottom;

        /**
         *
         * @param fieldName doc的id域, 对应datasource的key
         * @param numHits min(查询年条数, 命中条数)
         */
        private ExternalDataComparator(String fieldName, int numHits) {
            this.fieldName = fieldName;
            values = new Comparable[numHits];
        }

        /**
         * 查询条数多于最大命中条数, 直接比较队列中的数据.
         * @param slot1
         * @param slot2
         * @return
         */
        @Override
        public int compare(int slot1, int slot2) {
            Comparable val1 = value(slot1);
            Comparable val2 = value(slot2);
            return compare(val1, val2);
        }

        /**
         * 如果查询结果超过了我们设定的总行数那么会第一步调用这个方法.
         * 设置排序队列底部的值.
         * @param slot
         */
        @Override
        public void setBottom(int slot) {
            bottom = value(slot);
        }

        /**
         * 查询条数少于最大命中条数, 超过的那部分数据调用此方法比较.
         * @param doc
         * @return
         * @throws IOException
         */
        @Override
        public int compareBottom(int doc) throws IOException {
            Comparable val1 = bottom;
            T field = getIdFieldByDoc(doc);
            Comparable val2 = getDataSourceValue(field);
            return compare(val1, val2);
        }

        /**
         * 切换reader后, 如果reader有命中的doc,调用此方法.将doc对应的数据源的value拷贝到values中.
         *
         * @param slot 存放的位置
         * @param doc doc
         * @throws IOException
         */
        @Override
        public void copy(int slot, int doc) throws IOException {
            T field = getIdFieldByDoc(doc);
            values[slot] = getDataSourceValue(field);
//            log.debug("copy=" + slot + ";" + doc + ";" + value(slot));
//            System.out.println("copy=" + slot + ";" + doc + ";" + value(slot));
        }

        /**
         * 每次切换reader时,调用此方法.
         * 缓存keyFieldName的所有值.
         *
         * @param context
         * @return
         * @throws IOException
         */
        @Override
        public FieldComparator<Comparable> setNextReader(AtomicReaderContext context) throws IOException {
            terms = FieldCache.DEFAULT.getTerms(context.reader(), fieldName, false);
            return this;
        }

        /**
         *  排序时按位置查找value
         * @param slot 查找的位置
         * @return
         */
        @Override
        public Comparable value(int slot) {
            return values[slot];
        }

        /**
         * Returns negative result if the doc's value is less than the provided value.
         */
        @Override
        public int compareDocToValue(int doc, Object value) throws IOException {
//            TopFieldCollector.create()
//            log.debug("compareDocToValue=" + doc + ";" + value);
            return 0;
        }

        /**
         * 根据doc获取keyFieldName的值
         * @param doc
         * @return
         */
        private T getIdFieldByDoc(int doc){
            BytesRef byteRef = new BytesRef();
            terms.get(doc, byteRef);
            String id = byteRef.utf8ToString();
            return (T) id;
        }

        /**
         * 比较两个compare的大小. 按倒序比较.
         * @param val1
         * @param val2
         * @return 大于返回1, 小于返回-1. reversed=true, 会对值取反.
         */
        private int compare(Comparable val1, Comparable val2){
            if (val1 != null && val2 != null) {
                return (val1.compareTo(val2));
            } else if (val1 != null && val2 == null) {
                return 1;
            } else if (val1 == null && val2 != null) {
                return -1;
            } else {
                return 0;
            }
        }
    }

}
