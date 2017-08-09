package org.apache.lucene.analysis.category;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.dictionary.SpecialWordsDic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by yangtao on 2016/2/3.
 */
public final class SpecialWordsAnalysisFilter extends TokenFilter {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    private SpecialWordsDic specialWordsDic;

    private Queue<State> states;

    protected SpecialWordsAnalysisFilter(TokenStream input, SpecialWordsDic specialWordsDic) {
        super(input);
        this.specialWordsDic = specialWordsDic;
        this.states = new LinkedList<State>();
    }

    @Override
    public boolean incrementToken() throws IOException {
        if(this.states.isEmpty()) {
            if(input.incrementToken()) {
                this.states.add(captureState());
                String term = termAtt.toString();
                List<State> states = null;
                if(this.specialWordsDic.contains(term)) {
                    //特殊分词处理
                    states = analyzeSpecialWords(term);
                }
                if(states != null && !states.isEmpty()) {
                    this.states.addAll(states);
                }
            }
        }
        if(this.states.isEmpty()) {
            return false;
        }
        State state = this.states.poll();
        restoreState(state);
        return true;
    }

    public List<State> analyzeSpecialWords(String term) {
        String[] specialWordsArray = this.specialWordsDic.getRefWords(term);
        if(specialWordsArray == null || specialWordsArray.length == 0) {
            return null;
        }
        List<State> states = new ArrayList<State>(specialWordsArray.length);
        for(String specialWords : specialWordsArray) {
            clearAttributes();
            termAtt.copyBuffer(specialWords.toCharArray(), 0, specialWords.length());
            states.add(captureState());
        }
        return states;
    }
}
