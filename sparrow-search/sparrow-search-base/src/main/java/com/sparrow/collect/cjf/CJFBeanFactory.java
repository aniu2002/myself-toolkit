package com.dili.dd.searcher.basesearch.common.cjf;

import com.dili.dd.searcher.basesearch.common.cjf.impl.ChineseJFImpl;

public class CJFBeanFactory {

    private static ChineseJF chineseJF = new ChineseJFImpl();

    public static ChineseJF getChineseJF() {
        return chineseJF;
    }
}
