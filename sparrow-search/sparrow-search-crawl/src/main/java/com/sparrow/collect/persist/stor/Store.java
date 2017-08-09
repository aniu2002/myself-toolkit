package com.sparrow.collect.persist.stor;

import com.sparrow.collect.persist.format.DataFormat;

/**
 * Created by Administrator on 2017/7/29 0029.
 */
public interface Store<D> {

    DataFormat<D> getFormat();

    void save(D d);

    void initialize();

    void close();
}
