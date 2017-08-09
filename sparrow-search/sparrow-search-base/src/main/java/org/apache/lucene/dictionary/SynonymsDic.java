package org.apache.lucene.dictionary;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wltea.analyzer.dic.Dictionary;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by yangtao on 2015/12/11.
 */
public class SynonymsDic {
    private Log log = LogFactory.getLog(SynonymsDic.class);
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    //同义词映射
    private Map<String, String[]> dic = new HashMap();
    //删除的词
    private List<String> deleted = new LinkedList();

    private static final SynonymsDic instance = new SynonymsDic();

    public static SynonymsDic getInstance() {
        return instance;
    }

    private SynonymsDic() {}

    public String[] get(String keyword) {
        lock.readLock().lock();
        try {
            return dic.get(keyword);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void init() {
        log.info("初始化同义词开始");
        Map<String, String[]> synonyms = null;
        try {
            synonyms = loadFromFile();
        } catch (IOException e) {
            log.warn("读取同义词词库异常:", e);
        } catch (Exception e) {
            log.warn("读取同义词词库未知异常:", e);
        }
        if(synonyms == null || synonyms.isEmpty()) {
            return;
        }
        lock.writeLock().lock();
        try {
            dic = synonyms;
            addAnalyzerDic(dic.keySet());
            delAnalyzerDic(deleted);
        } finally {
            lock.writeLock().unlock();
        }
        log.info("初始化同义词结束");
    }

    public void reLoad() {
        init();
    }

    public Map<String, String[]> loadFromFile() throws IOException {
//        Configuration config = new Configuration();
//        config.addResource("synonyms.xml");
        String path = System.getProperty("synonyms.dicPath");
        return loadFromFile(path);
    }

    /**
     * 从文件中加载同义词
     * @param path
     * @return
     * @throws IOException
     */
    public Map<String, String[]> loadFromFile(String path) throws IOException {
        deleted.clear();
        Map<String, String[]> synonyms = new HashMap();
        String line = null;
        String[] values= null;
        BufferedReader br = null;
        InputStream in = null;
        String delimiter = ",";
        try {
            in = this.getClass().getClassLoader().getResourceAsStream(path);
            br = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
            while ((line = br.readLine()) != null) {
                if(StringUtils.isBlank(line)) {
                    continue;
                }
                if(line.trim().startsWith("#")) {
                    continue;
                }
                values = line.split(delimiter, -1);
                values = filter(values);
                for(String value : values) {
                    synonyms.put(value, values);
                }
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
        return synonyms;
    }

    public String[] filter(String[] values) {
        List<String> _values = new ArrayList(values.length);
        for(String value : values) {
            value = value.trim();
            if(value.charAt(0) == '-') {
                //已删除的词
                deleted.add(value.substring(1, value.length()));
            } else {
                _values.add(value);
            }
        }
        return _values.toArray(new String[0]);
    }

    /**
     * 向ikAnalyzer词库中新增指定词
     * @param words
     */
    public void addAnalyzerDic(Collection words) {
        org.wltea.analyzer.cfg.Configuration config = org.wltea.analyzer.cfg.DefaultConfig.getInstance();
        Dictionary.initial(config);
        Dictionary.getSingleton().addWords(words);
    }

    /**
     * 删除ikAnalyzer词库中的指定词
     * @param words
     */
    public void delAnalyzerDic(Collection words) {
        Dictionary.getSingleton().disableWords(words);
    }
}
