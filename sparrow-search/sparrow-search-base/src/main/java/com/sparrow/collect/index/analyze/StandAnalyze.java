package com.sparrow.collect.index.analyze;

import com.sparrow.collect.utils.StringKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by rhdlzl on 2014/7/8.
 */
public class StandAnalyze implements IAnalyze {

    private Log log = LogFactory.getLog(StandAnalyze.class);

    private StandardAnalyzer standardAnalyzer = new StandardAnalyzer(Version.LUCENE_46);

    @Override
    public List<String> split(String s) {

        if (StringUtils.isBlank(s)) {
            return null;
        }

        s = StringKit.removeSpecialCharsNotSpaceByType(s);
        List<String> ret = new LinkedList<>();
        TokenStream tokenStream = null;
        try {
            tokenStream = standardAnalyzer.tokenStream("content", s);
            CharTermAttribute ch = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                tokenStream.getAttribute(CharTermAttribute.class);
                ret.add(ch.toString());
            }
        } catch (Exception ex) {
            log.fatal(ex);
            return null;
        } finally {
            try {
                tokenStream.end();
                tokenStream.close();
            } catch (IOException e) {
                log.fatal(e);
                return null;
            }

        }
        return ret;

    }
}
