package com.sparrow.collect.analyze.support;

import com.sparrow.collect.analyze.IAnalyze;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yangtao on 2016/2/4.
 */
public abstract class Analyze implements IAnalyze {
    protected Log log = LogFactory.getLog(this.getClass());

    @Override
    public List<String> split(String s) {
//        s = StringUtil.removeSpecialCharsNotSpaceByType(s);
        List<String> ret = new LinkedList<>();
        TokenStream tokenStream = null;
        try {
            tokenStream = getAnalyzer().tokenStream("", s);
            CharTermAttribute ch = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                tokenStream.getAttribute(CharTermAttribute.class);
                ret.add(ch.toString());
            }
        } catch (Exception ex) {
            log.error(ex);
        } finally {
            try {
                tokenStream.end();
                tokenStream.close();
            } catch (IOException e) {
                log.error(e);
                return null;
            }
        }
        return ret;
    }

    public abstract Analyzer getAnalyzer();
}
