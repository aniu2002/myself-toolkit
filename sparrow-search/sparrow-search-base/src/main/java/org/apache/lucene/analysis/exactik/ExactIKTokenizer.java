package org.apache.lucene.analysis.exactik;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.exactik.core.IKArbitrator;
import org.apache.lucene.analysis.exactik.core.Lexeme;
import org.apache.lucene.analysis.exactik.core.QuickSortSet;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.dictionary.ik.IKDict;
import org.apache.lucene.dictionary.ik.IKHit;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by yangtao on 2016/2/16.
 */
public final class ExactIKTokenizer extends Tokenizer{
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);

    private IKDict dict;
    private LinkedList<Lexeme> results;
    private Iterator<Lexeme> iterator = null;

    private IKArbitrator arbitrator;

    private boolean useSmart = false;

    public ExactIKTokenizer(Reader input, IKDict dict) {
        super(input);
        this.dict = dict;
        this.arbitrator = new IKArbitrator();
    }

    public void setUseSmart(boolean useSmart) {
        this.useSmart = useSmart;
    }

    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes();
        if (results == null) {
            results = analyze();
            iterator = results.iterator();
        }
        if (!iterator.hasNext()) {
            results = null;
            iterator = null;
            return false;
        }
        Lexeme lexeme = iterator.next();
        //将Lexeme转成Attributes
        //设置词元文本
        termAtt.append(lexeme.getLexemeText());
        //设置词元长度
        termAtt.setLength(lexeme.getLength());
        //设置词元位移
        offsetAtt.setOffset(lexeme.getBeginPosition(), lexeme.getEndPosition());
        //记录词元分类
        typeAtt.setType(lexeme.getLexemeTypeString());
        //返会true告知还有下个词元
        return true;
    }

    /**
     * 从reader中读取出字符
     * @return
     * @throws IOException
     */
    public char[] readChars() throws IOException {
        //每次从reader中读取字符个数
        final int BUFFER_SIZE = 16;
        char[] buff = new char[BUFFER_SIZE];
        int num = input.read(buff);
        if(num == -1) {
            return null;
        }
        int count = num;
        char[] chars = Arrays.copyOf(buff, count);
        if((num == BUFFER_SIZE)) {
            while ((num = input.read(buff)) != -1) {
                int newCount = count + num;
                //扩容
                chars = Arrays.copyOf(chars, newCount);
                //复制新数据
                System.arraycopy(buff, 0, chars, count, num);
                count = newCount;
            }
        }
        return chars;
    }

    public LinkedList<Lexeme> analyze() throws IOException {
        char[] chars = readChars();
        if(chars == null) {
            return new LinkedList<Lexeme>();
        }
        QuickSortSet lexemes = analyzeWithDict(chars);
        LinkedList<Lexeme> _lexemes = arbitrator.process(lexemes, this.useSmart);
        return _lexemes;
    }

    public QuickSortSet analyzeWithDict(char[] chars) {
        QuickSortSet lexemes = new QuickSortSet();
        final int MATCH_LENGTH = 1;
        for(int i=0; i<chars.length; i++) {
            IKHit hit = dict.match(chars, i, MATCH_LENGTH);
            if(hit.isMatch()) {
                addLexeme(lexemes, chars, hit);
            }
            while (hit.isPrefix()) {
                int nextPos = hit.getEnd() + 1;
                if(nextPos >= chars.length) {
                    break;
                }
                hit = hit.getMatchedDictSegment().match(chars, nextPos, MATCH_LENGTH, hit);
                if(hit.isMatch()) {
                    addLexeme(lexemes, chars, hit);
                }
            }
        }
        return lexemes;
    }

    private void addLexeme(QuickSortSet lexemes, char[] chars, IKHit hit) {
        int begin = hit.getBegin();
        int end = hit.getEnd() + 1;
        int length = end - begin;
        Lexeme lexeme = new Lexeme(0, begin, length, Lexeme.TYPE_CNWORD);
        lexeme.setLexemeText(String.valueOf(chars, begin, length));
        lexemes.addLexeme(lexeme);
    }
}
