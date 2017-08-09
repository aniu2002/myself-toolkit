package com.sparrow.search.analysis.tfidf;

import com.chenlb.mmseg4j.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Iterator;

/**
 * Project Name: icloudunion
 * Package Name: com.sparrow.search.analysis
 * Author : YZC
 * Date: 2017/1/24
 * Time: 11:10
 */
public class WordFrequencyStat {
    public void stat() throws IOException {
        String str = "昨日，中国人民银行宣布，自2011年4月6日起上调金融机构人民币存贷款基准利率。金融机构一年期存贷款基准利率分别上调0.25个百分点，其他各档次存贷款基准利率及个人住房公积金贷款利率相应调整。【加息前后房贷对比图】";
        String text = this.segStr(str, "simple");//切词后结果

        char[] w = new char[501];
        WordsTable wt = new WordsTable();

        try {
            StringReader in = new StringReader(text);
            while (true) {
                int ch = in.read();
                if (Character.isLetter((char) ch)) {
                    int j = 0;
                    while (true) {
                        ch = Character.toLowerCase((char) ch);
                        w[j] = (char) ch;
                        if (j < 500)
                            j++;
                        ch = in.read();
                        if (!Character.isLetter((char) ch)) {

                            String word1 = new String(w, 0, j);

                            if (!wt.isStopWord(word1)) {// 如果不是停用词，则进行统计
                                word1 = wt.getStem(word1);// 提取词干
                                wt.stat(word1);
                            }

                            break;
                        }
                    }
                }
                if (ch < 0)
                    break;

            }

            in.close();
            Iterator<WordCount> iter = wt.getWords();
            while (iter.hasNext()) {
                WordCount wor =   iter.next();
                if (wor.getCount() > 1) {
                    System.out.println(wor.getWord() + "     :     " + wor.getCount());
                }
            }
        } catch (Exception e) {
            System.out.println(e);

        }

    }

    /**
     * @param text
     * @param mode: simple or complex
     * @return
     * @throws IOException
     */
    private String segStr(String text, String mode) throws IOException {
        String returnStr = "";
        Seg seg = null;
        Dictionary dic = Dictionary.getInstance();
        if ("simple".equals(mode)) {
            seg = new SimpleSeg(dic);
        } else {
            seg = new ComplexSeg(dic);
        }

        // String words = seg.
        MMSeg mmSeg = new MMSeg(new InputStreamReader(new ByteArrayInputStream(text.getBytes())), seg);
        Word word = null;
        while ((word = mmSeg.next()) != null) {
            returnStr += word.getString() + " ";
        }

        return returnStr;
    }
}
