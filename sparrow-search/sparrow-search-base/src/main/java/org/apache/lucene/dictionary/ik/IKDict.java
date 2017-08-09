package org.apache.lucene.dictionary.ik;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangtao on 2016/1/26.
 */
public abstract class IKDict {
    protected Log log = LogFactory.getLog(this.getClass());

    private IKDictSegment dict;

    public IKDict() {
        this.dict = new IKDictSegment((char)0);
    }

    public IKDictSegment getDict() {
        return dict;
    }

    /**
     * 匹配词段
     * @param charArray
     * @return
     */
    public IKHit match(char[] charArray){
        return this.dict.match(charArray , 0 , charArray.length , null);
    }

    /**
     * 匹配词段
     * @param charArray
     * @param begin
     * @param length
     * @return IKHit
     */
    public IKHit match(char[] charArray , int begin , int length){
        return this.dict.match(charArray, begin, length, null);
    }

    /**
     * 匹配词段
     * @param charArray
     * @param begin
     * @param length
     * @param searchHit
     * @return IKHit
     */
    public IKHit match(char[] charArray , int begin , int length , IKHit searchHit) {
        return this.dict.match(charArray, begin, length, searchHit);
    }

    public void init() {
        List<String> libPathList = getLibPath();
        for(String libPath : libPathList) {
            if(StringUtils.isBlank(libPath)) {
                continue;
            }
            List<String> wordsList = null;
            try {
                wordsList = loadFromFile(libPath);
            } catch (IOException e) {
                log.warn("读取词库异常:", e);
            } catch (Exception e) {
                log.warn("读取词库未知异常:", e);
            }
            if(CollectionUtils.isEmpty(wordsList)) {
                continue;
            }
            addWords(wordsList);
        }
    }

    public List<String> loadFromFile(String path) throws IOException {
        List<String> wordsList = new ArrayList();
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
                wordsList.add(line);
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
        return wordsList;
    }

    public void addWords(List<String> wordsList) {
        for(String s : wordsList) {
            dict.fillSegment(s.toCharArray());
        }
    }

    public abstract List<String> getLibPath();
}
