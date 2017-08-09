package org.apache.lucene.analysis.exactik;

import java.io.Reader;
import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.Version;

/**
 * Created with IntelliJ IDEA.
 * User: zhanghang
 * Date: 2016/3/2
 * Time: 15:20
 * 逗号分词
 */
public class CommaTokenizer extends CharTokenizer{

    /**
     * 英文逗号，中文逗号
     */
    private static final char[] chars = {',', '，'};

    public CommaTokenizer(Version matchVersion, Reader input) {
        super(matchVersion, input);
    }

    @Override
    protected boolean isTokenChar(int c) {
        return !(c == Character.codePointAt(chars, 0) || c == Character.codePointAt(chars, 1));
    }
}
