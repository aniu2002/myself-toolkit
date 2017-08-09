package com.sparrow.collect.crawler.conf.format;

/**
 * Created by Administrator on 2016/12/6.
 */
public class FieldMap {
    // bean的field名
    private String name;
    // 表达式 为1时，是html dom 的css 表达式
    private String express;
    // 0 custom set,1 element text ，2  attribute value ,
    // 3 crawler data , 4 site entry data , 5 page entry
    private int expressType = 1;
    private String type = "string";

    public FieldMap() {
    }

    FieldMap(String name, String express) {
        this.name = name;
        this.express = express;
    }

    FieldMap(String name, String express, int expressType) {
        this.name = name;
        this.express = express;
        this.expressType = expressType;
    }

    public int getExpressType() {
        return expressType;
    }

    public void setExpressType(int expressType) {
        this.expressType = expressType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpress() {
        return express;
    }

    public void setExpress(String express) {
        this.express = express;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
