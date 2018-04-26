package com.dili.dd.searcher.basesearch.common.analyze.support;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.dili.dd.searcher.basesearch.common.analyze.IAnalyze;
import com.dili.dd.searcher.basesearch.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;


public class WhiteSpaceAnalyze implements IAnalyze {

    private Log log = LogFactory.getLog(WhiteSpaceAnalyze.class);

    private WhitespaceAnalyzer whiteSpaceAnalyzer = new WhitespaceAnalyzer(Version.LUCENE_46);

    @Override
    public List<String> split(String s) {

        if (StringUtils.isBlank(s)) {
            return null;
        }

        s = StringUtil.removeSpecialCharsNotSpaceByType(s);
        TokenStream tokenStream = null;
        List<String> ret = new LinkedList<String>();
        try {
            tokenStream = whiteSpaceAnalyzer.tokenStream("content", s);
            CharTermAttribute ch = tokenStream
                    .addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                tokenStream.getAttribute(CharTermAttribute.class);
                ret.add(ch.toString());
            }
        } catch (IOException e) {

            log.fatal(e);
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
