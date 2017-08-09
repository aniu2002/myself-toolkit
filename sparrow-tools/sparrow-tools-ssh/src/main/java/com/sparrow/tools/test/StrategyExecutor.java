package com.sparrow.tools.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2016/11/30.
 */
public class StrategyExecutor {
    private static final Map<String, IHandler> handlers = new ConcurrentHashMap<String, IHandler>();

    static {
        handlers.put("default",new DefaultIHandler());
    }

    public static void regHandler(String alias, IHandler handler) {
        handlers.put(alias, handler);
    }

    public void execute(String alias,Map<String,String> param){
        handlers.get(alias).doExecute(param);
    }
}
