package com.sparrow.collect.website.score;

/**
 * 外部数据源, V必须是Comparable.
 * Created by yaobo on 2014/8/18.
 */
public interface ExternalDataSource<K, V extends Comparable> {
    public V getValue(K k);
}
