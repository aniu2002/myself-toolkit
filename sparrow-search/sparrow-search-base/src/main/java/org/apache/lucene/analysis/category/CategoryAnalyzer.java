package org.apache.lucene.analysis.category;

import org.apache.lucene.analysis.SynonymsFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.exactik.ExactIKAnalyzer;
import org.apache.lucene.analysis.exactik.ExactIKTokenizer;
import org.apache.lucene.dictionary.AdjectiveDic;
import org.apache.lucene.dictionary.AdjectiveFilterWordsDic;
import org.apache.lucene.dictionary.SpecialWordsDic;
import org.apache.lucene.dictionary.ik.IKDict;

import java.io.Reader;

/**
 * Created by yangtao on 2016/2/4.
 */
public class CategoryAnalyzer extends ExactIKAnalyzer {
    //形容词词库
    private IKDict adjectiveDict;
    //不做形容词处理的词库
    private IKDict filterDict;
    //特殊词组分词词库
    private SpecialWordsDic specialWordsDict;

    public CategoryAnalyzer(IKDict dict, boolean useSmart) {
        this(dict, useSmart, false);
    }

    public CategoryAnalyzer(IKDict dict, boolean useSmart, boolean addSynonyms) {
        super(dict, useSmart, addSynonyms);
        this.adjectiveDict = AdjectiveDic.getInstance();
        this.filterDict = AdjectiveFilterWordsDic.getInstance();
        this.specialWordsDict = SpecialWordsDic.getInstance();
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        ExactIKTokenizer tokenizer = new ExactIKTokenizer(reader, getDict());
        tokenizer.setUseSmart(isUseSmart());
        TokenStream tokenStream = new AdjectiveFilter(tokenizer, this.adjectiveDict, this.filterDict);
        tokenStream = new SpecialWordsAnalysisFilter(tokenStream, this.specialWordsDict);
        if(isAddSynonyms()) {
            tokenStream = new SynonymsFilter(tokenStream);
        }
        return new TokenStreamComponents(tokenizer, tokenStream);
    }
}
