package com.sparrow.app.common;

/**
 * Created by yuanzc on 2015/8/21.
 */
public class SpeEnvironment {
    private static final ThreadLocal<InfoHolder> holderLocal = new ThreadLocal<InfoHolder>();

    public static final InfoHolder getInfoHolder() {
        return holderLocal.get();
    }

    public static final void setInfoHolder(InfoHolder infoHolder) {
        holderLocal.set(infoHolder);
    }

}
