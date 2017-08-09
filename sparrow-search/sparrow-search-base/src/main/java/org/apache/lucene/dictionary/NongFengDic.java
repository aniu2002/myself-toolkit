package org.apache.lucene.dictionary;

import org.apache.lucene.dictionary.ik.IKDict;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yangtao on 2016/1/26.
 */
public class NongFengDic extends IKDict {
    private static final NongFengDic instance = new NongFengDic();

    public static NongFengDic getInstance() {
        return instance;
    }

    private NongFengDic() {

    }

    public void init() {
        log.info("初始化农丰网词库开始");
        super.init();
        log.info("初始化农丰网词库结束");
    }

    public List<String> getLibPath() {
//        Configuration config = new Configuration();
//        config.addResource("synonyms.xml");
        String path = System.getProperty("nong12.dicPath");
        return Arrays.asList(path.split(";", -1));
    }
}
