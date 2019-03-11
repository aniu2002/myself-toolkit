package com.sparrow.collect.index.config;

import com.sparrow.collect.index.format.DefaultStringFormat;
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
    private static final StringFormat DEFAULT_FORMAT = new DefaultStringFormat();
    private String index;
    private String alias;
    private String dataPath;
    private String configFile;
    private Map<String, Analyzer> analyzers;
    private Map<String, StringFormat> formats;
    private Map<String, FieldSetting> fields;
    private Analyzer analyzer;

    public StringFormat getStringFormat(String fieldName) {
        return formats == null ? DEFAULT_FORMAT : formats.getOrDefault(fieldName,DEFAULT_FORMAT);
    }

    public FieldSetting getFieldSetting(String fieldName) {
        return fields == null ? null : fields.get(fieldName);
    }
}
