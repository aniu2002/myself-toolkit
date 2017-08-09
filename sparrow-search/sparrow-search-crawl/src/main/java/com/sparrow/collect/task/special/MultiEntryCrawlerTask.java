package com.sparrow.collect.task.special;

import com.sparrow.collect.crawler.ConfiguredCrawlerBuilder;
import com.sparrow.collect.crawler.conf.ConfigWrap;
import com.sparrow.collect.task.Context;
import com.sparrow.collect.utils.FileIOUtil;
import com.sparrow.collect.utils.JsonMapper;
import org.apache.commons.lang3.StringUtils;

public class MultiEntryCrawlerTask extends ConfigureTask {
    @Override
    public void execute(Context ctx) {
        String configPath = ctx.get("crawler.config");
        String paths[] = StringUtils.split(configPath, ',');
        for (String p : paths) {
            ConfigWrap configWrap = JsonMapper.bean(FileIOUtil.readString(p), ConfigWrap.class);
            new ConfiguredCrawlerBuilder(configWrap)
                    .build()
                    .exec();
        }
    }
}
