package com.sparrow.search.analysis.bayes;

/**
 * Project Name: icloudunion
 * Package Name: com.sparrow.search.analysis.bayes
 * Author : YZC
 * Date: 2017/1/24
 * Time: 14:53
 */

import com.chenlb.mmseg4j.analysis.MMSegAnalyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

/**
 * 中文分词器
 */
public class ChineseSpliter {
    /**
     * 对给定的文本进行中文分词
     *
     * @param text       给定的文本
     * @param splitToken 用于分割的标记,如"|"
     * @return 分词完毕的文本
     */
 /*   public static String split(String text,String splitToken)
    {
        String result = null;
        MMAnalyzer analyzer = new MMAnalyzer();
        try
        {
            result = analyzer.segment(text, splitToken);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }*/
    public static String split(String text, String splitToken) {
        String result = null;
        MMSegAnalyzer analyzer = new MMSegAnalyzer();
        try {
            TokenStream ts = analyzer.tokenStream("s", text);
//            TokenStream ts = analyzer.tokenStream("s", sr);
            //保存相应词汇
            CharTermAttribute cta = ts.addAttribute(CharTermAttribute.class);
            StringBuilder sb = new StringBuilder();
            while (ts.incrementToken()) {
                sb.append(splitToken).append(cta.toString());
            }
            result = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
