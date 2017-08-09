package com.sparrow.collect.utils;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.*;

/**
 * <p>
 * Title: HighLightTool
 * </p>
 * <p>
 * Description: com.eweb.article.lucene.tools
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: HR
 * </p>
 *
 * @author Yzc
 * @version 1.0
 * @date 2009-10-29上午12:20:40
 */
public class HighLightTool {

    public static String highLight(Analyzer analyzer, Query query, String text,
                                   String field) throws IOException, InvalidTokenOffsetsException {
        String result = "";
        SimpleHTMLFormatter sHtmlF = new SimpleHTMLFormatter(
                "<b><font color=\'red\'>", "</font></b>");
        Highlighter highlighter = new Highlighter(sHtmlF,
                new QueryScorer(query));
        highlighter.setTextFragmenter(new SimpleFragmenter(200));
        TokenStream tokenStream;
        tokenStream = analyzer.tokenStream(field, new StringReader(text));

        result = highlighter.getBestFragments(tokenStream, text, 1, "...");

        if (result == null || result.equals("")) {
            result = text;
            if (text != null && text.length() > 200) {
                result = text.substring(0, 200) + "...";
            }
        }
        return result;
    }
}
