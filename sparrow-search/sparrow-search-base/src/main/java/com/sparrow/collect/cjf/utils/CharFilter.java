package com.dili.dd.searcher.basesearch.common.cjf.utils;

import com.dili.dd.searcher.basesearch.common.cjf.entity.Char;

public class CharFilter {

    public static final char fan2Jan(char fan, Char[] charMapList_Fan2Jan) {
        int fanId = fan;
        int staIndex = 0;
        int endIndex = charMapList_Fan2Jan.length - 1;
        int midIndex = (staIndex + endIndex) / 2;

        while ((staIndex < midIndex) && (midIndex < endIndex)) {
            if (charMapList_Fan2Jan[midIndex].fId == fanId) {
                return charMapList_Fan2Jan[midIndex].jChar;
            }
            if (charMapList_Fan2Jan[midIndex].fId > fanId) {
                endIndex = midIndex;
                midIndex = (staIndex + endIndex) / 2;
            } else {
                staIndex = midIndex;
                midIndex = (staIndex + endIndex) / 2;
            }
        }
        if (charMapList_Fan2Jan[staIndex].fId == fanId) {
            return charMapList_Fan2Jan[staIndex].jChar;
        }
        if (charMapList_Fan2Jan[endIndex].fId == fanId) {
            return charMapList_Fan2Jan[endIndex].jChar;
        }
        return fan;
    }
}
