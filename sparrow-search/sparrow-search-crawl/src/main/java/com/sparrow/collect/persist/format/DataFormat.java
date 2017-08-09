package com.sparrow.collect.persist.format;

/**
 * Created by Administrator on 2017/7/29 0029.
 */
public interface DataFormat<D> {
    Object[] format(D d);
}
