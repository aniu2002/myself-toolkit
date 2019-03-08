package com.sparrow.collect.index.config;

import com.sparrow.collect.index.format.StringFormat;
import lombok.Getter;
import lombok.Setter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field;

/**
 * Created by Administrator on 2019/2/20 0020.
 */
@Setter
@Getter
public class FieldSetting {
    private String name;
    private FieldType type;
    /**
     * string format , default is null
     */
    private StringFormat format;
    /**
     * 是否存储字段值
     */
    private Field.Store store;
    /**
     * 分词器
     */
    private Analyzer analyzer;
    /**
     * 描述信息
     */
    private String description;
    /**
     * 权重设置
     */
    private float boost;
}
