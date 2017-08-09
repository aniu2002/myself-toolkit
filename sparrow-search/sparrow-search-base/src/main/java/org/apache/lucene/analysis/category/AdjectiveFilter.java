package org.apache.lucene.analysis.category;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.dictionary.ik.IKDict;
import org.apache.lucene.dictionary.ik.IKHit;

import java.io.IOException;

/**
 * Created by yangtao on 2016/2/3.
 */
public final class AdjectiveFilter extends TokenFilter {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);

    private IKDict adjectiveDict;

    private IKDict filterDict;

    protected AdjectiveFilter(TokenStream input, IKDict adjectiveDict) {
        this(input, adjectiveDict, null);
    }

    protected AdjectiveFilter(TokenStream input, IKDict adjectiveDict, IKDict filterDict) {
        super(input);
        this.adjectiveDict = adjectiveDict;
        this.filterDict = filterDict;
    }

    @Override
    public boolean incrementToken() throws IOException {
        if(input.incrementToken()) {
            if(!isFilterWords()) {
                removeAdjective();
            }
            return true;
        }
        return false;
    }

    public boolean isFilterWords() {
        if(filterDict == null) {
            return false;
        }
        char[] chars = termAtt.buffer();
        int length = termAtt.length();
        IKHit hit = filterDict.match(chars, 0, length);
        return hit.isMatch();
    }

    public void removeAdjective() {
        IKHit maxHit = matchHeadAdjective();
        if(maxHit.isMatch()) {
            char[] chars = termAtt.buffer();
            int length = termAtt.length();
            int begin = maxHit.getEnd() + 1;
            termAtt.setEmpty();
            termAtt.copyBuffer(chars, begin, length - begin);
            int start = offsetAtt.startOffset() + begin;
            int end = offsetAtt.endOffset();
            offsetAtt.setOffset(start, end);
        }
    }

    public IKHit matchHeadAdjective() {
        char[] chars = termAtt.buffer();
        final int MATCH_LENGTH = 1;
        IKHit hit = adjectiveDict.match(chars, 0, MATCH_LENGTH);
        IKHit maxHit = hit;
        while (hit.isPrefix()) {
            hit = adjectiveDict.match(chars, hit.getEnd() + 1, MATCH_LENGTH, hit);
            if(hit.isMatch()) {
                maxHit = hit;
            }
        }
        return maxHit;
    }
}
