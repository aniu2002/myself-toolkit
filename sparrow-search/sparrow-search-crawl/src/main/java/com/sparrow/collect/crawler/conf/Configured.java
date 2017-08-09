package com.sparrow.collect.crawler.conf;

/**
 * Project Name: sparrow-egg
 * Package Name: com.sparrow.collect.crawler.conf
 * Author : YZC
 * Date: 2016/12/13
 * Time: 10:52
 */
public interface Configured<T> {
    void configure(T t);
}
