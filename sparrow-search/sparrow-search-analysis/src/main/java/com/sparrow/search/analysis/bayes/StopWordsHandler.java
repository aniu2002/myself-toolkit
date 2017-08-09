package com.sparrow.search.analysis.bayes;

/**
 * Project Name: icloudunion
 * Package Name: com.sparrow.search.analysis.bayes
 * Author : YZC
 * Date: 2017/1/24
 * Time: 14:54
 */
public class StopWordsHandler {
    private static String stopWordsList[] ={"的", "我们","要","自己","之","将","“","”","，","（","）","后","应","到","某","后","个","是","位","新","一","两","在","中","或","有","更","好",""};//常用停用词
    public static boolean IsStopWord(String word)
    {
        for(int i=0;i<stopWordsList.length;++i)
        {
            if(word.equalsIgnoreCase(stopWordsList[i]))
                return true;
        }
        return false;
    }
}
