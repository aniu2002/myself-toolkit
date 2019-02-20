package org.apache.lucene.analysis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.utils.Reflections;
import org.apache.lucene.utils.StringUtil;

import java.io.Reader;
import java.io.StringReader;

/**
 * 格式化分词器, 只保留汉字,英文字母,数字.
 * Created by yaobo on 2014/7/23.
 */
public class FormatAnalyzerWrapper extends Analyzer {
    private Log log = LogFactory.getLog(FormatAnalyzerWrapper.class);

    private Analyzer analyzer;

    public FormatAnalyzerWrapper(Analyzer analyzer) {
        super();
        this.analyzer = analyzer;
    }

//    @Override
//    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
//        if (reader instanceof ReusableStringReader) {
//            ReusableStringReader reusableReader = (ReusableStringReader) reader;
//            String s = (String) Reflections.getFieldValue(reusableReader, "s");
//            //format s, 只保留字母,数字,汉字,空格.
//            // TODO: 因为ansj的停用词只能通过代码显示调用, 且只能对term进行处理. 此处不使用停用词
//            s = StringUtil.removeSpecialCharsNotSpaceByType(s);
//            log.debug("formated key : " + s);
//            ((ReusableStringReader) reader).setValue(s);
//        }
//        return analyzer.createComponents(fieldName, reader);
//    }


    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        return analyzer.createComponents(fieldName, reader);
    }

    /**
     * 过滤掉reader中的特殊字符.
     * 不能在createComponents中进行处理.createComponents的结果会被缓存, 只会调用一次.
     * @param fieldName
     * @param reader
     * @return
     */
    @Override
    protected Reader initReader(String fieldName, Reader reader) {
        if (reader instanceof ReusableStringReader) {
            ReusableStringReader reusableReader = (ReusableStringReader) reader;
            String s = (String) Reflections.getFieldValue(reusableReader, "s");
            //format s, 只保留字母,数字,汉字,空格.
            s = StringUtil.removeSpecialCharsNotSpaceByType(s);
            log.debug("formated key : " + s);
            ((ReusableStringReader) reader).setValue(s);
        }else if (reader instanceof StringReader){
            String s = (String) Reflections.getFieldValue(reader, "str");
            s = StringUtil.removeSpecialCharsNotSpaceByType(s);
            log.debug("formated key : " + s);
            reader = new StringReader(s);
        }
        return analyzer.initReader(fieldName, reader);
    }

    @Override
    public int getPositionIncrementGap(String fieldName) {
        return analyzer.getPositionIncrementGap(fieldName);
    }

    @Override
    public int getOffsetGap(String fieldName) {
        return analyzer.getOffsetGap(fieldName);
    }

    @Override
    public void close() {
        analyzer.close();
    }

    @Override
    public int hashCode() {
        return analyzer.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return analyzer.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return analyzer.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
