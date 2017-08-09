package org.apache.lucene.analysis.exactik;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.Version;

/**
 * Created with IntelliJ IDEA.
 * User: zhanghang
 * Date: 2016/3/2
 * Time: 15:11
 * 逗号分词器，用于处理联系电话有多个，逗号分隔的情况
 */
public class CommaAnalyzer extends Analyzer {

    private Version matchVersion;

    public CommaAnalyzer(Version matchVersion) {
        this.matchVersion = matchVersion;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        return new TokenStreamComponents(new CommaTokenizer(matchVersion, reader));
    }
}
