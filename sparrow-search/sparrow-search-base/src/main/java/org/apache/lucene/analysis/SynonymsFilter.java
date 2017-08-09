package org.apache.lucene.analysis;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.dictionary.SynonymsDic;

import java.io.IOException;
import java.util.Stack;

/**
 * Created by yangtao on 2015/12/30.
 */
public final class SynonymsFilter extends TokenFilter {
    private Stack<String> synonyms;
    private State current;

    private final CharTermAttribute termAtt;
    private final PositionIncrementAttribute posIncrAtt;

    public SynonymsFilter(TokenStream input) {
        super(input);
        this.termAtt = addAttribute(CharTermAttribute.class);
        this.posIncrAtt = addAttribute(PositionIncrementAttribute.class);
        this.synonyms = new Stack<String>();
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (this.synonyms.size() > 0) {
            String synonym = this.synonyms.pop();
            restoreState(current);
            termAtt.copyBuffer(synonym.toCharArray(), 0, synonym.length());
            posIncrAtt.setPositionIncrement(0);
            return true;
        }
        //无term
        if (!input.incrementToken()) {
            return false;
        }
        //有term
        if (addAliasesToStack()) {
            current = captureState();
        }
        return true;
    }

    private boolean addAliasesToStack() throws IOException {
        String term = termAtt.toString();
        String[] synonyms = SynonymsDic.getInstance().get(term);
        if (synonyms == null) {
            return false;
        }
        for (String synonym : synonyms) {
            if(term.equals(synonym)) {
                continue;
            }
            this.synonyms.push(synonym);
        }
        return true;
    }
}
