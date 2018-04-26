package com.dili.dd.searcher.basesearch.common.analyze.support;

import com.dili.dd.searcher.basesearch.common.analyze.IAnalyze;
import com.dili.dd.searcher.basesearch.common.util.StringUtil;
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
public class SmartIKAnalyze implements IAnalyze {

    private Log log = LogFactory.getLog(SmartIKAnalyze.class);

    private IKAnalyzer analyze = new IKAnalyzer(true);

    @Override
    public List<String> split(String s) {
        s = StringUtil.removeSpecialCharsNotSpaceByType(s);
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
