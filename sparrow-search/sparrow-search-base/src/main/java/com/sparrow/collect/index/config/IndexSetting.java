package com.sparrow.collect.index.config;

import com.sparrow.collect.index.format.StringFormat;
import lombok.Getter;
import lombok.Setter;
import org.apache.lucene.analysis.Analyzer;

import java.util.Map;

/**
 * Created by Administrator on 2019/2/20 0020.
 */
@Setter
@Getter
public class IndexSetting {
    private String index;
    private String alias;
    private String dataPath;
    private String configFile;
    private Map<String,Analyzer> analyzers;
    private Map<String,StringFormat> formats;
    private Map<String,FieldSetting> fields;
    private Analyzer analyzer;
}
