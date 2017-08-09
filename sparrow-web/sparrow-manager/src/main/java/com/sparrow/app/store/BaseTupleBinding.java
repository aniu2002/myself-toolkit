package com.sparrow.app.store;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-4-9 Time: 下午7:44 To change this
 * template use File | Settings | File Templates.
 */
public abstract class BaseTupleBinding<T> extends com.sleepycat.bind.tuple.TupleBinding<T> {

    public abstract Class<T> getBindingClass();

}
