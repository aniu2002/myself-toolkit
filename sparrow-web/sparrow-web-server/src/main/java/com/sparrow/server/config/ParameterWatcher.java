package com.sparrow.server.config;

/**
 * Created by Administrator on 2015/6/1 0001.
 */
public abstract class ParameterWatcher<T> {
    public abstract void watch(String parameter, T bean);

    public abstract Class<T> accept();
}
