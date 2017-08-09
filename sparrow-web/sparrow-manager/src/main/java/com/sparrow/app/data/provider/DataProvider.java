package com.sparrow.app.data.provider;

import com.sparrow.app.common.source.Source;

import java.util.List;

/**
 * Created by yuanzc on 2016/1/6.
 */
public class DataProvider {
    private String name;
    private String script;
    private String desc;
    private Class<?> wrapperClass;
    private Source source;

    public Class<?> getWrapperClass() {
        return wrapperClass;
    }

    public void setWrapperClass(Class<?> wrapperClass) {
        this.wrapperClass = wrapperClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public List<?> query(Object data, int page, int limit) {
        if (this.source != null)
            return this.source.query(this.script, data, this.wrapperClass, page, limit);
        else
            return null;
    }

    public List<?> query(Object data) {
        if (this.source != null)
            return this.source.query(this.script, data, this.wrapperClass);
        else
            return null;
    }
}
