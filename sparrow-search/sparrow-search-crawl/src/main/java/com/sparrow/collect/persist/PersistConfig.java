package com.sparrow.collect.persist;

import com.sparrow.collect.crawler.conf.format.FieldMap;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/29 0029.
 */
public class PersistConfig {
    //sql插入语句
    private String sql;
    // 其他额外参数
    private Map<String, String> props;
    // sql参数对应参数取值设置
    private List<FieldMap> fields;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Map<String, String> getProps() {
        return props;
    }

    public void setProps(Map<String, String> props) {
        this.props = props;
    }

    public List<FieldMap> getFields() {
        return fields;
    }

    public void setFields(List<FieldMap> fields) {
        this.fields = fields;
    }
}
