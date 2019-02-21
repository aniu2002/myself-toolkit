package com.sparrow.collect.config;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.format.StringFormat;
import lombok.Getter;
import lombok.Setter;

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
    private String logsPath;
    private String configFile;
    private Map<String,IAnalyze> analyzers;
    private Map<String,StringFormat> formats;
    private Map<String,FieldSetting> fields;
}
