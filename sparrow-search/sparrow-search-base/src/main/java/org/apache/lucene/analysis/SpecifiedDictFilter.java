package org.apache.lucene.analysis;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.dictionary.ik.IKDictSegment;
import org.apache.lucene.dictionary.ik.IKHit;

import java.io.IOException;

/**
 * Created by yangtao on 2016/2/2.
 */
public final class SpecifiedDictFilter extends TokenFilter {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    private IKDictSegment specifiedDict;

    public SpecifiedDictFilter(TokenStream input, IKDictSegment specifiedDict) {
        super(input);
        this.specifiedDict = specifiedDict;
    }

    @Override
    public boolean incrementToken() throws IOException {
        while (input.incrementToken()) {
            String term = termAtt.toString();
            if(contain(term)) {
                return true;
            }
        }
        return false;
    }

    public boolean contain(String term) {
        IKHit hit = this.specifiedDict.match(term.toCharArray());
        return hit.isMatch();
    }
}
