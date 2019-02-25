package com.sparrow.collect.index.format;

import com.sparrow.collect.crawler.conf.format.FormatConfig;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2016/12/2.
 */
public abstract class DataFormatFactory {
    static final DataFormat _df = new DefaultFormat();

    public static final DataFormat dataFormat(FormatConfig config) {
        String className = config.getFormatter();
        if (StringUtils.equals("default", className))
            return new ConfigureDataFormat(config);
        else if (StringUtils.equals("configureDataFormat", className))
            return new ConfigureDataFormat(config);
        else if (StringUtils.equals("configureFormat", className))
            return new ConfigureDataFormat(config);
        else if (StringUtils.equals("array", className))
            return new ArrayDataFormat(config);
        else
            return _df;
    }
}
