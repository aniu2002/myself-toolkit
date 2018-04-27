package com.sparrow.collect.analyze.support;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.utils.StringKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yaobo on 2014/10/30.
 */
public class IKAnalyze implements IAnalyze {

    private Log log = LogFactory.getLog(IKAnalyze.class);

    private IKAnalyzer analyze = new IKAnalyzer();

    @Override
    public List<String> split(String s) {

        if (StringUtils.isBlank(s)) {
            return null;
        }

        s = StringKit.removeSpecialCharsNotSpaceByType(s);
        List<String> ret = new LinkedList<>();
        TokenStream tokenStream = null;
        try {
            tokenStream = analyze.tokenStream("", s);
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
