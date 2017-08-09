package com.sparrow.collect.store.check;

/**
 * Project Name: sparrow-egg
 * Package Name: com.sparrow.collect.store.check
 * Author : YZC
 * Date: 2016/12/12
 * Time: 18:42
 */
public interface TagCheck {
    boolean checkTag(Object object);
    void saveTag(Object object);
    void close();
}
