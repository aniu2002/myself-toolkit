package com.szl.icu.miner.tools.log;

/**
 * Created by Administrator on 2016/10/20.
 */
public class DefaultLog implements Log {
    public void info(Object message) {
        System.out.println(String.format("[info] %s", message));
    }

    public void debug(Object message) {
        System.out.println(String.format("[debug] %s", message));
    }
}

