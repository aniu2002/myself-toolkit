package org.apache.lucene.dictionary;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangtao on 2016/2/2.
 */
public class SpecialWordsDic {
    private Map<String, String[]> wordsMap;

    private static SpecialWordsDic instance = new SpecialWordsDic();

    private SpecialWordsDic() {}

    public static SpecialWordsDic getInstance() {
        return instance;
    }

    public Map<String, String[]> getDict() {
        return wordsMap;
    }

    public boolean contains(String words) {
        return this.wordsMap.containsKey(words);
    }

    public String[] getRefWords(String words) {
        return this.wordsMap.get(words);
    }

    public void init() {
        System.out.println("初始化特殊词库开始");
        String path = getLibPath();
        Map<String, String[]> wordsMap = null;
        try {
            wordsMap = loadFromFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(wordsMap == null) {
            wordsMap = new HashMap<String, String[]>();
        }
        this.wordsMap = wordsMap;
        System.out.println("初始化特殊词库接收");
    }

    public String getLibPath() {
        return "library/specialWords.dic";
    }

    public Map<String, String[]> loadFromFile(String path) throws IOException {
        Map<String, String[]> wordsMap = new HashMap<String, String[]>();
        String line = null;
        InputStream in = null;
        BufferedReader br = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream(path);
            br = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
            while ((line = br.readLine()) != null) {
                if(StringUtils.isBlank(line)) {
                    continue;
                }
                line = line.trim();
                if(line.startsWith("#")) {
                    continue;
                }
                line = line.replaceAll("\\s", "");
                String[] keyValues = split(line);
                if(keyValues.length < 2) {
                    continue;
                }
                wordsMap.put(keyValues[0].toString(), keyValues[1].split(",", -1));
            }
        } finally{
            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return wordsMap;
    }

    public String[] split(String value) {
        return value.split("=>", -1);
    }
}
