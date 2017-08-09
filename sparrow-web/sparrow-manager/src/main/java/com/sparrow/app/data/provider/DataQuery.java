package com.sparrow.app.data.provider;

/**
 * Created by yuanzc on 2015/12/31.
 */
public class DataQuery<T> {
    private String script;
    private int page;
    private int limit;
    private T data;
    private Class<?> clazz;

    public DataQuery(String script, T data) {
        this(script, data, 1, 20);
    }

    public DataQuery(String script, T data, int page, int limit) {
        this.script = script;
        this.page = page;
        this.limit = limit;
        this.data = data;
        this.clazz = data.getClass();
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public T getData() {
        return data;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
