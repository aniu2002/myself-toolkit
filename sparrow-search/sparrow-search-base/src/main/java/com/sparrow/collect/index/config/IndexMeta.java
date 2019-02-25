package com.sparrow.collect.index.config;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by Administrator on 2019/2/20 0020.
 */
@Setter
@Getter
public class IndexMeta {
    private String index;
    private Map<String,String> analyzers;
    private Map<String,String> formats;
    private Map<String,FieldMeta> fields;
}
