package org.apache.lucene.analysis;

import org.apache.lucene.dictionary.ik.IKDictSegment;
import org.wltea.analyzer.lucene.IKTokenizer;

import java.io.Reader;

/**
 * Created by yangtao on 2016/2/4.
 */
public class SpecifiedDictAnalyzer extends Analyzer {
    private IKDictSegment specifiedDict;

    public SpecifiedDictAnalyzer(IKDictSegment dict) {
        this.specifiedDict = dict;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer ikTokenizer = new IKTokenizer(reader , true);
        TokenFilter specifiedDictFilter = new SpecifiedDictFilter(ikTokenizer, this.specifiedDict);
        return new TokenStreamComponents(ikTokenizer, specifiedDictFilter);
    }
}
