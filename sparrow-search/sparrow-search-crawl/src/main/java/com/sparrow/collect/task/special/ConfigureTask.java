package com.sparrow.collect.task.special;

import com.sparrow.collect.crawler.ConfiguredCrawlerBuilder;
import com.sparrow.collect.crawler.conf.ConfigWrap;
import com.sparrow.collect.task.AbstractTask;
import com.sparrow.collect.task.Context;
import com.sparrow.collect.utils.FileIOUtil;
import com.sparrow.collect.utils.JsonMapper;

public class ConfigureTask extends AbstractTask {
    @Override
    public void execute(Context ctx) {
        String configPath = ctx.get("crawler.config");
        ConfigWrap configWrap = JsonMapper.bean(FileIOUtil.readString(configPath), ConfigWrap.class);
        new ConfiguredCrawlerBuilder(configWrap).build().exec();
    }

    @Override
    protected Context initContext() {
        Context ctx = new Context();
        ctx.set("crawler.config", "classpath:crawler-config.json");
        return ctx;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Context ctx = new Context();
        ctx.set("crawler.config", "classpath:crawler-config.json");
        long time = System.currentTimeMillis();
        new ConfigureTask().execute(ctx);
        time = System.currentTimeMillis() - time;
        System.out.println(String.format("cost : %s s", time / 1000));
    }
}
