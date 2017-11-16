package com.szl.icu.miner.tools.data;

/**
 * Created by Administrator on 2016/11/4.
 */
public abstract class ContentType {
    private ContentType() {
    }

    public static String XML = "application/xml;charset=utf-8";
    public static String JSON = "application/json;charset=utf-8";
    public static String FORM = "application/x-www-form-urlencoded;charset=utf-8";
    public static String FILE = "multipart/form-data";
    public static String TEXT = "text/plain;charset=utf-8";
    public static String HTML = "text/html;charset=UTF-8";

}
