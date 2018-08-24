package com.sparrow.collect.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangtao on 2015/12/19.
 */
public class CombineWord {
    private static void subSelect(String[] words, int head, int index, String[] combine, int k, List<String[]> result) {
        for (int i = head; i < words.length + index - k; i++) {
            if (index < k) {
                combine[index - 1] = words[i];
                subSelect(words, i + 1, index + 1, combine, k, result);
            } else if (index == k) {
                combine[index - 1] = words[i];
                result.add(clone(combine));
                subSelect(words, i + 1, index + 1, combine, k, result);
            } else {
                return;
            }
        }
    }

    private static String[] clone(String[] strs) {
        String[] clone = new String[strs.length];
        for(int i=0; i<strs.length; i++) {
            clone[i] = strs[i];
        }
        return clone;
    }

    public static List<String[]> select(String[] words, int k) {
        String[] combine = new String[k];
        List<String[]> result = new ArrayList();
        subSelect(words, 0, 1, combine, k, result);
        return result;
    }
}
