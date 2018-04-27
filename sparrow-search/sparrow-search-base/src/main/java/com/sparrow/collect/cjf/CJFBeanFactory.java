package com.sparrow.collect.cjf;

import com.sparrow.collect.cjf.impl.ChineseJFImpl;

public class CJFBeanFactory {

    private static ChineseJF chineseJF = new ChineseJFImpl();

    public static ChineseJF getChineseJF() {
        return chineseJF;
    }
}
