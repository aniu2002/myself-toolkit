package com.sparrow.collect.index.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Administrator on 2019/2/20 0020.
 */
@Setter
@Getter
public class FieldMeta {
    private String type;
    /**
     * string format , default is null
     */
    private String format;
    /**
     * 是否存储字段值
     */
    private String store;
    /**
     * 分词器
     */
    private String analyzer;
    /**
     * 描述信息
     */
    private String description;
    /**
     * 权重设置
     */
    private Float boost;
}
