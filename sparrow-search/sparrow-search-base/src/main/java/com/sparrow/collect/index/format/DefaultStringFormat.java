package com.sparrow.collect.index.format;

/**
 * Created by Administrator on 2019/2/25 0025.
 */
public class DefaultStringFormat implements StringFormat {
    @Override
    public String format(String content) {
        String regEx = "[`~!@#$%^&* ()_+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        return content.replaceAll(regEx, " ");
    }
}
