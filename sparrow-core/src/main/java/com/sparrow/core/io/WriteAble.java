package com.sparrow.core.io;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-6-9
 * Time: 下午6:01
 * To change this template use File | Settings | File Templates.
 */
public interface WriteAble {

    public void write(TupleOutput output) throws IOException;

    public void read(TupleInput input) throws IOException;

}
