package org.apache.lucene.analysis;

import org.wltea.analyzer.lucene.IKTokenizer;

import java.io.Reader;

/**
 * Created by yangtao on 2015/12/30.
 */
public class SynonymsAnalyzer extends Analyzer {
    private boolean useSmart;

    public boolean useSmart() {
        return useSmart;
    }

    public void setUseSmart(boolean useSmart) {
        this.useSmart = useSmart;
    }

    public SynonymsAnalyzer() {
        this(false);
    }

    public SynonymsAnalyzer(boolean useSmart) {
        this.useSmart = useSmart;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer _IKTokenizer = new IKTokenizer(reader , this.useSmart());
        TokenStream tokenStream = new SynonymsFilter(_IKTokenizer);
        return new TokenStreamComponents(_IKTokenizer, tokenStream);
    }
}
