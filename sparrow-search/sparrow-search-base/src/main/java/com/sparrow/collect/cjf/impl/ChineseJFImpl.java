package com.dili.dd.searcher.basesearch.common.cjf.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.dili.dd.searcher.basesearch.common.cjf.ChineseJF;
import com.dili.dd.searcher.basesearch.common.cjf.config.LoadConfig;
import com.dili.dd.searcher.basesearch.common.cjf.entity.Char;
import com.dili.dd.searcher.basesearch.common.cjf.utils.CharFilter;
import com.dili.dd.searcher.basesearch.common.cjf.utils.IOUtil;

public class ChineseJFImpl implements ChineseJF {

    public static Char[] charMapList_Fan2Jan = null;

    public boolean initialized() {
        if (charMapList_Fan2Jan != null) {
            return true;
        }
        return false;
    }

    public void init() {
        if (!initialized())
            try {
                charMapList_Fan2Jan = LoadConfig.getInstance().loadFJmapUTF8();
            } catch (IOException e) {
                charMapList_Fan2Jan = null;
                throw new RuntimeException(e);
            }
    }

    public String chineseFan2Jan(String fanText) {
        if (fanText == null) {
            return null;
        }
        if (!initialized()) {
            init();
        }
        StringBuffer sb = new StringBuffer();
        int textSize = fanText.length();
        for (int index = 0; index < textSize; index++) {
            sb.append(CharFilter.fan2Jan(fanText.charAt(index),
                    charMapList_Fan2Jan));
        }
        return sb.toString();
    }

    public long chineseFan2Jan(File sourceFile, String sourceCharSet,
            File targetFile, String targetCharSet) throws IOException {
        if (!initialized()) {
            init();
        }
        char[] readCash = new char[2000];
        char[] writCash = new char[2000];
        int readSize = 0;

        long staTime = new Date().getTime();

        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = IOUtil.getReader(sourceFile, sourceCharSet);
            bw = IOUtil.getWriter(targetFile, targetCharSet, true);
            while ((readSize = br.read(readCash, 0, 2000)) != -1) {
                for (int index = 0; index < readSize; index++) {
                    writCash[index] = CharFilter.fan2Jan(readCash[index],
                            charMapList_Fan2Jan);
                }
                bw.write(writCash, 0, readSize);
                bw.flush();
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            readCash = (char[]) null;
            writCash = (char[]) null;
            try {
                if (bw != null)
                    bw.close();
            } catch (Exception localException) {
            }
            try {
                if (br != null)
                    br.close();
            } catch (Exception localException1) {
            }

        }
        long endTime = new Date().getTime();
        return endTime - staTime;
    }

    public void free() {
        charMapList_Fan2Jan = null;
    }
}
