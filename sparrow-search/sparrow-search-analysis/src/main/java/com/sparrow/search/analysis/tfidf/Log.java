package com.sparrow.search.analysis.tfidf;

/**
 * Project Name: icloudunion
 * Package Name: com.sparrow.search.analysis
 * Author : YZC
 * Date: 2017/1/24
 * Time: 11:36
 */
public class Log {
    public static float log(float value, float base) {
        return (float) (Math.log(value) / Math.log(base));
    }
}
