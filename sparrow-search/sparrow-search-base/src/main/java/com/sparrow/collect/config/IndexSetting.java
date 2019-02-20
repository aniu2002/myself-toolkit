package com.sparrow.collect.config;

import java.util.Map;

/**
 * Created by Administrator on 2019/2/20 0020.
 */
public class IndexSetting {
    private String index;
    private Map<String,String> analyzers;
    private Map<String,FieldSetting> fields;
}
