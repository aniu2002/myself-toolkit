package org.apache.lucene.analysis.exactik;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SynonymsFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.dictionary.ik.IKDict;

import java.io.Reader;

/**
 * Created by yangtao on 2016/2/16.
 * 精确依照词库分词
 */
public class ExactIKAnalyzer extends Analyzer {
    private IKDict dict;
    private boolean useSmart;
    private boolean addSynonyms;

    public ExactIKAnalyzer(IKDict dict) {
        this(dict, false, false);
    }

    public ExactIKAnalyzer(IKDict dict, boolean useSmart) {
        this(dict, useSmart, false);
    }

    public ExactIKAnalyzer(IKDict dict, boolean useSmart, boolean addSynonyms) {
        this.dict = dict;
        this.useSmart = useSmart;
        this.addSynonyms = addSynonyms;
    }

    public IKDict getDict() {
        return dict;
    }

    public boolean isUseSmart() {
        return useSmart;
    }

    public boolean isAddSynonyms() {
        return addSynonyms;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        ExactIKTokenizer tokenizer = new ExactIKTokenizer(reader, getDict());
        tokenizer.setUseSmart(isUseSmart());
        TokenStream tokenStream = tokenizer;
        if(isAddSynonyms()) {
            tokenStream = new SynonymsFilter(tokenStream);
        }
        return new TokenStreamComponents(tokenizer, tokenStream);
    }
}
