package com.sparrow.data.tools.message;

/**
 * Created by Administrator on 2016/3/15 0015.
 */
public abstract class ProcessMessageHolder {
    private static ThreadLocal<ProcessMessage> local = new ThreadLocal<ProcessMessage>();

    public static void set(ProcessMessage par) {
        if (par != null)
            local.set(par);
    }

    public static ProcessMessage get() {
        return local.get();
    }

    public static void progressNotify(int step, String msg) {
        ProcessMessage processMessage = get();
        if (processMessage != null)
            processMessage.notify(step, msg);
    }

    public static void clear() {
        local.set(null);
        local.remove();
    }
}
