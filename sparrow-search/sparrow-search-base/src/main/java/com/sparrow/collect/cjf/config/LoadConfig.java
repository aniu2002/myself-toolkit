package com.dili.dd.searcher.basesearch.common.cjf.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.dili.dd.searcher.basesearch.common.cjf.entity.Char;

public class LoadConfig {

    public static final String fjMapUTF8 = "fj_map_utf8.properties";
    private static LoadConfig instance = null;

    public static LoadConfig getInstance() {
        if (instance == null) {
            instance = new LoadConfig();
        }
        return instance;
    }

    public Char[] loadFJmapUTF8() throws IOException {
        String line = null;
        ArrayList list = new ArrayList();
        BufferedReader br = null;
        Char[] charList = (Char[]) null;
        try {
            br = getReader("fj_map_utf8.properties");
            int index = 0;
            while ((line = br.readLine()) != null)
                if (!line.startsWith("#")) {
                    if (line.trim().length() != 0) {
                        char fChar = line.charAt(0);
                        char jChar = line.charAt(2);
                        if (index >= fChar) {
                            list = null;
                            throw new IOException(
                                    "对不起，您的配置文件[fj_map_utf8.properties]有问题！FCharId="
                                            + fChar);
                        }
                        index = fChar;
                        list.add(new Char(jChar, fChar));
                    }
                }
            charList = new Char[list.size()];
            for (int i = 0; i < list.size(); i++) {
                charList[i] = ((Char) list.get(i));
            }
            list = null;
        } catch (IOException ex) {
            list = null;
            throw ex;
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException localIOException1) {
                }

        }
        return charList;
    }

    protected BufferedReader getReader(String file) {
        BufferedReader bufferedReader = null;
        try {
            InputStream is = getClass().getResourceAsStream(file);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            bufferedReader = new BufferedReader(isr);
        } catch (IOException ex) {
            bufferedReader = null;
            throw new RuntimeException(ex);
        }
        return bufferedReader;
    }
}
